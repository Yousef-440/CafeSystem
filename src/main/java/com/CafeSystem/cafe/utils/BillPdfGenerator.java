package com.CafeSystem.cafe.utils;

import com.CafeSystem.cafe.dto.bill.ProductDetailsDTO;
import com.CafeSystem.cafe.exception.HandleException;
import com.CafeSystem.cafe.model.Bill;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Component
public class BillPdfGenerator {
    @Autowired
    private ObjectMapper objectMapper;

    public byte[] generatePdf(Bill bill){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            FontFactory.register("src/main/resources/fonts/MySoul-Regular.ttf", "Tajawal");
            Font titleFont = FontFactory.getFont("Tajawal", BaseFont.WINANSI, BaseFont.EMBEDDED, 35);
            titleFont.setColor(BaseColor.DARK_GRAY);

            Chunk chunk = new Chunk("Cafe System", titleFont);
            chunk.setCharacterSpacing(2f);
            Paragraph title = new Paragraph( );
            title.add(chunk);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(21);
            document.add(title);

            LineSeparator line = new LineSeparator();
            line.setLineColor(BaseColor.LIGHT_GRAY);
            line.setPercentage(80);
            line.setAlignment(Element.ALIGN_CENTER);
            document.add(line);


            Font subDataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.ITALIC);
            subDataFont.setColor(BaseColor.DARK_GRAY);

            Paragraph subData = new Paragraph();
            subData.setLeading(20f);
            subData.setSpacingBefore(23f);
            subData.setSpacingAfter(40f);
            String space = "         |         ";
            subData.add(new Chunk("Name: " + bill.getName() + space, subDataFont));
            subData.add(new Chunk("ContactNumber: " + bill.getContactNumber() + space, subDataFont));
            subData.add(new Chunk("PaymentMethod: " + bill.getPaymentMethod(), subDataFont));
            document.add(subData);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingAfter(20f);
            table.setWidths(new float[] {3f, 2f, 1.5f, 2f, 2f});

            BaseColor headerColor = new BaseColor(33, 37, 41);
            BaseColor evenRowColor = new BaseColor(245, 245, 245);
            BaseColor oddRowColor = BaseColor.WHITE;
            BaseColor borderColor = new BaseColor(102, 102, 102);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 13, Font.BOLD, BaseColor.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, BaseColor.DARK_GRAY);

            Stream.of("Product", "Category", "Quantity", "Price", "Total")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell(new Phrase(columnTitle, headerFont));
                        header.setBackgroundColor(headerColor);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPadding(10f);
                        header.setBorderColor(borderColor);
                        table.addCell(header);
                    });

            List<ProductDetailsDTO> products = objectMapper.readValue(
                    bill.getProductDetails(),
                    new TypeReference<List<ProductDetailsDTO>>() {}
            );

            boolean alternate = false;
            Double sumTotal = 0.0;
            for (ProductDetailsDTO p : products) {
                BaseColor bgColor = alternate ? evenRowColor : oddRowColor;

                PdfPCell nameCell = new PdfPCell(new Phrase(p.getName(), cellFont));
                nameCell.setBackgroundColor(bgColor);
                nameCell.setPadding(8);
                nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                nameCell.setBorderColor(borderColor);
                table.addCell(nameCell);

                PdfPCell categoryCell = new PdfPCell(new Phrase(p.getCategory(), cellFont));
                categoryCell.setBackgroundColor(bgColor);
                categoryCell.setPadding(8);
                categoryCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                categoryCell.setBorderColor(borderColor);
                table.addCell(categoryCell);

                PdfPCell quantityCell = new PdfPCell(new Phrase(String.valueOf(p.getQuantity()), cellFont));
                quantityCell.setBackgroundColor(bgColor);
                quantityCell.setPadding(8);
                quantityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                quantityCell.setBorderColor(borderColor);
                table.addCell(quantityCell);

                PdfPCell priceCell = new PdfPCell(new Phrase(String.format("%.2f", p.getPrice()), cellFont));
                priceCell.setBackgroundColor(bgColor);
                priceCell.setPadding(8);
                priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                priceCell.setBorderColor(borderColor);
                table.addCell(priceCell);

                PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f", p.getSubTotal()), cellFont));
                totalCell.setBackgroundColor(bgColor);
                totalCell.setPadding(8);
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalCell.setBorderColor(borderColor);
                table.addCell(totalCell);
                sumTotal += p.getSubTotal();

                alternate = !alternate;
            }

            document.add(table);

            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 14, Font.BOLD | Font.UNDERLINE
                    , BaseColor.DARK_GRAY);
            Paragraph totalPrice = new Paragraph("Total Price: " + sumTotal + " JD", totalFont);
            totalPrice.setAlignment(Element.ALIGN_LEFT);
            totalPrice.setSpacingAfter(45f);
            document.add(totalPrice);


            LineSeparator footerLine = new LineSeparator();
            footerLine.setLineColor(BaseColor.LIGHT_GRAY);
            footerLine.setPercentage(66);
            document.add(footerLine);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 11, Font.ITALIC, BaseColor.GRAY);

            Paragraph footer = new Paragraph("Thank you for visiting us! 😊\n© 2025 Cafe System. All rights reserved.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15f);
            document.add(footer);


            document.close();

            return byteArrayOutputStream.toByteArray();
        }catch (Exception ex){
            throw new HandleException("Something Went Wrong" + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
