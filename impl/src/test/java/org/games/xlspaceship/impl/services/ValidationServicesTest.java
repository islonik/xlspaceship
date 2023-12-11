package org.games.xlspaceship.impl.services;

import org.games.xlspaceship.impl.model.FireRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationServicesTest {

    @Test
    public void testValidateFireRequestCase01() {
        ValidationServices validationServices = new ValidationServices();
        List<String> salvo = new ArrayList<>();
        salvo.add("0x0");
        salvo.add("1x1");
        salvo.add("9x9");
        salvo.add("AxA");
        salvo.add("ExE");
        FireRequest fireRequest = new FireRequest();
        fireRequest.setSalvo(salvo);

        Assertions.assertNull(validationServices.validateFireRequest(fireRequest));
    }

    @Test
    public void testValidateFireRequestCase02() {
        ValidationServices validationServices = new ValidationServices();
        List<String> salvo = new ArrayList<>();
        salvo.add("0x0");
        salvo.add("1x1");
        salvo.add("9x9");
        salvo.add("AxA");
        salvo.add("BxB");
        salvo.add("ExE");
        FireRequest fireRequest = new FireRequest();
        fireRequest.setSalvo(salvo);

        Assertions.assertEquals(
                ValidationServices.MORE_THEN_5,
                validationServices.validateFireRequest(fireRequest).getBody().toString()
        );
    }

    @Test
    public void testValidateFireRequestCase03() {
        ValidationServices validationServices = new ValidationServices();
        List<String> salvo = new ArrayList<>();
        salvo.add("0x0");
        salvo.add("1x1");
        salvo.add("9x9");
        salvo.add("10x10");
        salvo.add("ExE");
        FireRequest fireRequest = new FireRequest();
        fireRequest.setSalvo(salvo);

        Assertions.assertEquals(
                "Wrong format. Shot = '10x10'.",
                validationServices.validateFireRequest(fireRequest).getBody().toString()
        );
    }

    @Test
    public void testValidateFireRequestCase04() {
        ValidationServices validationServices = new ValidationServices();
        List<String> salvo = new ArrayList<>();
        salvo.add("0y0");
        FireRequest fireRequest = new FireRequest();
        fireRequest.setSalvo(salvo);

        Assertions.assertEquals(
                "Wrong format. Shot = '0y0'.",
                validationServices.validateFireRequest(fireRequest).getBody().toString()
        );
    }

    @Test
    public void testValidateFireRequestCase05() {
        ValidationServices validationServices = new ValidationServices();
        List<String> salvo = new ArrayList<>();
        salvo.add("Gx0");
        FireRequest fireRequest = new FireRequest();
        fireRequest.setSalvo(salvo);

        Assertions.assertEquals(
                "Wrong format. Shot = 'Gx0'.",
                validationServices.validateFireRequest(fireRequest).getBody().toString()
        );
    }

    @Test
    public void testValidateFireRequestCase06() {
        ValidationServices validationServices = new ValidationServices();
        List<String> salvo = new ArrayList<>();
        salvo.add("0xG");
        FireRequest fireRequest = new FireRequest();
        fireRequest.setSalvo(salvo);

        Assertions.assertEquals(
                "Wrong format. Shot = '0xG'.",
                validationServices.validateFireRequest(fireRequest).getBody().toString()
        );
    }
}
