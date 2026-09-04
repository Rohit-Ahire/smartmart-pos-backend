package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.BillRequest;
import in.rohitahire.smartmartpos.dto.BillResponse;
import in.rohitahire.smartmartpos.entity.Bill;

import java.util.List;

public interface BillService {

    BillResponse createBill(BillRequest request);

    List<Bill> getAllBills();

    Bill getBillById(Long id);
}