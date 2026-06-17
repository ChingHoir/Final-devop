package com.example.idcard.dto;

import com.example.idcard.model.ProfileType;
import java.time.LocalDate;

public class ProfileDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String idNumber;
    private LocalDate dateOfBirth;
    private ProfileType profileType;
    private String additionalInfo;
    private Long templateId;
    private boolean active;

    public ProfileDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public ProfileType getProfileType() { return profileType; }
    public void setProfileType(ProfileType profileType) { this.profileType = profileType; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}