package com.example.lab6new.controllers;

import com.example.lab6new.models.Task;
import com.example.lab6new.models.User;
import com.example.lab6new.repositories.CategoryRepository;
import com.example.lab6new.services.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CategoryRepository categoryRepository;

    private static final String IMAGE_UPLOAD_DIR = "src/main/resources/static/images";

    public TaskController(TaskService taskService, CategoryRepository categoryRepository) {
        this.taskService = taskService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String listTasks(@AuthenticationPrincipal User user,
                            @RequestParam Optional<Long> categoryId,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        Page<Task> tasks;

        if (categoryId.isPresent() && !keyword.isEmpty()) {
            tasks = taskService.getTasksByCategoryAndKeyword(user, categoryId.get(), keyword, page, size);
        } else if (categoryId.isPresent()) {
            tasks = taskService.getTasksByCategory(user, categoryId.get(), page, size);
        } else if (!keyword.isEmpty()) {
            tasks = taskService.getTasksByKeyword(user, keyword, page, size);
        } else {
            tasks = taskService.getTasks(user, page, size);
        }

        model.addAttribute("tasks", tasks.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tasks.getTotalPages());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId.orElse(null));
        model.addAttribute("keyword", keyword);

        return "task-list";
    }

    @GetMapping("/add")
    public String addTaskForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("categories", categoryRepository.findAll());
        return "task-form";
    }

    @PostMapping("/add")
    public String saveTask(@ModelAttribute Task task,
                           @RequestParam("image") MultipartFile file,
                           @AuthenticationPrincipal User user) throws IOException {
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(IMAGE_UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());
            task.setImageUrl("/images/" + fileName);
        }
        taskService.saveTask(task, user);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable Long id, Model model) {
        model.addAttribute("task", taskService.getTaskById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "task-form";
    }

    @PostMapping("/edit/{id}")
    public String updateTask(@PathVariable Long id,
                             @ModelAttribute Task task,
                             @RequestParam("image") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(IMAGE_UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());
            task.setImageUrl("/images/" + fileName);
        }
        taskService.updateTask(id, task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return "redirect:/tasks";
    }

    @PostMapping("/complete/{id}")
    public String completeTask(@PathVariable Long id) {
        taskService.markTaskAsCompleted(id);
        return "redirect:/tasks";
    }

    @GetMapping("/back")
    public String backToMainPage() {
        return "redirect:/tasks";
    }
}
