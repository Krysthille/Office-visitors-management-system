package model;

import java.time.LocalDateTime;

public class Visitor {
    private int id;
    private String logbookNumber;
    private LocalDateTime timestamp;
    private String lastName;
    private String firstName;
    private String middleName;
    private String province;
    private String municipality;
    private String barangay;
    private Integer age;
    private String gender;
    private String phoneNumber;
    private String email;
    private String sector;
    private String purpose;
    private String signaturePath;
    private String photoPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public Visitor() {}
    
    // Constructor with all fields
    public Visitor(String logbookNumber, LocalDateTime timestamp, String lastName, String firstName, 
                   String middleName, String province, String municipality, String barangay, 
                   Integer age, String gender, String phoneNumber, String email, String sector, String purpose) {
        this.logbookNumber = logbookNumber;
        this.timestamp = timestamp;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.province = province;
        this.municipality = municipality;
        this.barangay = barangay;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.sector = sector;
        this.purpose = purpose;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getLogbookNumber() { return logbookNumber; }
    public void setLogbookNumber(String logbookNumber) { this.logbookNumber = logbookNumber; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    
    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    
    public String getBarangay() { return barangay; }
    public void setBarangay(String barangay) { this.barangay = barangay; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    
    public String getSignaturePath() { return signaturePath; }
    public void setSignaturePath(String signaturePath) { this.signaturePath = signaturePath; }
    
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null) fullName.append(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        if (lastName != null) fullName.append(" ").append(lastName);
        return fullName.toString().trim();
    }
    
    public String getAddress() {
        StringBuilder address = new StringBuilder();
        if (barangay != null) address.append(barangay);
        if (municipality != null) address.append(", ").append(municipality);
        if (province != null) address.append(", ").append(province);
        return address.toString();
    }
    
    @Override
    public String toString() {
        return "Visitor{" +
                "id=" + id +
                ", logbookNumber='" + logbookNumber + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", municipality='" + municipality + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 