package com.kreuterkeule.paypalspringboot;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RestController {


    public RestController() {
    }

    public static final String SUCCESS_URL = "pay/success";
    public static final String CANCEL_URL = "pay/cancel";

    @Autowired
    private PaypalPayService service;

    @Autowired
    private Environment environment;

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/")
    public String getROOT() {
        return "home";
    }

    @GetMapping("/error")
    public String getEroor() {
        return "error";
    }

    @PostMapping("/pay")
    public String postPay(@ModelAttribute("order") OrderFormData orderData) throws PayPalRESTException {
        Payment payment = service.generatePayment(
                orderData.getPrice(),
                orderData.getCurrency(),
                orderData.getMethod(),
                orderData.getIntent(),
                orderData.getDescription(),
                "http://localhost:" + serverPort + "/",
                "http://localhost:" + serverPort + "/"
        );
        for (Links link : payment.getLinks()) {
            if (link.getRel().equals("approval_url")) {
                return "redirect:" +  link.getHref();
            }
        }
        return "redirect:/";
    }

    @GetMapping(value = SUCCESS_URL)
    public String getPaySuccess() {
        return "success";
    }

    @GetMapping(value = CANCEL_URL)
    public String getPayCancel() {
        return "cancel";
    }
}
