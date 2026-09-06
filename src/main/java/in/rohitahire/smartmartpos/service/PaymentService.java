package in.rohitahire.smartmartpos.service;

import in.rohitahire.smartmartpos.dto.PaymentOrderResponse;
import in.rohitahire.smartmartpos.dto.PaymentVerifyRequest;

public interface PaymentService {

    PaymentOrderResponse createOrder(Long billId) throws Exception;

    boolean verifyPayment(PaymentVerifyRequest request) throws Exception;
}