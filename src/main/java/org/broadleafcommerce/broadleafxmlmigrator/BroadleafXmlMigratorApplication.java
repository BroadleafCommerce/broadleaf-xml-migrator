package org.broadleafcommerce.broadleafxmlmigrator;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BroadleafXmlMigratorApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(BroadleafXmlMigratorApplication.class, args);
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("IM IN YRCODE");
    }
}
