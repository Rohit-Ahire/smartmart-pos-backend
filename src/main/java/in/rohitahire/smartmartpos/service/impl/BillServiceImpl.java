package in.rohitahire.smartmartpos.service.impl;

import in.rohitahire.smartmartpos.dto.BillItemRequest;
import in.rohitahire.smartmartpos.dto.BillRequest;
import in.rohitahire.smartmartpos.dto.BillResponse;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.BillItem;
import in.rohitahire.smartmartpos.entity.Customer;
import in.rohitahire.smartmartpos.entity.Product;
import in.rohitahire.smartmartpos.repository.BillRepository;
import in.rohitahire.smartmartpos.repository.CustomerRepository;
import in.rohitahire.smartmartpos.repository.ProductRepository;
import in.rohitahire.smartmartpos.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BillServiceImpl implements BillService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    @Override
    public BillResponse createBill(BillRequest request) {

        Bill bill = new Bill();
        bill.setCreatedAt(LocalDateTime.now());
        bill.setPaymentStatus("PENDING");

        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            bill.setCustomerId(customer.getId());
            bill.setCustomerName(customer.getName());
            bill.setCustomerPhone(customer.getPhone());
        } else {
            bill.setCustomerName(
                    request.getCustomerName() == null || request.getCustomerName().isBlank()
                            ? "Walk-in Customer"
                            : request.getCustomerName()
            );
        }

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
        bill.setTotalAmount(applyGst(total));

        Bill savedBill = billRepository.save(bill);

        BillResponse response = new BillResponse();
        response.setId(savedBill.getId());
        response.setCustomerName(savedBill.getCustomerName());
        response.setCustomerId(savedBill.getCustomerId());
        response.setCustomerPhone(savedBill.getCustomerPhone());
        response.setTotalAmount(savedBill.getTotalAmount());
        response.setCreatedAt(savedBill.getCreatedAt().toString());
        response.setPaymentStatus(savedBill.getPaymentStatus());

        return response;
    }

    private BigDecimal applyGst(BigDecimal subtotal) {

        BigDecimal gst = subtotal.multiply(GST_RATE);
        return subtotal.add(gst).setScale(2, RoundingMode.HALF_UP);
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