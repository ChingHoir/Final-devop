package com.example.idcard.repository;

import com.example.idcard.model.Template;
import com.example.idcard.model.BarcodeType;
import com.example.idcard.model.Template.TemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    
    Optional<Template> findByName(String name);
    
    List<Template> findByTemplateType(TemplateType templateType);
    
    List<Template> findByIsActiveTrue();
    
    boolean existsByName(String name);
    
    List<Template> findByNameContainingIgnoreCase(String keyword);
    
    List<Template> findByBarcodeType(BarcodeType barcodeType);
}