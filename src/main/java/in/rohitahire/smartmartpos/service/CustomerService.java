package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.CustomerRequest;
import in.rohitahire.smartmartpos.dto.CustomerResponse;
import in.rohitahire.smartmartpos.entity.Customer;
import in.rohitahire.smartmartpos.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse addCustomer(CustomerRequest request){

        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        return map(customerRepository.save(customer));
    }

    public List<CustomerResponse> getAllCustomers(){

        return customerRepository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        return map(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request){

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());

        return map(customerRepository.save(customer));
    }

    public String deleteCustomer(Long id){

        customerRepository.deleteById(id);

        return "Customer Deleted Successfully";
    }

    private CustomerResponse map(Customer customer){

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .build();
    }
}