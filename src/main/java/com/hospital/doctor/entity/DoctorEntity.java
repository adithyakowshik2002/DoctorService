package com.hospital.doctor.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDateTime;

import java.util.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctors")
@EntityListeners(AuditingEntityListener.class)
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profile_image")
    private byte[] profileImage;

    @Column(name = "Doctor_name")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(name = "registration_number", nullable = false, unique = true)
    private UUID registrationNumber;

    @Column(nullable = false)
    private String specialization;

    @Column
    private String languages;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column
    private String location;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany
    @JsonManagedReference
    @Builder.Default
    private List<AvailableDateEntity> availableSchedules = new ArrayList<>();


}
