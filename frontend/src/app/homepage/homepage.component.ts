import { Component, inject, OnInit } from '@angular/core';
import { ApiService } from '../service/api.service';
import { NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [ NgFor, FormsModule ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent implements OnInit {

  tasks: any[] = [];
  newTask = {
    title: '',
    description: '',
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
    if (this.newTask.title && this.newTask.description) {
      try {
        const createdTask = await this.ApiService.createTask(this.newTask);
        this.tasks.push(createdTask); // Add the newly created task to the list
        this.newTask = { "title": '', "description": '' }; // Reset the form
      } catch (error) {
        console.error('Error creating task:', error);
      }
    }
  }

  async deleteTask(taskId: string) {
    try {
      await this.ApiService.deleteTask(taskId);
      this.tasks = this.tasks.filter(task => task.id !== taskId); // Remove the deleted task from the list
    } catch (error) {
      console.error('Error deleting task:', error);
    }
  }

}
