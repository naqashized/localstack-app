package com.localstack.app;

import com.localstack.app.api.CarController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {"spring.jpa.hibernate.ddl-auto:create"}
)
class AppApplicationTests extends AbstractTestContainer{
	@Autowired
	private CarController carController;
	@Test
	void contextLoads() {
		assertNotNull(carController);
	}

}
