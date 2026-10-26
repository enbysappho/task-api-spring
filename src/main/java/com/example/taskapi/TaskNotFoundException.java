package com.example.taskapi;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("task " + id + " not found");
    }
}
