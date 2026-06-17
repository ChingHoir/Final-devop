package com.example.idcard.service;

import com.example.idcard.model.Profile;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PDFService {

    // ID Card dimensions (credit card size in points: 85.6mm x 53.98mm ≈ 242.6pt x 153pt)
    private static final float CARD_WIDTH = 243f;
    private static final float CARD_HEIGHT = 153f;
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(79, 70, 229); // indigo
    private static final DeviceRgb TEXT_DARK = new DeviceRgb(31, 41, 55);
    private static final DeviceRgb TEXT_MUTED = new DeviceRgb(107, 114, 128);
    private static final float PAGE_MARGIN = 30f;

    public byte[] generateIDCardPDF(Profile profile) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Use a page size slightly larger than a single card for printing
        PageSize pageSize = new PageSize(CARD_WIDTH + 40, CARD_HEIGHT + 40);
        pdfDoc.setDefaultPageSize(pageSize);

        Document document = new Document(pdfDoc);
        document.setMargins(20, 20, 20, 20);

        // Draw the ID card
        drawIDCard(document, profile, 0, 0);

        document.close();
        return baos.toByteArray();
    }

    private void drawIDCard(Document document, Profile profile, float xOffset, float yOffset) {
        PdfDocument pdfDoc = document.getPdfDocument();
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage());

        // --- Draw card border/shadow ---
        canvas.saveState();

        // Shadow offset - draw with very light color for subtle shadow effect
        canvas.setFillColor(new DeviceRgb(220, 220, 230));
        canvas.roundRectangle(xOffset + 3, yOffset - 3, CARD_WIDTH, CARD_HEIGHT, 8);
        canvas.fill();

        // White card background
        canvas.setFillColor(ColorConstants.WHITE);
        canvas.roundRectangle(xOffset, yOffset, CARD_WIDTH, CARD_HEIGHT, 8);
        canvas.fill();

        // Card border
        canvas.setStrokeColor(new DeviceRgb(200, 200, 210));
        canvas.setLineWidth(1);
        canvas.roundRectangle(xOffset, yOffset, CARD_WIDTH, CARD_HEIGHT, 8);
        canvas.stroke();

        // --- Draw header banner ---
        float headerHeight = 44f;
        canvas.roundRectangle(xOffset, yOffset + CARD_HEIGHT - headerHeight, CARD_WIDTH, headerHeight, 8);
        canvas.clip();
        canvas.endPath();
        canvas.roundRectangle(xOffset, yOffset + CARD_HEIGHT - headerHeight, CARD_WIDTH, headerHeight, 8);
        canvas.setFillColor(HEADER_COLOR);
        canvas.fill();

        // Draw a second rectangle to fill the top corners
        canvas.roundRectangle(xOffset, yOffset + CARD_HEIGHT - headerHeight - 10, CARD_WIDTH, headerHeight + 10, 8);
        canvas.setFillColor(HEADER_COLOR);
        canvas.fill();

        canvas.restoreState();

        // Reset clipping
        canvas = new PdfCanvas(pdfDoc.getLastPage());

        // --- Header text ---
        float headerTextY = yOffset + CARD_HEIGHT - 14f;
        document.showTextAligned(
            new Paragraph("INSTITUTION ID CARD")
                .setFontSize(10)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER),
            xOffset + CARD_WIDTH / 2, headerTextY,
            TextAlignment.CENTER);

        // Registration number below header
        document.showTextAligned(
            new Paragraph("Reg: " + profile.getRegistrationNumber())
                .setFontSize(6.5f)
                .setFontColor(new DeviceRgb(220, 220, 240))
                .setTextAlignment(TextAlignment.CENTER),
            xOffset + CARD_WIDTH / 2, headerTextY - 12,
            TextAlignment.CENTER);

        // --- Photo section ---
        float photoX = xOffset + 12f;
        float photoY = yOffset + CARD_HEIGHT - headerHeight - 70f;
        float photoSize = 65f;

        if (profile.getPhoto() != null && profile.getPhoto().length > 0) {
            try {
                Image img = new Image(ImageDataFactory.create(profile.getPhoto()));
                img.scaleToFit(photoSize, photoSize);
                img.setFixedPosition(photoX, photoY);
                img.setWidth(photoSize);
                img.setHeight(photoSize);
                document.add(img);

                // Photo border
                canvas.setStrokeColor(new DeviceRgb(210, 210, 220));
                canvas.setLineWidth(1);
                canvas.rectangle(photoX, photoY, photoSize, photoSize);
                canvas.stroke();
            } catch (Exception e) {
                // Fallback if photo fails to render
                drawPhotoPlaceholder(canvas, photoX, photoY, photoSize);
            }
        } else {
            drawPhotoPlaceholder(canvas, photoX, photoY, photoSize);
        }

        // --- Info fields (right side of photo) ---
        float infoX = photoX + photoSize + 14f;
        float infoY = yOffset + CARD_HEIGHT - headerHeight - 16f;
        float lineHeight = 11f;

        addInfoLine(document, infoX, infoY, "NAME", profile.getFullName());
        addInfoLine(document, infoX, infoY - lineHeight, "EMAIL", profile.getEmail());
        addInfoLine(document, infoX, infoY - lineHeight * 2, "PHONE", profile.getPhone());
        addInfoLine(document, infoX, infoY - lineHeight * 3, "TYPE", profile.getProfileType() != null ? profile.getProfileType().name() : "-");
        addInfoLine(document, infoX, infoY - lineHeight * 4, "DEPT", profile.getDepartment() != null ? profile.getDepartment() : "-");
        addInfoLine(document, infoX, infoY - lineHeight * 5, "POSITION", profile.getPosition() != null ? profile.getPosition() : "-");

        // --- Address (below photo, full width) ---
        float addrY = yOffset + 18f;
        if (profile.getAddress() != null && !profile.getAddress().isEmpty()) {
            document.showTextAligned(
                new Paragraph(profile.getAddress())
                    .setFontSize(5.5f)
                    .setFontColor(TEXT_MUTED)
                    .setTextAlignment(TextAlignment.CENTER),
                xOffset + CARD_WIDTH / 2, addrY,
                TextAlignment.CENTER);
        }

        // --- Divider line ---
        canvas.setStrokeColor(new DeviceRgb(220, 220, 230));
        canvas.setLineWidth(0.5f);
        canvas.moveTo(xOffset + 12, yOffset + addrY + 8);
        canvas.lineTo(xOffset + CARD_WIDTH - 12, yOffset + addrY + 8);
        canvas.stroke();

        // --- Footer: Issue/Expiry dates ---
        String footerText = "Issued: " + (profile.getIssueDate() != null ? profile.getIssueDate().toString() : "-")
            + "  |  Expires: " + (profile.getExpiryDate() != null ? profile.getExpiryDate().toString() : "N/A");
        document.showTextAligned(
            new Paragraph(footerText)
                .setFontSize(5f)
                .setFontColor(TEXT_MUTED)
                .setTextAlignment(TextAlignment.CENTER),
            xOffset + CARD_WIDTH / 2, yOffset + 5f,
            TextAlignment.CENTER);
    }

    private void drawPhotoPlaceholder(PdfCanvas canvas, float x, float y, float size) {
        canvas.saveState();
        canvas.setFillColor(new DeviceRgb(243, 244, 246));
        canvas.rectangle(x, y, size, size);
        canvas.fill();

        canvas.setStrokeColor(new DeviceRgb(209, 213, 219));
        canvas.setLineWidth(1.5f);
        canvas.setLineDash(3, 3);
        canvas.rectangle(x, y, size, size);
        canvas.stroke();
        canvas.setLineDash(0);
        canvas.restoreState();
    }

    private void addInfoLine(Document document, float x, float y, String label, String value) {
        if (value == null) value = "-";

        // Label
        document.showTextAligned(
            new Paragraph(label)
                .setFontSize(5f)
                .setBold()
                .setFontColor(TEXT_MUTED)
                .setTextAlignment(TextAlignment.LEFT),
            x, y + 3f,
            TextAlignment.LEFT);

        // Value
        document.showTextAligned(
            new Paragraph(value)
                .setFontSize(7f)
                .setFontColor(TEXT_DARK)
                .setTextAlignment(TextAlignment.LEFT),
            x, y - 5f,
            TextAlignment.LEFT);
    }

    public byte[] generateBatchPDF(List<Profile> profiles) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Page size for multiple cards: A4 landscape
        PageSize pageSize = PageSize.A4.rotate();
        pdfDoc.setDefaultPageSize(pageSize);

        Document document = new Document(pdfDoc);
        document.setMargins(PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);

        // Cards per row and per column
        int cols = 3;
        int rows = 2;
        float cardsPerPage = cols * rows; // 6 cards per page

        float totalCardAreaWidth = cols * CARD_WIDTH + (cols - 1) * 15f;
        float totalCardAreaHeight = rows * CARD_HEIGHT + (rows - 1) * 15f;

        // Center cards on page
        float startX = (pageSize.getWidth() - totalCardAreaWidth) / 2;
        float startY = (pageSize.getHeight() + totalCardAreaHeight) / 2;

        int count = 0;
        for (Profile profile : profiles) {
            if (count > 0 && count % cardsPerPage == 0) {
                pdfDoc.addNewPage();
                startY = (pageSize.getHeight() + totalCardAreaHeight) / 2;
                startX = (pageSize.getWidth() - totalCardAreaWidth) / 2;
            }

            int cardIndex = count % (int) cardsPerPage;
            int col = cardIndex % cols;
            int row = cardIndex / cols;

            float x = startX + col * (CARD_WIDTH + 15f);
            float y = startY - row * (CARD_HEIGHT + 15f);

            drawIDCard(document, profile, x, y);
            count++;
        }

        document.close();
        return baos.toByteArray();
    }
}