import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../service/api.service';
import { DOCUMENT, isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-callback',
  standalone: true,
  template: '<p>Processing login...</p>',
})
export class CallbackComponent implements OnInit {
  private isTokenFetched = false; // Flag to prevent double API call
  private token: string = '';
  private userSub: string[] = [];

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private apiService: ApiService,
    @Inject(PLATFORM_ID) private platformId: Object,
    @Inject(DOCUMENT) private document: Document
  ) {
  }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const code = params['code']; 
      if (code && !this.isTokenFetched) { // Check the flag to prevent multiple calls
        console.log('Received code:', code);
        this.isTokenFetched = true; // Set flag to true to prevent multiple API calls

        this.apiService.gettoken(code)
          .then(response => {
            console.log('response:', response);

            this.userSub = response.username;
            console.log('UserSub:', this.userSub);
            // Parse the token string into a JSON object
            const tokenObject = JSON.parse(response.token);

            this.token = tokenObject.id_token; // Use the parsed token object
            console.log('Is platform browser:', isPlatformBrowser(this.platformId));
            
            const localStorage = this.document.defaultView?.localStorage;
            if (localStorage) {
              localStorage.setItem('token', this.token); // Save the id_token
              console.log('Token saved in localStorage');
              console.log('Token:', this.token);
            } else {
              console.warn('localStorage is not available. Token cannot be saved.');
            }
          })
          .catch(error => {
            console.error('Error during token retrieval:', error);
          });

      } else if (!code) {
        console.error('No code found in redirect');
        this.router.navigate(['/']);
      }
    });
    
  }
}
