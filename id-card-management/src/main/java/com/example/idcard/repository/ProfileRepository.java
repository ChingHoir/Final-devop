package com.example.idcard.repository;

import com.example.idcard.model.Profile;
import com.example.idcard.model.ProfileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    // Find by email (unique)
    Optional<Profile> findByEmail(String email);
    
    // Find by registration number (unique)
    Optional<Profile> findByRegistrationNumber(String registrationNumber);
    
    // Find by profile type
    List<Profile> findByProfileType(ProfileType profileType);
    
    // Search by full name containing keyword
    List<Profile> findByFullNameContainingIgnoreCase(String keyword);
    
    // Search by department
    List<Profile> findByDepartmentIgnoreCase(String department);
    
    // Check existence
    boolean existsByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);
    
    // Custom query for searching
    @Query("SELECT p FROM Profile p WHERE " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.registrationNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Profile> searchProfiles(@Param("keyword") String keyword);
    
    // Count by type
    long countByProfileType(ProfileType profileType);
    
    // Get latest registration number for sequence generation
    @Query("SELECT p.registrationNumber FROM Profile p ORDER BY p.id DESC LIMIT 1")
    String findLatestRegistrationNumber();
}