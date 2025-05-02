package com.hospital.doctor.repository;
import com.hospital.doctor.dto.DoctorDto;
import com.hospital.doctor.dto.DoctorRequestDto;
import com.hospital.doctor.dto.DoctorResponseDto;
import com.hospital.doctor.entity.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    List<DoctorEntity> findBySpecialization(String specialization);

    List<DoctorEntity> findByName(String name);

    DoctorEntity findByRegistrationNumber(String registrationNumber);
    DoctorEntity findByEmail(String email);

    DoctorEntity findByUserId(@Param("id")Long id);

}
