package in.rohitahire.smartmartpos.repository;

import in.rohitahire.smartmartpos.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {}