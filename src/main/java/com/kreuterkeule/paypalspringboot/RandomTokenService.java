package com.kreuterkeule.paypalspringboot;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomTokenService {

    public String getRandomToken() {
        int leftLimit = 97; //letter 'a'
        int rightLimit = 122; //letter 'z'
        int targetTokenLength = 1024; // not possible to fake this or get via bruteforde
        Random random = new Random();
        String generatedToken = random.ints(leftLimit,rightLimit + 1)
                .limit(targetTokenLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedToken;
    }

}
