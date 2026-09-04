package in.rohitahire.smartmartpos.repository;

import in.rohitahire.smartmartpos.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {}