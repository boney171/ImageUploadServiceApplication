package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=password",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "jwt.secret=TRI_TRAN_171_1997",
        "jwt.issuer=TRI_TRAN",
        "imgur.client-id=ed9aeeb28e0e629",
        "imgur.client-secret=7fcb7d2fe793a0bb017ec8a6f75ac33275a73268",
        "imgur.username=tritran1711"
})
class SynchronyApplicationTests {

    @Test
    void contextLoads() {
    }

}
