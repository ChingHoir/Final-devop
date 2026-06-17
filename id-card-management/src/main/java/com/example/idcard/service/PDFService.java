package com.example.idcard.service;

import com.example.idcard.model.Profile;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
@Service
public class PDFService {
    
    public byte[] generateIDCardPDF(Profile profile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        // Add content
        document.add(new Paragraph("ID CARD"));
        document.add(new Paragraph("Registration: " + profile.getRegistrationNumber()));
        document.add(new Paragraph("Name: " + profile.getFullName()));
        document.add(new Paragraph("Email: " + profile.getEmail()));
        document.add(new Paragraph("Phone: " + profile.getPhone()));
        document.add(new Paragraph("Type: " + profile.getProfileType()));
        document.add(new Paragraph("Department: " + profile.getDepartment()));
        
        // Add photo if exists
        if (profile.getPhoto() != null) {
            Image img = new Image(ImageDataFactory.create(profile.getPhoto()));
            img.setWidth(100);
            img.setHeight(100);
            document.add(img);
        }
        
        document.close();
        return baos.toByteArray();
    }
    
    public byte[] generateBatchPDF(List<Profile> profiles) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        document.add(new Paragraph("BATCH ID CARDS"));
        document.add(new Paragraph("Total: " + profiles.size()));
        document.add(new Paragraph("-------------------"));
        
        for (Profile profile : profiles) {
            document.add(new Paragraph(profile.getRegistrationNumber() + " - " + profile.getFullName()));
            document.add(new Paragraph("-------------------"));
        }
        
        document.close();
        return baos.toByteArray();
    }
}