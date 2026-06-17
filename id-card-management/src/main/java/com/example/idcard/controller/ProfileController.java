package com.example.idcard.controller;

import com.example.idcard.model.Profile;
import com.example.idcard.model.ProfileType;
import com.example.idcard.service.ProfileService;
import com.example.idcard.service.QRCodeService;
import com.example.idcard.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;  // ADD THIS IMPORT

@Controller
@RequestMapping("/api/profiles")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private QRCodeService qrCodeService;
    
    @Autowired
    private PDFService pdfService;
    
    @GetMapping
    public String getAllProfiles(Model model) {
        model.addAttribute("profiles", profileService.getAllProfiles());
        return "profile-list";
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("profile", new Profile());
        model.addAttribute("profileTypes", ProfileType.values());
        return "profile-form";
    }
    
    @PostMapping("/create")
    public String createProfile(@Valid @ModelAttribute Profile profile, 
                               BindingResult result,
                               @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("profileTypes", ProfileType.values());
            return "profile-form";
        }
        
        try {
            if (profileService.existsByEmail(profile.getEmail())) {
                model.addAttribute("error", "Email already exists");
                model.addAttribute("profileTypes", ProfileType.values());
                return "profile-form";
            }
            profileService.createProfile(profile, photoFile);
            return "redirect:/api/profiles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("profileTypes", ProfileType.values());
            return "profile-form";
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Profile> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfileById(id));
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Profile profile = profileService.getProfileById(id);
        model.addAttribute("profile", profile);
        model.addAttribute("profileTypes", ProfileType.values());
        return "profile-form";
    }
    
    @PostMapping("/update/{id}")
    public String updateProfile(@PathVariable Long id,
                               @Valid @ModelAttribute Profile profile,
                               BindingResult result,
                               @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("profileTypes", ProfileType.values());
            return "profile-form";
        }
        
        try {
            profileService.updateProfile(id, profile, photoFile);
            return "redirect:/api/profiles";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("profileTypes", ProfileType.values());
            return "profile-form";
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
        return "redirect:/api/profiles";
    }
    
    @GetMapping("/search")
    public String searchProfiles(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("profiles", profileService.searchProfiles(keyword));
        return "profile-list";
    }
    
    @GetMapping("/preview/{id}")
    public String previewIDCard(@PathVariable Long id, Model model) {
        Profile profile = profileService.getProfileById(id);
        model.addAttribute("profile", profile);
        return "id-card-preview";
    }
    
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable Long id) {
        try {
            String verificationUrl = "https://yourdomain.com/verify/" + id;
            byte[] qrCode = qrCodeService.generateQRCode(verificationUrl, 200, 200);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> exportPDF(@PathVariable Long id) {
        try {
            Profile profile = profileService.getProfileById(id);
            byte[] pdfBytes = pdfService.generateIDCardPDF(profile);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=id-card-" + profile.getRegistrationNumber() + ".pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/batch-pdf")
    public ResponseEntity<byte[]> exportBatchPDF(@RequestBody List<Long> profileIds) {
        try {
            List<Profile> profiles = profileIds.stream()
                .map(profileService::getProfileById)
                .toList();
            byte[] pdfBytes = pdfService.generateBatchPDF(profiles);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=batch-id-cards.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}