package com.hospital.doctor.repository;

import com.hospital.doctor.entity.AvailableScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AvailableScheduleRepository extends JpaRepository<AvailableScheduleEntity, Long> {

    @Query("SELECT s FROM AvailableScheduleEntity s " +
            "JOIN s.availableDate d " +
            "WHERE d.doctor.id = :doctorId AND d.availableDate = :availableDate")
    List<AvailableScheduleEntity> findByDoctorIdAndAvailableDate(
            @Param("doctorId") Long doctorId,
            @Param("availableDate") LocalDate availableDate
    );
}
