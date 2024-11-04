import { Component, inject, OnInit } from '@angular/core';
import { ApiService } from '../service/api.service';
import { NgClass, NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [NgFor, FormsModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule, TitleCasePipe, NgClass, NgIf],
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {
  tasks: any[] = [];
  newTask = {
    title: '',
    description: '',
    priority: ''
  };

  ApiService = inject(ApiService);

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
    if (this.newTask.title && this.newTask.description && this.newTask.priority) {
      try {
        const createdTask = await this.ApiService.createTask(this.newTask);
        this.tasks.push(createdTask);
        this.newTask = { title: '', description: '', priority: '' };
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
  
}
