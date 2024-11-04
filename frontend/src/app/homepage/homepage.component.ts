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

  ApiService = inject(ApiService);

  dialog = inject(MatDialog);

  ngOnInit(): void {
    this.loadTasks();
  }

  async loadTasks() {
    try {
      this.tasks = await this.ApiService.getTasks();
      console.log('Tasks:', this.tasks);
    } catch (error) {
      console.error('Error fetching tasks:', error);
    }
  }

  async addTask() {
    if (this.newTask.title && this.newTask.description && this.newTask.priority && this.newTask.category) {
      try {
        const createdTask = await this.ApiService.createTask(this.newTask);
        this.tasks.push(createdTask);
        this.newTask = { title: '', description: '',category: '', priority: '' , deadline: null};
      } catch (error) {
        console.error('Error creating task:', error);
      }
    }
  }

  async deleteTask(taskId: string) {
    try {
      await this.ApiService.deleteTask(taskId);
      this.tasks = this.tasks.filter(task => task.id !== taskId);
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

  openTaskDialog() {
    const dialogRef = this.dialog.open(TaskDialogComponent);

    dialogRef.afterClosed().subscribe(async (result) => {
      if (result) {
        try {
          const createdTask = await this.ApiService.createTask(result);
          this.tasks.push(createdTask);
        } catch (error) {
          console.error('Error creating task:', error);
        }
      }
    });
  }

  
}
