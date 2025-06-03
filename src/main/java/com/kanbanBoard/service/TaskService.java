package com.kanbanBoard.service;

import com.kanbanBoard.entity.Task;
import com.kanbanBoard.entity.Status;
import com.kanbanBoard.entity.Priority;
import com.kanbanBoard.repository.TaskRepository;
import com.kanbanBoard.repository.StatusRepository;
import com.kanbanBoard.repository.PriorityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private PriorityRepository priorityRepository;

 
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    // PUT (Full Update)
    public Task updateTask(Integer id, Task updatedTask) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());
        task.setUpdatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }

    // PATCH (Partial Update)
    public Task patchTask(Integer id, Map<String, Object> updates) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> task.setTitle((String) value);
                case "description" -> task.setDescription((String) value);
                case "statusId" -> {
                    Integer statusId = (Integer) value;
                    Status status = statusRepository.findById(statusId)
                            .orElseThrow(() -> new RuntimeException("Invalid status ID: " + statusId));
                    task.setStatus(status);
                }
                case "priorityId" -> {
                    Integer priorityId = (Integer) value;
                    Priority priority = priorityRepository.findById(priorityId)
                            .orElseThrow(() -> new RuntimeException("Invalid priority ID: " + priorityId));
                    task.setPriority(priority);
                }
            }
        });

        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
}
