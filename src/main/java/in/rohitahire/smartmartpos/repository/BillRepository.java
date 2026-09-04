package in.rohitahire.smartmartpos.repository;

import in.rohitahire.smartmartpos.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}