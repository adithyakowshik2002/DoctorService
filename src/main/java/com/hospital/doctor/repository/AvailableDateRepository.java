package com.hospital.doctor.repository;

import com.hospital.doctor.entity.AvailableDateEntity;
import com.hospital.doctor.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableDateRepository extends JpaRepository<AvailableDateEntity, Long> {

    List<AvailableDateEntity> findByDoctorId(Long doctorId);


    List<AvailableDateEntity> findByDoctorIdAndAvailableDate(Long doctorId, LocalDate date);
}

