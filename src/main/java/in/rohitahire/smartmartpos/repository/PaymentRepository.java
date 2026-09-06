package in.rohitahire.smartmartpos.repository;

import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    Optional<Payment> findByBill(Bill bill);
}