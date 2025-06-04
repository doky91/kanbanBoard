package com.kanbanBoard.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.kanbanBoard.entity.Task;
import com.kanbanBoard.repository.PriorityRepository;
import com.kanbanBoard.repository.StatusRepository;
import com.kanbanBoard.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

	@InjectMocks
	private TaskServiceImpl taskService;

	@Mock
	private TaskRepository taskRepository;

	@Mock
	private StatusRepository statusRepository;

	@Mock
	private PriorityRepository priorityRepository;

	@Test
	void testGetAllTasks_withStatus() {
		Pageable pageable = PageRequest.of(0, 10);
		String status = "IN_PROGRESS";
		Page<Task> mockPage = new PageImpl<>(List.of(new Task()));

		when(taskRepository.findByStatus(status, pageable)).thenReturn(mockPage);

		Page<Task> result = taskService.getAllTasks(status, pageable);

		assertEquals(mockPage, result);
	}
	
	@Test
	void testUpdateTask_notFound() {
	    when(taskRepository.findById(1L)).thenReturn(Optional.empty());

	    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
	        taskService.updateTask(1L, new Task());
	    });

	    assertEquals("Task not found with ID: 1", ex.getMessage());
	}
	
	@Test
	void testPatchTask_statusNotFound() {
	    Task task = new Task();
	    Map<String, Object> updates = new HashMap<>();
	    updates.put("statusId", 999);

	    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
	    when(statusRepository.findById(999)).thenReturn(Optional.empty());

	    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
	        taskService.patchTask(1L, updates);
	    });

	    assertEquals("Invalid status ID: 999", ex.getMessage());
	}


}
