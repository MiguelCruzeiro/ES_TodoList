import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { DOCUMENT, isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  baseUrl = "https://es-ua.ddns.net:444/api";

  constructor(@Inject(PLATFORM_ID) private platformId: Object, @Inject(DOCUMENT) private document: Document) { }

  private getAuthToken(): string | null {
    if (isPlatformBrowser(this.platformId)) {
      return localStorage.getItem('token');
    } else {
      return null;
    }
  }

  public logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('token');
      this.document.location.href = '/';
    }
  }

  private getHeaders(withAuth: boolean = false): Headers {
    const headers = new Headers({
      'Content-Type': 'application/json',
    });
    if (withAuth) {
      const token = this.getAuthToken();
      if (token) {
        headers.append('Authorization', `Bearer ${token}`);
      }
    }
    return headers;
  }

  async gettoken(code: string) {
    // Construct the GET request URL with the code parameter
    const url = `https://es-ua.ddns.net:444/api/token/get?code=${code}`;

    const headers = new Headers({
      'Content-Type': 'text/plain',
    });

    // Make the GET request
    const response = await fetch(url, {
      method: 'GET',
      headers,
    });

    const data = await response.json();
    console.log('Token:', data.id_token);
    
    return data;
  }

  async getTasks() {
    const url = `${this.baseUrl}/tasks`;
    const response = await fetch(url, {
      method: 'GET',
      headers: this.getHeaders(true)
    });
    return await response.json() ?? [];
  }

  async createTask(task: any) {
    const url = `${this.baseUrl}/tasks`;
    const response = await fetch(url, {
      method: 'POST',
      headers: this.getHeaders(true),
      body: JSON.stringify(task)
    });
    return await response.json() ?? undefined;
  }

  async deleteTask(taskId: string) {
    const url = `${this.baseUrl}/tasks/${taskId}`;
    const response = await fetch(url, {
      method: 'DELETE',
      headers: this.getHeaders(true)
    });
    return await response.status === 204;
  }

  async markTaskAsCompleted(taskId: string) {
    const url = `${this.baseUrl}/tasks/completed/${taskId}`;
    const response = await fetch(url, {
      method: 'PUT',
      headers: this.getHeaders(true)
    });
    return await response.json() ?? undefined;
  }

  async editTask(taskId: string, task: any) {
    const url = `${this.baseUrl}/tasks/${taskId}`;
    const response = await fetch(url, {
      method: 'PUT',
      headers: this.getHeaders(true),
      body: JSON.stringify(task)
    });
    return await response.json() ?? undefined;
  }


}
