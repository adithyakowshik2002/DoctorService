package com.hospital.doctor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;


    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "available_date_id", nullable = false)
    private AvailableDateEntity availableDate;

    @Column
    private LocalTime availableFrom;

    @Column
    private LocalTime availableTo;

}
