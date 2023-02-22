package com.kreuterkeule.paypalspringboot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderFormData {

    private Double price;
    private String currency;
    private String method;
    private String intent;
    private String description;

}
