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

    //TODO: name privates with underscore

    // did not work before, because price would be initialized with null, even when changing name of input field to name="_price"

    private Double price;
    private String currency;
    private String method;
    private String intent;
    private String description;
}
