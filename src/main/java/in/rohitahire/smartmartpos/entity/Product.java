package in.rohitahire.smartmartpos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name="products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    private String category;

    @Column(nullable=false)
    private BigDecimal price;

    @Column(nullable=false)
    private Integer quantity;

    private String imageUrl;
}