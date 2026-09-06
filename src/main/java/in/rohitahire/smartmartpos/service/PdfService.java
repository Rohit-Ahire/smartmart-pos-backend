package in.rohitahire.smartmartpos.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.BillItem;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    public byte[] generateInvoicePdf(Bill bill) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 30, 30, 30, 30);

        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(0, 52, 153));
        Font normal = new Font(Font.HELVETICA, 12);
        Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);

        Paragraph title = new Paragraph("SmartMart POS Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Invoice No : INV-" + bill.getId(), bold));
        document.add(new Paragraph("Customer : " + bill.getCustomerName(), normal));
        document.add(new Paragraph("Date : " + bill.getCreatedAt(), normal));
        document.add(new Paragraph("Payment Status : " + bill.getPaymentStatus(), normal));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4,2,2,2});

        addHeader(table, "Product");
        addHeader(table, "Qty");
        addHeader(table, "Price");
        addHeader(table, "Total");

        for (BillItem item : bill.getItems()) {

            table.addCell(item.getProduct().getName());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell("₹ " + item.getPrice());

            double total = item.getPrice().doubleValue() * item.getQuantity();

            table.addCell("₹ " + String.format("%.2f", total));
        }

        document.add(table);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Grand Total : ₹ " + bill.getTotalAmount(),
                new Font(Font.HELVETICA, 16, Font.BOLD, new Color(0,52,153))
        ));

        document.add(new Paragraph(" "));
        Paragraph thanks = new Paragraph("Thank you for shopping with SmartMart!", bold);
        thanks.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);

        document.close();

        return out.toByteArray();
    }

    private void addHeader(PdfPTable table, String text){

        PdfPCell cell = new PdfPCell(new Phrase(text));

        cell.setBackgroundColor(new Color(0,52,153));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);

        cell.setPhrase(new Phrase(text,
                new Font(Font.HELVETICA,12,Font.BOLD,Color.WHITE)));

        table.addCell(cell);
    }
}