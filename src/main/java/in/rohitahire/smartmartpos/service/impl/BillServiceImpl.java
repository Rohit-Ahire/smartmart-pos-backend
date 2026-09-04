package in.rohitahire.smartmartpos.service.impl;

import in.rohitahire.smartmartpos.dto.BillItemRequest;
import in.rohitahire.smartmartpos.dto.BillRequest;
import in.rohitahire.smartmartpos.dto.BillResponse;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.BillItem;
import in.rohitahire.smartmartpos.entity.Product;
import in.rohitahire.smartmartpos.repository.BillRepository;
import in.rohitahire.smartmartpos.repository.ProductRepository;
import in.rohitahire.smartmartpos.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;

    @Override
    public BillResponse createBill(BillRequest request) {

        Bill bill = new Bill();
        bill.setCustomerName(request.getCustomerName());
        bill.setCreatedAt(LocalDateTime.now());
        bill.setPaymentStatus("PENDING");

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (BillItemRequest item : request.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Stock validation
            if (product.getQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName() +
                                ". Available: " + product.getQuantity()
                );
            }

            // Reduce stock
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            BillItem billItem = new BillItem();
            billItem.setBill(bill);
            billItem.setProduct(product);
            billItem.setQuantity(item.getQuantity());
            billItem.setPrice(product.getPrice());

            billItems.add(billItem);

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        bill.setItems(billItems);
        bill.setTotalAmount(total);

        Bill savedBill = billRepository.save(bill);

        BillResponse response = new BillResponse();
        response.setId(savedBill.getId());
        response.setCustomerName(savedBill.getCustomerName());
        response.setTotalAmount(savedBill.getTotalAmount());
        response.setCreatedAt(savedBill.getCreatedAt().toString());
        response.setPaymentStatus(savedBill.getPaymentStatus());

        return response;
    }
    @Override
    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    @Override
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }
}