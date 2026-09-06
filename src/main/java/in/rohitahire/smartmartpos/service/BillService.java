package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.ProductRequest;
import in.rohitahire.smartmartpos.entity.Bill;

import java.util.List;

public interface BillService {

    Bill createBill(String customerName,
                    String paymentMethod,
                    List<ProductRequest> items);

    List<Bill> getAllBills();

    Bill getBillById(Long id);
}