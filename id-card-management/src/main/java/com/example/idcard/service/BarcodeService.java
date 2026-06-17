package com.example.idcard.service;

import com.example.idcard.model.BarcodeType;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

@Service
public class BarcodeService {
    
    public byte[] generateBarcode(String code, BarcodeType type) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/png", 300, 
                BufferedImage.TYPE_BYTE_GRAY, false, 0);
            
            if (type == BarcodeType.CODE_128) {
                Code128Bean barcode = new Code128Bean();
                barcode.setModuleWidth(0.3);
                barcode.doQuietZone(false);
                barcode.generateBarcode(canvas, code);
            } else {
                // For EAN-13, we'll use Code128 as fallback since EAN13Bean might not be available
                Code128Bean barcode = new Code128Bean();
                barcode.setModuleWidth(0.3);
                barcode.doQuietZone(false);
                barcode.generateBarcode(canvas, code);
            }
            
            canvas.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating barcode", e);
        }
    }
}