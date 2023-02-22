package com.kreuterkeule.paypalspringboot;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalPayService {

    @Autowired
    APIContext context;

    public Payment generatePayment(
            Double total,
            String currency,
            String payMethod,
            String intent,
            String description,
            String cancelUrl,
            String successUrl
    ) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        total = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP).doubleValue();
        amount.setTotal(String.format("%.3f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(payMethod);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setReturnUrl(successUrl);
        redirectUrls.setCancelUrl(cancelUrl);

        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactionList);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(context);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(context, paymentExecution);

    }

}
