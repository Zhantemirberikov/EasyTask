package com.example.lab6new.services;

import com.example.lab6new.models.Task;
import com.example.lab6new.models.User;
import com.example.lab6new.repositories.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Page<Task> getTasks(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUser(user, pageable);
    }

    public Page<Task> getTasksByCategory(User user, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUserAndCategoryId(user, categoryId, pageable);
    }

    public Page<Task> getTasksByKeyword(User user, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUserAndTitleContainingIgnoreCase(user, keyword, pageable);
    }

    public Page<Task> getTasksByCategoryAndKeyword(User user, Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findByUserAndCategoryIdAndTitleContainingIgnoreCase(user, categoryId, keyword, pageable);
    }

    public void saveTask(Task task, User user) {
        task.setUser(user);
        task.setCompleted(false);
        taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Задача не найдена"));
    }

    public void updateTask(Long id, Task updatedTask) {
        Task task = getTaskById(id);
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDueDate(updatedTask.getDueDate());
        task.setCategory(updatedTask.getCategory());
        taskRepository.save(task);
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }

    public void markTaskAsCompleted(Long id) {
        Task task = getTaskById(id);
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
    }
}
