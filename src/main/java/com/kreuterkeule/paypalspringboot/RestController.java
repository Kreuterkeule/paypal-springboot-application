package com.kreuterkeule.paypalspringboot;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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

    @Autowired
    private ServletContext context;
    @Autowired
    private PaypalPayService service;

    @Autowired
    private ShoppingCartService cart;

    @Autowired
    private Environment environment;

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/")
    public String getROOT(@RequestParam(value = "action", required = false) String action, Model model) {
        if (model == null) {
            return "home";
        }
        if (action == null) {
            model.addAttribute("path", context.getContextPath());
            model.addAttribute("price", cart.get_price());
            model.addAttribute("count", cart.get_count());
            return "home";
        }
        switch (action) {
            case "add":
                cart.add_count();
                break;
            case "sub":
                cart.sub_count();
                break;
            case "reset":
                cart.set_count(0);
                cart.set_price(0);
                break;
            default:
                break;
        }
        model.addAttribute("price", cart.get_price());
        model.addAttribute("count", cart.get_count());
        return "home";
    }

    @GetMapping("/error")
    public String getError() {
        return "error";
    }

    @PostMapping("/pay")
    public String postPay(@ModelAttribute("order") OrderFormData orderData) throws PayPalRESTException {
        System.out.println("asdf:" + orderData.getPrice().getClass().getName());
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
