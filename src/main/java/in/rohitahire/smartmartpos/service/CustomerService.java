package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.CustomerRequest;
import in.rohitahire.smartmartpos.dto.CustomerResponse;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.Customer;
import in.rohitahire.smartmartpos.repository.BillRepository;
import in.rohitahire.smartmartpos.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BillRepository billRepository;

    public CustomerResponse addCustomer(CustomerRequest request){

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        return map(customerRepository.save(customer), Map.of());
    }

    public List<CustomerResponse> getAllCustomers(){

        Map<Long, CustomerAgg> aggregates = buildAggregates();

        return customerRepository.findAll()
                .stream()
                .map(c -> map(c, aggregates))
                .toList();
    }

    public CustomerResponse getCustomerById(Long id){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        return map(customer, buildAggregates());
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());

        return map(customerRepository.save(customer), buildAggregates());
    }

    public String deleteCustomer(Long id){

        customerRepository.deleteById(id);

        return "Customer Deleted Successfully";
    }

    private Map<Long, CustomerAgg> buildAggregates() {

        Map<Long, CustomerAgg> result = new HashMap<>();

        for (Bill bill : billRepository.findAll()) {
            if (bill.getCustomerId() == null) continue;

            CustomerAgg agg = result.computeIfAbsent(
                    bill.getCustomerId(),
                    k -> new CustomerAgg()
            );

            agg.billCount++;

            if ("PAID".equals(bill.getPaymentStatus())) {
                agg.totalSpent = agg.totalSpent.add(
                        bill.getTotalAmount() == null
                                ? BigDecimal.ZERO
                                : bill.getTotalAmount()
                );
            }

            if (agg.lastVisit == null
                    || bill.getCreatedAt() != null && bill.getCreatedAt().isAfter(agg.lastVisit)) {
                agg.lastVisit = bill.getCreatedAt();
            }
        }

        return result;
    }

    private CustomerResponse map(Customer customer, Map<Long, CustomerAgg> aggregates) {

        CustomerAgg agg = aggregates.get(customer.getId());
        CustomerAgg empty = new CustomerAgg();

        if (agg == null) agg = empty;

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .billCount(agg.billCount)
                .totalSpent(agg.totalSpent)
                .lastVisit(agg.lastVisit == null ? null : agg.lastVisit.toString())
                .build();
    }

    private static class CustomerAgg {
        long billCount = 0;
        BigDecimal totalSpent = BigDecimal.ZERO;
        java.time.LocalDateTime lastVisit = null;
    }
}