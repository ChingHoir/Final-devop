package com.example.idcard.model;

public class ProfileBuilder {
    private Profile profile;
    
    public ProfileBuilder() {
        this.profile = new Profile();
    }
    
    public ProfileBuilder withFullName(String fullName) {
        profile.setFullName(fullName);
        return this;
    }
    
    public ProfileBuilder withEmail(String email) {
        profile.setEmail(email);
        return this;
    }
    
    public ProfileBuilder withPhone(String phone) {
        profile.setPhone(phone);
        return this;
    }
    
    public ProfileBuilder withProfileType(ProfileType profileType) {
        profile.setProfileType(profileType);
        return this;
    }
    
    public ProfileBuilder withDepartment(String department) {
        profile.setDepartment(department);
        return this;
    }
    
    public ProfileBuilder withPosition(String position) {
        profile.setPosition(position);
        return this;
    }
    
    public ProfileBuilder withAddress(String address) {
        profile.setAddress(address);
        return this;
    }
    
    public ProfileBuilder withPhoto(byte[] photo, String contentType) {
        profile.setPhoto(photo);
        profile.setPhotoContentType(contentType);
        return this;
    }
    
    public Profile build() {
        return profile;
    }
    
    public static Profile createDefaultProfile() {
        return new ProfileBuilder()
            .withFullName("Default User")
            .withEmail("default@example.com")
            .withPhone("+1234567890")
            .withProfileType(ProfileType.USER)
            .withDepartment("General")
            .withPosition("Member")
            .withAddress("Default Address")
            .build();
    }
}