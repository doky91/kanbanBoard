package com.kanbanBoard.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kanbanBoard.entity.Task;

public interface TaskService {

	public Page<Task> getAllTasks(String status, Pageable pageable);

	public Optional<Task> getTaskById(Long id);

	public Task createTask(Task task);

	public Task updateTask(Long id, Task updatedTask);

	public void deleteTask(Long id);

	public Task patchTask(Long id, Map<String, Object> updates);

	public Optional<Task> findById(Long id);

	public Task update(Task task);

}
