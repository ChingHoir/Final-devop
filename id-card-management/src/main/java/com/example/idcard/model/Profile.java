package com.example.idcard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "profiles")
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;
    
    @NotNull(message = "Profile type is required")
    @Enumerated(EnumType.STRING)
    private ProfileType profileType;
    
    @Column(unique = true, nullable = false)
    private String registrationNumber;
    
    private String department;
    private String position;
    private String address;
    
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;
    
    private String photoContentType;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (registrationNumber == null) {
            generateRegistrationNumber();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void generateRegistrationNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String deptCode = department != null ? department.substring(0, Math.min(3, department.length())).toUpperCase() : "GEN";
        String typeCode = profileType != null ? profileType.name().substring(0, 3) : "USR";
        Long count = 1L;
        this.registrationNumber = String.format("%s-%s-%s-%03d", year, deptCode, typeCode, count);
    }
    
    // ============ GETTERS AND SETTERS ============
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public ProfileType getProfileType() {
        return profileType;
    }
    
    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public byte[] getPhoto() {
        return photo;
    }
    
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    
    public String getPhotoContentType() {
        return photoContentType;
    }
    
    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getPhotoBase64() {
        if (photo != null) {
            return Base64.getEncoder().encodeToString(photo);
        }
        return null;
    }
}