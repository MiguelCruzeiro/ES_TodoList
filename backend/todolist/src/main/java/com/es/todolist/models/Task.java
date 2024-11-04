package com.es.todolist.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "priority")
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("tasks")
    private AppUser user;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "category")
    private String category;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    public Task(Long id, String title, String description, boolean completed, Priority priority, AppUser user, LocalDateTime deadline) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.priority = priority;
        this.user = user;
        this.deadline = deadline;
    }
    
    
}
