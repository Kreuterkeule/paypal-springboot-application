package com.kreuterkeule.paypalspringboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HTTPRequestTest {

    @Value(value = "${local.server.port}")
    private int _port;

    @Autowired
    private TestRestTemplate _restTemplate;

    @Test
    public void homeReturnsHomeHTMLTest() throws Exception {
        assertThat(
                this._restTemplate.getForObject("http://localhost:" + _port + "/", String.class)
        ).contains("<!DOCTYPE html>");
    }

    @Test
    public void paySuccessReturnsSuccessHTMLTest() throws Exception {
        assertThat(
                this._restTemplate.getForObject("http://localhost:" + _port + "/pay/success", String.class)
        ).contains("<!DOCTYPE html>");
    }

    @Test
    public void payCancelReturnsCancelHTMLTest() throws Exception {
        assertThat(
                this._restTemplate.getForObject("http://localhost:" + _port + "/pay/cancel", String.class)
        ).contains("<!DOCTYPE html>");
    }

}
