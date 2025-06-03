package com.kanbanBoard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kanbanBoard.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Integer> {}

