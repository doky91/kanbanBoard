package com.kanbanBoard.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
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
        t.setTitle("Test Task " + id);
        t.setDescription("Description");
        return t;
    }
    
    
    @Test
    void getAllTasks_shouldReturnEmptyPagedModel_whenNoTasks() {
    	
        Pageable pageable = PageRequest.of(0, 10);
        when(taskService.getAllTasks(null, pageable)).thenReturn(Page.empty());

        TaskController spyController = Mockito.spy(taskController);
        doAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return EntityModel.of(task);
        }).when(spyController).toModel(any());

        ResponseEntity<PagedModel<EntityModel<Task>>> response = spyController.getAllTasks(null, pageable);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        PagedModel<EntityModel<Task>> body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getContent()).isEmpty();

        assertThat(body.getLink("self")).isPresent();
    }



    
    @Test
    void getTaskById_whenTaskExists_shouldReturnTaskModel() {
        Task task = sampleTask(1L);
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<EntityModel<Task>> response = taskController.getTaskById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent().getId()).isEqualTo(1L);
    }

    @Test
    void getTaskById_whenTaskNotFound_shouldReturn404() {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        ResponseEntity<EntityModel<Task>> response = taskController.getTaskById(1L);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createTask_shouldReturnCreatedTaskWithLocation() {
        Task taskToCreate = sampleTask(null);
        Task createdTask = sampleTask(1L);
        when(taskService.createTask(taskToCreate)).thenReturn(createdTask);

        ResponseEntity<EntityModel<Task>> response = taskController.createTask(taskToCreate);

        assertEquals(201, response.getStatusCode().value());
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody().getContent().getId()).isEqualTo(1L);
    }

    
    @Test
    void updateTask_whenSuccessful_shouldReturnUpdatedTask() {
        Task updated = sampleTask(1L);
        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updated);

        ResponseEntity<EntityModel<Task>> response = taskController.updateTask(1L, updated);

        assertEquals(200, response.getStatusCode().value());
        assertThat(response.getBody().getContent().getId()).isEqualTo(1L);
    }

    @Test
    void updateTask_whenOptimisticLockException_shouldReturnConflict() {
        when(taskService.updateTask(eq(1L), any(Task.class)))
                .thenThrow(new OptimisticLockingFailureException("conflict"));

        ResponseEntity<EntityModel<Task>> response = taskController.updateTask(1L, sampleTask(1L));

        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void updateTask_whenNotFound_shouldReturn404() {
        when(taskService.updateTask(eq(1L), any(Task.class)))
                .thenThrow(new RuntimeException("not found"));

        ResponseEntity<EntityModel<Task>> response = taskController.updateTask(1L, sampleTask(1L));

        assertEquals(404, response.getStatusCode().value());
    }

    
    @Test
    void deleteTask_whenSuccessful_shouldReturnNoContent() {
        doNothing().when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteTask_whenException_shouldReturn404() {
        doThrow(new RuntimeException()).when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(404, response.getStatusCode().value());
    }       

	@Test
	void patchTask_shouldApplyPatchAndReturnUpdated() throws Exception {
	    Task original = sampleTask(1L);
	    Task patched = sampleTask(1L);
	    patched.setTitle("Patched Title");

	    when(taskService.findById(1L)).thenReturn(Optional.of(original));
	    when(taskService.update(any(Task.class))).thenReturn(patched);

	    ObjectNode patchNode = objectMapper.createObjectNode();
	    patchNode.put("title", "Patched Title");

	    ResponseEntity<EntityModel<Task>> response = taskController.patchTask(1L, patchNode);

	    assertEquals(200, response.getStatusCode().value());
	    assertThat(response.getBody().getContent().getTitle()).isEqualTo("Patched Title");
	}
}
	
