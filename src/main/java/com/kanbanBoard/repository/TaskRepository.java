package com.kanbanBoard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kanbanBoard.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

	void deleteById(Integer id);

	Optional<Task> findById(Integer id);

	Page<Task> findByStatus(String status, Pageable pageable);
}
