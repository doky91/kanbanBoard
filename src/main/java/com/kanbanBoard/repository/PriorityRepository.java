package com.kanbanBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanbanBoard.entity.Priority;

public interface PriorityRepository extends JpaRepository<Priority, Integer> {}
