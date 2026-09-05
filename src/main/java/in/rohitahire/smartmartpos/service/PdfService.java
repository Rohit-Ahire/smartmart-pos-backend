package in.rohitahire.smartmartpos.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.BillItem;
import in.rohitahire.smartmartpos.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final BillRepository billRepository;

    @Transactional
    public byte[] generateInvoice(Long billId) throws Exception {

        Bill bill = billRepository.findByIdWithItems(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        PdfWriter.getInstance(document, output);
        document.open();

        Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font subFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
        Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        Paragraph title = new Paragraph("SmartMart POS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph tagline = new Paragraph("Retail Billing Software", subFont);
        tagline.setAlignment(Element.ALIGN_CENTER);
        document.add(tagline);

        document.add(new Paragraph(" "));

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);

        meta.addCell(cell("Bill ID: #" + bill.getId(), boldFont));
        meta.addCell(cell("Date: " + bill.getCreatedAt(), boldFont));
        meta.addCell(cell("Customer: " + bill.getCustomerName(), subFont));
        meta.addCell(cell("Status: " + bill.getPaymentStatus(), boldFont));

        if (bill.getRazorpayPaymentId() != null) {
            meta.addCell(cell("Payment: Online (Razorpay)", subFont));
            meta.addCell(cell("Ref: " + bill.getRazorpayPaymentId(), subFont));
        }

        document.add(meta);
        document.add(new Paragraph(" "));

        PdfPTable items = new PdfPTable(4);
        items.setWidthPercentage(100);
        items.setWidths(new float[]{3f, 1f, 1f, 1.5f});

        items.addCell(headerCell("Item", boldFont));
        items.addCell(headerCell("Qty", boldFont));
        items.addCell(headerCell("Price", boldFont));
        items.addCell(headerCell("Total", boldFont));

        BigDecimal subtotal = BigDecimal.ZERO;

        for (BillItem item : bill.getItems()) {

            String productName = item.getProduct() != null
                    ? item.getProduct().getName()
                    : "Item " + item.getId();

            BigDecimal lineTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            subtotal = subtotal.add(lineTotal);

            items.addCell(cell(productName, subFont));
            items.addCell(cell(String.valueOf(item.getQuantity()), subFont));
            items.addCell(cell("Rs. " + item.getPrice(), subFont));
            items.addCell(cell("Rs. " + lineTotal, subFont));
        }

        document.add(items);
        document.add(new Paragraph(" "));

        BigDecimal gst = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);

        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(100);
        totals.setWidths(new float[]{3f, 1.5f});

        totals.addCell(cell("Subtotal (incl. of GST split below)", subFont));
        totals.addCell(cell("Rs. " + subtotal, subFont));
        totals.addCell(cell("GST (18%)", subFont));
        totals.addCell(cell("Rs. " + gst, subFont));
        totals.addCell(cell("TOTAL", boldFont));
        totals.addCell(cell("Rs. " + bill.getTotalAmount(), boldFont));

        document.add(totals);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Thank you for shopping with us!", subFont));

        document.close();

        return output.toByteArray();
    }

    private PdfPCell cell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setPadding(6);
        return cell;
    }

    private PdfPCell headerCell(String text, Font font) {
        PdfPCell cell = cell(text, font);
        cell.setBackgroundColor(new Color(43, 92, 255));
        cell.getPhrase().getFont().setColor(Color.WHITE);
        return cell;
    }
}