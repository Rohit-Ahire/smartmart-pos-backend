package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.dto.BillRequest;
import in.rohitahire.smartmartpos.dto.BillResponse;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.service.BillService;
import in.rohitahire.smartmartpos.service.PaymentService;
import in.rohitahire.smartmartpos.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BillController {

    private final BillService billService;
    private final PdfService pdfService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<BillResponse> createBill(@RequestBody BillRequest request) {
        return ResponseEntity.ok(billService.createBill(request));
    }

    @GetMapping
    public ResponseEntity<List<BillResponse>> getAllBills() {

        List<BillResponse> bills = billService.getAllBills().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(bills);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(mapToResponse(billService.getBillById(id)));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillResponse> markBillPaid(@PathVariable Long id) {

        paymentService.markAsPaid(id);

        return ResponseEntity.ok(mapToResponse(billService.getBillById(id)));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {

        try {
            byte[] pdf = pdfService.generateInvoice(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"invoice-" + id + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private BillResponse mapToResponse(Bill bill) {

        BillResponse response = new BillResponse();

        response.setId(bill.getId());
        response.setCustomerName(bill.getCustomerName());
        response.setCustomerId(bill.getCustomerId());
        response.setCustomerPhone(bill.getCustomerPhone());
        response.setTotalAmount(bill.getTotalAmount());

        if (bill.getCreatedAt() != null) {
            response.setCreatedAt(bill.getCreatedAt().toString());
        }

        response.setPaymentStatus(bill.getPaymentStatus());
        response.setRazorpayOrderId(bill.getRazorpayOrderId());
        response.setRazorpayPaymentId(bill.getRazorpayPaymentId());

        return response;
    }
}