package com.example.idcard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "templates")
public class Template {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Template name is required")
    @Column(unique = true, nullable = false)
    private String name;
    
    @NotBlank(message = "Template content is required")
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @NotNull(message = "Template type is required")
    @Enumerated(EnumType.STRING)
    private TemplateType templateType;
    
    @Enumerated(EnumType.STRING)
    private BarcodeType barcodeType;
    
    private String description;
    private Boolean isActive = true;
    
    public enum TemplateType {
        PDF,
        HTML,
        BOTH
    }
    
    // ============ GETTERS AND SETTERS ============
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public TemplateType getTemplateType() {
        return templateType;
    }
    
    public void setTemplateType(TemplateType templateType) {
        this.templateType = templateType;
    }
    
    public BarcodeType getBarcodeType() {
        return barcodeType;
    }
    
    public void setBarcodeType(BarcodeType barcodeType) {
        this.barcodeType = barcodeType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}