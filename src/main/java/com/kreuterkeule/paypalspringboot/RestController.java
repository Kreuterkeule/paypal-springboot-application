package com.kreuterkeule.paypalspringboot;


import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class RestController {


    public RestController() {
    }

    public static final String SUCCESS_URL = "pay/success";
    public static final String CANCEL_URL = "pay/cancel";

    @Value("${server.port}")
    private String _serverPort;

    // List of carts important for sessions

    private HashMap<Object, ShoppingCartService> _shoppingCarts = new HashMap<Object, ShoppingCartService>();

    @Autowired
    private RandomTokenService _tokenService;
    @Autowired
    private ServletContext _servletContext;
    @Autowired
    private PaypalPayService _service;

    @Autowired
    private ServerProperties _serverProperties;

    @GetMapping("/")
    public String getROOT(@RequestParam(value = "action", required = false) String action, Model model, HttpServletResponse response, HttpServletRequest request) {

        //for test cases

        if (request == null || response == null || model == null) {

            return "home";

        }

        String sessionToken;

        ShoppingCartService _cart;

        HttpSession session = request.getSession(true); // creates a session if there is none and returns the session

        if (session.getAttribute("sessionToken") == null) { // generate new session if there is no session yet
            boolean tokenNotUnique = true;
            String generatedToken = "notVerified!";
            while (tokenNotUnique) {
                generatedToken = _tokenService.getRandomToken();
                if (!_shoppingCarts.containsKey(generatedToken)) { // check if token is already taken
                    tokenNotUnique = false;
                }
            }
            session.setAttribute("sessionToken", generatedToken);
            System.out.println("New User '" + generatedToken + "' generated");
            sessionToken = generatedToken;
            _shoppingCarts.put(sessionToken, new ShoppingCartService());
        } else { // sets up the session if there is one already
            System.out.println("User '" + session.getAttribute("sessionToken") + "' connected");
            sessionToken = (String) session.getAttribute("sessionToken");
        }

        _cart = _shoppingCarts.get(sessionToken); // gets the cart which corresponds to the current session


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
                //orderData.getCurrency(),
                "USD", // ISO CURRENCY CODE could be anything, but decided it to be USD feel free and change it f.e. EUR,CHE
                "paypal",
                "sale",
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

    // for checking cookies

    @GetMapping("/all-cookies")
    public String readAllCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .map(c -> c.getName() + "=" + c.getValue()).collect(Collectors.joining(", "));
        }

        return "No cookies";
    }

}
