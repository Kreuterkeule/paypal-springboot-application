package com.kreuterkeule.paypalspringboot;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ShoppingCartService {

    private int _count = 0;

    private float _price = 0;

    public int get_count() {
        return _count;
    }

    public void set_count(int _count) {
        this._count = _count;
    }

    public float get_price() {
        return _price;
    }

    public void set_price(int _price) {
        this._price = _price;
    }

    public void add_count() {

        _count++;
        reload_price();

    }

    public void sub_count() {

        if (_count - 1 < 0f) {
            return;
        }

        _count--;
        reload_price();

    }

    private void reload_price() {

        this._price = _count * 7.99f;
        this._price = new BigDecimal(_price).setScale(2, RoundingMode.CEILING).floatValue();

    }
}
