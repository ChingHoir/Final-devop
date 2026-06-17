package com.example.idcard.service;

import com.example.idcard.model.Profile;
import com.example.idcard.model.ProfileType;
import com.example.idcard.model.ProfileBuilder;
import com.example.idcard.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProfileService {
    
    @Autowired
    private ProfileRepository profileRepository;
    
    // Create profile with photo
    public Profile createProfile(Profile profile, MultipartFile photoFile) throws IOException {
        if (photoFile != null && !photoFile.isEmpty()) {
            validatePhoto(photoFile);
            profile.setPhoto(photoFile.getBytes());
            profile.setPhotoContentType(photoFile.getContentType());
        }
        profile.generateRegistrationNumber();
        return profileRepository.save(profile);
    }
    
    // Create profile without photo
    public Profile createProfile(Profile profile) {
        profile.generateRegistrationNumber();
        return profileRepository.save(profile);
    }
    
    // Update profile
    public Profile updateProfile(Long id, Profile profileDetails, MultipartFile photoFile) throws IOException {
        Profile existingProfile = profileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
        
        existingProfile.setFullName(profileDetails.getFullName());
        existingProfile.setEmail(profileDetails.getEmail());
        existingProfile.setPhone(profileDetails.getPhone());
        existingProfile.setProfileType(profileDetails.getProfileType());
        existingProfile.setDepartment(profileDetails.getDepartment());
        existingProfile.setPosition(profileDetails.getPosition());
        existingProfile.setAddress(profileDetails.getAddress());
        
        if (photoFile != null && !photoFile.isEmpty()) {
            validatePhoto(photoFile);
            existingProfile.setPhoto(photoFile.getBytes());
            existingProfile.setPhotoContentType(photoFile.getContentType());
        }
        
        return profileRepository.save(existingProfile);
    }
    
    // Delete profile
    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }
    
    // Get all profiles
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
    
    // Get profile by ID
    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
    
    // Get profile by email
    public Profile getProfileByEmail(String email) {
        return profileRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
    
    // Get profiles by type
    public List<Profile> getProfilesByType(ProfileType type) {
        return profileRepository.findByProfileType(type);
    }
    
    // Search profiles
    public List<Profile> searchProfiles(String keyword) {
        return profileRepository.searchProfiles(keyword);
    }
    
    // Check existence
    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }
    
    public boolean existsByRegistrationNumber(String regNumber) {
        return profileRepository.existsByRegistrationNumber(regNumber);
    }
    
    // Validate photo
    private void validatePhoto(MultipartFile file) {
        String contentType = file.getContentType();
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
            throw new IllegalArgumentException("Only JPEG and PNG images are allowed");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("File size must be less than 5MB");
        }
    }
    
    // Build profile using builder
    public Profile buildProfileWithBuilder(String fullName, String email, String phone, 
                                          ProfileType type, String department, String position) {
        return new ProfileBuilder()
            .withFullName(fullName)
            .withEmail(email)
            .withPhone(phone)
            .withProfileType(type)
            .withDepartment(department)
            .withPosition(position)
            .build();
    }
}