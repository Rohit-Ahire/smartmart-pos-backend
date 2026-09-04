package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.dto.BillResponse;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class InvoiceController {

    private final BillRepository billRepository;

    @GetMapping
    public List<BillResponse> getAllInvoices() {

        return billRepository.findAll().stream().map(bill -> {
            BillResponse response = new BillResponse();

            response.setId(bill.getId());
            response.setCustomerName(bill.getCustomerName());
            response.setTotalAmount(bill.getTotalAmount());

            if (bill.getCreatedAt() != null) {
                response.setCreatedAt(bill.getCreatedAt().toString());
            }

            response.setPaymentStatus(bill.getPaymentStatus());

            return response;
        }).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public BillResponse getInvoice(@PathVariable Long id) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        BillResponse response = new BillResponse();

        response.setId(bill.getId());
        response.setCustomerName(bill.getCustomerName());
        response.setTotalAmount(bill.getTotalAmount());

        if (bill.getCreatedAt() != null) {
            response.setCreatedAt(bill.getCreatedAt().toString());
        }

        response.setPaymentStatus(bill.getPaymentStatus());

        return response;
    }
}