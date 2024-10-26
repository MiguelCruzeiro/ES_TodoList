package com.es.todolist.repositories;

import org.springframework.stereotype.Repository;

import com.es.todolist.models.Task;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Iterable<Task> findByUserId(Long userId);

    Iterable<Task> findByUserSub(String sub);

}
