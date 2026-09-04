package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.BillResponse;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final BillRepository billRepository;

    public BillResponse generateInvoice(Long billId) {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        BillResponse response = new BillResponse();

        response.setId(bill.getId());
        response.setCustomerName(bill.getCustomerName());
        response.setTotalAmount(bill.getTotalAmount());
        response.setCreatedAt(bill.getCreatedAt().toString());
        response.setPaymentStatus(bill.getPaymentStatus());

        return response;
    }
}