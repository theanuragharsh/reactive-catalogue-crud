package com.catalogue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
@SpringBootTest(
        classes = ReactiveCatalogueCrudApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ReactiveCatalogueCrudApplicationTests {

    @Test
    void contextLoads() {
    }

}
