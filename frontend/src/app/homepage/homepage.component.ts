import { Component, inject, OnInit } from '@angular/core';
import { ApiService } from '../service/api.service';
import { NgClass, NgFor, NgIf, TitleCasePipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog } from '@angular/material/dialog';
import { TaskDialogComponent } from "../task-dialog/task-dialog.component";
import { MatIconModule } from '@angular/material/icon';



@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [NgFor, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule, TitleCasePipe, NgClass, NgIf, MatIconModule, DatePipe],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {
  tasks: any[] = [];
  newTask = {
    title: '',
    description: '',
    category: '',
    priority: '',
    deadline: null
  };
  filteredTasks: any[] = [];
  sortOption: string = '';
  filterCategory: string = '';
  filterStatus: string = '';
  categories: string[] = [];

  ApiService = inject(ApiService);

  dialog = inject(MatDialog);

  ngOnInit(): void {
    this.loadTasks();
  }

  async loadTasks() {
    try {
      this.tasks = await this.ApiService.getTasks();
      this.filteredTasks = [...this.tasks];
      this.populateCategories();
      this.applyFiltersAndSorting();
    } catch (error) {
      console.error('Error fetching tasks:', error);
    }
  }

  populateCategories() {
    this.categories = [...new Set(this.tasks.map(task => task.category))];
  }

  applyFiltersAndSorting() {
    this.filteredTasks = this.tasks
      .filter(task => {
        // Filter by category, defaulting to show all if no category filter is applied
        const categoryMatch = !this.filterCategory || task.category === this.filterCategory;
  
        // Filter by completion status, ensure task.completed exists and matches the filter
        const statusMatch = 
          !this.filterStatus ||
          (this.filterStatus === 'completed' && task.completed === true) ||
          (this.filterStatus === 'incomplete' && task.completed === false);
  
        return categoryMatch && statusMatch;
      })
      .sort((a, b) => {
        if (this.sortOption === 'creationDate') {
          return new Date(a.creationDate).getTime() - new Date(b.creationDate).getTime();
        } else if (this.sortOption === 'deadline') {
          return new Date(a.deadline).getTime() - new Date(b.deadline).getTime();
        } else if (this.sortOption === 'completionStatus') {
          return (a.completed === b.completed) ? 0 : a.completed ? 1 : -1;
        }
        return 0;
      });
  }
  

  sortTasks() {
    this.applyFiltersAndSorting();
  }

  filterTasks() {
    console.log('Filtering tasks');
    this.applyFiltersAndSorting();
  }

  resetFilters() {
    this.filterCategory = '';
    this.filterStatus = '';
    this.sortOption = '';
    this.applyFiltersAndSorting();
  }


  async deleteTask(taskId: string) {
    try {
      await this.ApiService.deleteTask(taskId);
      this.tasks = this.tasks.filter(task => task.id !== taskId);
      this.populateCategories();
      this.applyFiltersAndSorting();
    } catch (error) {
      console.error('Error deleting task:', error);
    }
  }

  async markTaskAsCompleted(taskId: string) {
    try {
      await this.ApiService.markTaskAsCompleted(taskId);
      const task = this.tasks.find(task => task.id === taskId);
      if (task) task.completed = true; // Mark as completed in the local list
    } catch (error) {
      console.error('Error marking task as completed:', error);
    }
  }

  async editTask(taskId: string, task: any) {
    try {
      const updatedTask = await this.ApiService.editTask(taskId, task);
      const index = this.tasks.findIndex(task => task.id === taskId);
      if (index !== -1) this.tasks[index] = updatedTask;
      this.populateCategories();
      this.applyFiltersAndSorting();
    } catch (error) {
      console.error('Error editing task:', error);
    }
  }

  openTaskDialog(task?: any) {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      data: task ? { ...task } : null // Pass the task data if editing
    });
  
    dialogRef.afterClosed().subscribe(async (result) => {
      if (result) {
        if (task) {
          // Editing an existing task
          await this.editTask(task.id, result);
        } else {
          // Creating a new task
          try {
            const createdTask = await this.ApiService.createTask(result);
            this.tasks.push(createdTask);
            this.populateCategories();
            this.applyFiltersAndSorting();
          } catch (error) {
            console.error('Error creating task:', error);
          }
        }
      }
    });
  }
  

  
}
