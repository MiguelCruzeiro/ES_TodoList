package com.es.todolist.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.es.todolist.models.AppUser;
import com.es.todolist.models.Task;
import com.es.todolist.models.Priority;
import com.es.todolist.services.TaskService;

import jakarta.servlet.ServletException;

import com.es.todolist.configuration.CustomUserDetails;
import com.es.todolist.configuration.JwtAuthFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.io.IOException;
import java.util.Collections;
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private CustomUserDetails customUserDetails;

    private Task task;
    private AppUser appUser;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        Mockito.doNothing().when(jwtAuthFilter).doFilterInternal(Mockito.any(), Mockito.any(), Mockito.any());
        // Setting up the user
        appUser = new AppUser(1L, "testUser", "testSub", "test@example.com", null);
        customUserDetails = new CustomUserDetails(appUser.getUsername(), appUser.getSub(), appUser.getEmail());

        // Setting up the task
        task = new Task(1L, "Sample Task", "This is a sample task description", false, Priority.HIGH, appUser);
    }

    @Test
    void createTask_ShouldReturnCreatedStatus() throws Exception {
        when(taskService.save(any(Task.class), anyString(), anyString(), anyString())).thenReturn(task);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetails, null));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Sample Task\", \"description\": \"This is a sample task description\", \"completed\": false, \"priority\": \"HIGH\"}")
                .with(user(customUserDetails)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sample Task"))
                .andExpect(jsonPath("$.description").value("This is a sample task description"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

   @Test
    void getTasks_ShouldReturnListOfTasks() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetails, null));
        when(taskService.findByUserSub(anyString())).thenReturn(Collections.singletonList(task));
        

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tasks")
                .with(user(customUserDetails)))
                .andDo(MockMvcResultHandlers.print()) // Print the request and response details
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sample Task"))
                .andExpect(jsonPath("$[0].description").value("This is a sample task description"))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));
    }
    

    @Test
    void deleteTask_ShouldReturnNotFoundStatusIfTaskDoesNotExist() throws Exception {
        when(taskService.findById(1L)).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetails, null));


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/{id}", 1L)
                .with(user(customUserDetails)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_ShouldReturnForbiddenStatusIfUserNotAuthorized() throws Exception {
        Task anotherTask = new Task(2L, "Other Task", "Other task description", false, Priority.LOW, new AppUser(2L, "otherUser", "otherSub", "other@example.com", null));

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetails, null));

        when(taskService.findById(2L)).thenReturn(anotherTask);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tasks/{id}", 2L)
                .with(user(customUserDetails)))
                .andExpect(status().isForbidden());
    }
}
