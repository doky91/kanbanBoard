package com.kanbanBoard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kanbanBoard.entity.Task;
import com.kanbanBoard.service.TaskService;

public class TaskControllerTest {

	@Mock
	private TaskService taskService;


	@Spy
	private ObjectMapper objectMapper = new ObjectMapper();

	@InjectMocks
	private TaskController taskController;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	private Task sampleTask(Long id) {
		Task t = new Task();
		t.setId(id);
		t.setTitle("Task 1 " + id);
		t.setDescription("Description");
		return t;
	}


	@Test
	void getTaskByIdSucessfully() {
		Task task = sampleTask(1L);
		when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

		ResponseEntity<EntityModel<Task>> response = taskController.getTaskById(1L);

		assertEquals(200, response.getStatusCode().value());
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getContent().getId()).isEqualTo(1L);
	}

	@Test
	void getTaskByIdError() {
		when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

		ResponseEntity<EntityModel<Task>> response = taskController.getTaskById(1L);

		assertEquals(404, response.getStatusCode().value());
	}

	@Test
	void createTaskSucessfully() {
		Task taskToCreate = sampleTask(null);
		Task createdTask = sampleTask(1L);
		when(taskService.createTask(taskToCreate)).thenReturn(createdTask);

		ResponseEntity<EntityModel<Task>> response = taskController.createTask(taskToCreate);

		assertEquals(201, response.getStatusCode().value());
		assertThat(response.getHeaders().getLocation()).isNotNull();
		assertThat(response.getBody().getContent().getId()).isEqualTo(1L);
	}

	@Test
	void updatetaskSucessfully() {
		Task updated = sampleTask(1L);
		when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updated);

		ResponseEntity<EntityModel<Task>> response = taskController.updateTask(1L, updated);

		assertEquals(200, response.getStatusCode().value());
		assertThat(response.getBody().getContent().getId()).isEqualTo(1L);
	}

	@Test
	void updateTaskGetConflictError() {
		when(taskService.updateTask(eq(1L), any(Task.class)))
				.thenThrow(new OptimisticLockingFailureException("conflict"));

		ResponseEntity<EntityModel<Task>> response = taskController.updateTask(1L, sampleTask(1L));

		assertEquals(409, response.getStatusCode().value());
	}

	@Test
	void updateTaskGetNotFoundError() {
		when(taskService.updateTask(eq(1L), any(Task.class))).thenThrow(new RuntimeException("not found"));

		ResponseEntity<EntityModel<Task>> response = taskController.updateTask(1L, sampleTask(1L));

		assertEquals(404, response.getStatusCode().value());
	}

	@Test
	void deleteTaskSucessfully() {
		doNothing().when(taskService).deleteTask(1L);

		ResponseEntity<Void> response = taskController.deleteTask(1L);

		assertEquals(204, response.getStatusCode().value());
		verify(taskService).deleteTask(1L);
	}

	@Test
	void deleteTaskErrorNotFound() {
		doThrow(new RuntimeException()).when(taskService).deleteTask(1L);

		ResponseEntity<Void> response = taskController.deleteTask(1L);

		assertEquals(404, response.getStatusCode().value());
	}

	@Test
	void patchTaskSucessfully() throws Exception {
		Task original = sampleTask(1L);
		Task patched = sampleTask(1L);
		patched.setTitle("New title");

		when(taskService.findById(1L)).thenReturn(Optional.of(original));
		when(taskService.update(any(Task.class))).thenReturn(patched);

		ObjectNode patchNode = objectMapper.createObjectNode();
		patchNode.put("title", "New title");

		ResponseEntity<EntityModel<Task>> response = taskController.patchTask(1L, patchNode);

		assertEquals(200, response.getStatusCode().value());
		assertThat(response.getBody().getContent().getTitle()).isEqualTo("New title");
	}
}
