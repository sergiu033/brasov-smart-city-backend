package com.smartcity.user_vehicles.entity;

import com.smartcity.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plate_number", nullable = false, columnDefinition = "VARCHAR(50)")
    private String plateNumber;
}
