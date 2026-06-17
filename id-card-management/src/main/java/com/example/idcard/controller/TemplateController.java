package com.example.idcard.controller;

import com.example.idcard.model.Template;
import com.example.idcard.model.BarcodeType;
import com.example.idcard.model.Template.TemplateType;
import com.example.idcard.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/templates")
public class TemplateController {
    
    @Autowired
    private TemplateRepository templateRepository;
    
    @GetMapping
    public String getAllTemplates(Model model) {
        model.addAttribute("templates", templateRepository.findAll());
        return "template-list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("template", new Template());
        model.addAttribute("templateTypes", TemplateType.values());
        model.addAttribute("barcodeTypes", BarcodeType.values());
        return "template-form";
    }
    
    @PostMapping("/create")
    public String createTemplate(@Valid @ModelAttribute Template template) {
        templateRepository.save(template);
        return "redirect:/api/templates";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Template> getTemplate(@PathVariable Long id) {
        return templateRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public String searchTemplates(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("templates", templateRepository.findByNameContainingIgnoreCase(keyword));
        return "template-list";
    }
    
    @GetMapping("/active")
    @ResponseBody
    public List<Template> getActiveTemplates() {
        return templateRepository.findByIsActiveTrue();
    }
}