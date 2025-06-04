package com.kanbanBoard.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanbanBoard.entity.Task;
import com.kanbanBoard.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Operations on tasks")
public class TaskController {

	private final TaskService taskService;
	private final ObjectMapper objectMapper;

	public TaskController(TaskService taskService, ObjectMapper objectMapper) {
		this.taskService = taskService;
		this.objectMapper = objectMapper;
	}

	EntityModel<Task> toModel(Task task) {
		return EntityModel.of(task, linkTo(methodOn(TaskController.class).getTaskById(task.getId())).withSelfRel(),
				linkTo(methodOn(TaskController.class).updateTask(task.getId(), task)).withRel("update"),
				linkTo(methodOn(TaskController.class).deleteTask(task.getId())).withRel("delete"));
	}

	@GetMapping
	@Operation(summary = "Dohvati sve taskove",  tags = {"Tasks"})
	public ResponseEntity<PagedModel<EntityModel<Task>>> getAllTasks(@RequestParam(required = false) String status,
			@ParameterObject Pageable pageable) {

		Page<Task> taskPage = taskService.getAllTasks(status, pageable);

		List<EntityModel<Task>> taskResources = taskPage.stream().map(this::toModel).collect(Collectors.toList());

		PagedModel<EntityModel<Task>> pagedModel = PagedModel.of(taskResources, new PagedModel.PageMetadata(
				taskPage.getSize(), taskPage.getNumber(), taskPage.getTotalElements(), taskPage.getTotalPages()));

		pagedModel.add(linkTo(methodOn(TaskController.class).getAllTasks(status, pageable)).withSelfRel());

		return ResponseEntity.ok(pagedModel);
	}

	@GetMapping("/{id}")
    @Operation(summary = "Dohvati task po ID-ju", tags = {"Tasks"})
	public ResponseEntity<EntityModel<Task>> getTaskById(@PathVariable Long id) {
		return taskService.getTaskById(id).map(task -> ResponseEntity.ok(toModel(task)))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@Operation(summary = "Kreiraj task",  tags = {"Tasks"})
	public ResponseEntity<EntityModel<Task>> createTask(@RequestBody Task task) {
		Task created = taskService.createTask(task);
		EntityModel<Task> model = toModel(created);
		return ResponseEntity.created(linkTo(methodOn(TaskController.class).getTaskById(created.getId())).toUri())
				.body(model);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Ažuriraj task",  tags = {"Tasks"})
	public ResponseEntity<EntityModel<Task>> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
		try {
			Task task = taskService.updateTask(id, updatedTask);
			return ResponseEntity.ok(toModel(task));
		} catch (OptimisticLockingFailureException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (RuntimeException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Izbriši task",  tags = {"Tasks"})
	public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
		try {
			taskService.deleteTask(id);
			return ResponseEntity.noContent().build();
		} catch (Exception ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
	@Operation(summary = "Djelomično ažuriraj task",  tags = {"Tasks"})
	public ResponseEntity<EntityModel<Task>> patchTask(@PathVariable Long id, @RequestBody JsonNode patchJsonNode) {
		Task originalTask = taskService.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));

		Task patchedTask = applyMergePatch(patchJsonNode, originalTask);
		Task updatedTask = taskService.update(patchedTask);

		return ResponseEntity.ok(toModel(updatedTask));
	}

	private Task applyMergePatch(JsonNode patchNode, Task targetBean) {
		try {
			JsonNode targetNode = objectMapper.valueToTree(targetBean);
			JsonNode merged = objectMapper.readerForUpdating(targetNode).readValue(patchNode.toString());
			return objectMapper.treeToValue(merged, Task.class);
		} catch (Exception e) {
			throw new RuntimeException("Failed to apply JSON Merge Patch", e);
		}
	}
}
