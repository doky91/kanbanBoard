package com.kanbanBoard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "priority")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Priority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;  // LOW, MED, HIGH
}