package com.kreuterkeule.paypalspringboot;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RestController {


    public RestController() {
    }

    public static final String SUCCESS_URL = "pay/success";
    public static final String CANCEL_URL = "pay/cancel";

    @Value("${server.port}")
    private String _serverPort;

    @Autowired
    private ServletContext _servletContext;
    @Autowired
    private PaypalPayService _service;

    @Autowired
    private ShoppingCartService _cart;

    @Autowired
    private ServerProperties _serverProperties;

    @GetMapping("/")
    public String getROOT(@RequestParam(value = "action", required = false) String action, Model model) {

        //for test cases

        if (model == null) {

            return "home";

        }

        if (action == null) {
            model.addAttribute("price", _cart.get_price());
            model.addAttribute("count", _cart.get_count());

            return "home";
        }

        switch (action) {
            case "add":
                _cart.add_count();
                break;
            case "sub":
                _cart.sub_count();
                break;
            case "reset":
                _cart.set_count(0);
                _cart.set_price(0);
                break;
            default:
                break;
        }

        model.addAttribute("price", _cart.get_price());
        model.addAttribute("count", _cart.get_count());

        return "home";
    }

    @GetMapping("/error")
    public String getError() {

        return "error";

    }

    @PostMapping("/pay")
    public String postPay(@ModelAttribute("order") OrderFormData orderData) throws PayPalRESTException {

        Payment payment = _service.generatePayment(
                orderData.getPrice(), // not from cart because tests would run into errors with empty cart
                orderData.getCurrency(),
                orderData.getMethod(),
                orderData.getIntent(),
                orderData.getDescription(),

                //TODO: get actual server port not from application.properties

                "http://localhost:" + _serverPort + _servletContext.getContextPath() + "/" + CANCEL_URL,
                "http://localhost:" + _serverPort + _servletContext.getContextPath() + "/" + SUCCESS_URL
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
