package com.hospital.doctor.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

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
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private DoctorEntity doctorEntity;

    @Column(nullable = false)
    private LocalDate slotDate;

    @Column(nullable = false)
    private LocalTime slotStartTime;

    @Column(nullable = false)
    private LocalTime slotEndTime;

    @Column
    private boolean isBooked; //


}
