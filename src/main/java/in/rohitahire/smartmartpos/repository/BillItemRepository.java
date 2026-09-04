package in.rohitahire.smartmartpos.repository;

import in.rohitahire.smartmartpos.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillItemRepository extends JpaRepository<BillItem,Long> {}