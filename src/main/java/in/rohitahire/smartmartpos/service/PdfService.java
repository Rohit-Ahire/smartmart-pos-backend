package in.rohitahire.smartmartpos.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import in.rohitahire.smartmartpos.entity.Bill;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateInvoice(Bill bill) throws Exception {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, output);

        document.open();

        Font title = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);

        document.add(new Paragraph("SmartMart Invoice", title));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Bill ID: " + bill.getId()));
        document.add(new Paragraph("Customer: " + bill.getCustomerName()));
        document.add(new Paragraph("Date: " + bill.getCreatedAt()));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total Amount: ₹" + bill.getTotalAmount()));
        document.add(new Paragraph("Payment Status: PENDING"));

        document.close();

        return output.toByteArray();
    }
}