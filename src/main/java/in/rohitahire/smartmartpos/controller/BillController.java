package in.rohitahire.smartmartpos.controller;

import in.rohitahire.smartmartpos.dto.BillRequest;
import in.rohitahire.smartmartpos.entity.Bill;
import in.rohitahire.smartmartpos.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://*.vercel.app"
})
public class BillController {

    private final BillService billService;

    @PostMapping
    public Bill createBill(@RequestBody BillRequest request) {

        return billService.createBill(
                request.getCustomerName(),
                request.getPaymentMethod(),
                request.getItems()
        );
    }

    @GetMapping
    public List<Bill> getAllBills() {
        return billService.getAllBills();
    }

    @GetMapping("/{id}")
    public Bill getBillById(@PathVariable Long id) {
        return billService.getBillById(id);
    }
}