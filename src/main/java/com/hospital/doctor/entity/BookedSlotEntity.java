package com.hospital.doctor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BookedSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookedSlotId;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @JsonBackReference
    private AvailableScheduleEntity availableScheduleEntity;

    @Column(nullable = false)
    private LocalDate slotDate;

    @Column(nullable = false)
    private LocalTime slotStartTime;

    @Column(nullable = false)
    private LocalTime slotEndTime;

    @Column
    private Long patientId;

    @Column(unique = true)
    @NonNull
    private String status;


}
