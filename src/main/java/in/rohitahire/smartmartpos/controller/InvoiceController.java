package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.service.BillService;
import in.rohitahire.smartmartpos.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://*.vercel.app"
})
public class InvoiceController {

    private final BillService billService;
    private final PdfService pdfService;

    @GetMapping("/{billId}")
    public ResponseEntity<Bill> getInvoice(@PathVariable Long billId) {

        Bill bill = billService.getBillById(billId);

        return ResponseEntity.ok(bill);
    }

    @GetMapping("/{billId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long billId) throws Exception {

        Bill bill = billService.getBillById(billId);

        byte[] pdf = pdfService.generateInvoicePdf(bill);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Invoice-" + bill.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}