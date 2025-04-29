package com.hospital.doctor.repository;

import com.hospital.doctor.entity.AvailableScheduleEntity;
import com.hospital.doctor.entity.BookedSlotEntity;
import com.hospital.doctor.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface BookedSlotRepository extends JpaRepository<BookedSlotEntity, Long> {

    @Query("SELECT b FROM BookedSlotEntity b " +
            "JOIN b.availableScheduleEntity s " +
            "JOIN s.availableDate d " +
            "JOIN d.doctor doc " +
            "WHERE doc.id = :doctorId AND b.slotDate = :date")
    List<BookedSlotEntity> findByDoctorIdAndSlotDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);




    boolean existsByAvailableScheduleEntityAndSlotDateAndSlotStartTimeLessThanAndSlotEndTimeGreaterThan(
            AvailableScheduleEntity schedule,
            LocalDate slotDate,
            LocalTime slotEndTime,
            LocalTime slotStartTime
    );

}
