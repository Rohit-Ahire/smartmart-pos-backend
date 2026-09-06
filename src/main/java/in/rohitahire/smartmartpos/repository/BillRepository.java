package in.rohitahire.smartmartpos.repository;

import in.rohitahire.smartmartpos.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("select b from Bill b left join fetch b.items item left join fetch item.product where b.id = :id")
    Optional<Bill> findByIdWithItems(@Param("id") Long id);
}