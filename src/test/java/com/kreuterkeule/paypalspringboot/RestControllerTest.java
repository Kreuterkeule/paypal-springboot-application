package com.kreuterkeule.paypalspringboot;

import com.paypal.base.rest.PayPalRESTException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class RestControllerTest {

    @Autowired
    private RestController controller = new RestController();

    @Autowired
    private PaypalPayService service = new PaypalPayService();

    @Test
    public void RestControllerFunctionsTest() throws PayPalRESTException {
        String expected;
        String actual;

        // GET: /home

        expected = "home";
        actual = controller.getROOT( "reset", null);
        assertEquals(expected, actual);

        // GET: /pay/success

        expected = "success";
        actual = controller.getPaySuccess();
        assertEquals(expected, actual);

        // GET: /pay/cancel

        expected = "cancel";
        actual = controller.getPayCancel();
        assertEquals(expected, actual);

        // POST: /pay

        // Possible Request Body:

        OrderFormData order = new OrderFormData(
                1D,
                "USD",
                "paypal",
                "sale",
                "Testing RestContoller"
        );

        expected = "redirect:https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=";
        actual = controller.postPay(order);
        System.out.println(actual);
        assertTrue(actual.contains(expected));
    }

    @Test
    public void errorCatches() throws PayPalRESTException {

        String expected;
        String actual;

        // Initializing list of invalid Orders

        ArrayList<OrderFormData> orders = new ArrayList<>();

        orders.add(new OrderFormData(
                0D,
                "USD",
                "paypal",
                "sale",
                "Testing RestContoller"
        ));

        orders.add(new OrderFormData(
                1D,
                "notValid",
                "paypal",
                "sale",
                "Testing RestContoller"
        ));

        orders.add(new OrderFormData(
                1D,
                "USD",
                "notValid",
                "sale",
                "Testing RestContoller"
        ));

        orders.add(new OrderFormData(
                1D,
                "USD",
                "paypal",
                "notValid",
                "Testing RestContoller"
        ));

        // Test each order

        for ( OrderFormData order : orders ) {
            Exception exception = assertThrows(PayPalRESTException.class, () -> {
                controller.postPay(order);
            });
            expected = "Response code: 400\tError response";
            actual = exception.getMessage();
            assertTrue(actual.contains(expected));
        }
    }

}
