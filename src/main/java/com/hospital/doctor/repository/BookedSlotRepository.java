package com.hospital.doctor.repository;

import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookedSlotRepository extends JpaRepository<BookedSlotEntity, Long> {
    List<BookedSlotEntity> findByDoctorEntityAndSlotDate(DoctorEntity doctorEntity, LocalDate slotDate);
    void deleteByDoctorEntityAndSlotDate(DoctorEntity doctor, LocalDate slotDate);
    // Optional: get booked slots that are actually marked as booked (for frontend/status use)
    List<BookedSlotEntity> findByDoctorEntityAndSlotDateAndIsBookedTrue(DoctorEntity doctorEntity, LocalDate slotDate);

    List<BookedSlotEntity> findByDoctorEntityAndSlotDateAndIsBookedFalseOrderBySlotStartTime(
            DoctorEntity doctorEntity, LocalDate slotDate);


    List<BookedSlotEntity> findByDoctorEntityAndSlotDateAndIsBookedTrueOrderBySlotStartTime(
            DoctorEntity doctorEntity, LocalDate slotDate);
    // Optional: check for duplicate slot time
    boolean existsByDoctorEntityAndSlotDateAndSlotStartTimeAndSlotEndTime(
            DoctorEntity doctorEntity, LocalDate slotDate,
            java.time.LocalTime slotStartTime, java.time.LocalTime slotEndTime
    );
}
