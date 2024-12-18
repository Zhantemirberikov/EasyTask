package com.example.lab6new.repositories;

import com.example.lab6new.models.Task;
import com.example.lab6new.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUser(User user, Pageable pageable);

    Page<Task> findByUserAndCategoryId(User user, Long categoryId, Pageable pageable);

    Page<Task> findByUserAndTitleContainingIgnoreCase(User user, String keyword, Pageable pageable);

    Page<Task> findByUserAndCategoryIdAndTitleContainingIgnoreCase(User user, Long categoryId, String keyword, Pageable pageable);
}
