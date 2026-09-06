package in.rohitahire.smartmartpos.service.impl;

import in.rohitahire.smartmartpos.dto.ProductRequest;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.BillItem;
import in.rohitahire.smartmartpos.entity.Product;
import in.rohitahire.smartmartpos.repository.BillRepository;
import in.rohitahire.smartmartpos.repository.ProductRepository;
import in.rohitahire.smartmartpos.service.BillService;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Bill createBill(String customerName,
                           String paymentMethod,
                           List<ProductRequest> items) {

        Bill bill = new Bill();

        bill.setCustomerName(customerName);
        bill.setCreatedAt(LocalDateTime.now());
        bill.setPaymentMethod(paymentMethod);

        if ("ONLINE".equalsIgnoreCase(paymentMethod)) {
            bill.setPaymentStatus("PENDING");
        } else {
            bill.setPaymentStatus("SUCCESS");
        }

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ProductRequest request : items) {

            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < request.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName()
                );
            }

            product.setQuantity(
                    product.getQuantity() - request.getQuantity()
            );

            productRepository.save(product);

            BillItem billItem = new BillItem();

            billItem.setBill(bill);
            billItem.setProduct(product);
            billItem.setQuantity(request.getQuantity());
            billItem.setPrice(product.getPrice());

            billItems.add(billItem);

            total = total.add(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(request.getQuantity()))
            );
        }

        bill.setItems(billItems);
        bill.setTotalAmount(total);

        return billRepository.save(bill);
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