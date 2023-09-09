package com.stefco.webapp;

import com.github.javafaker.Faker;
import com.stefco.webapp.dao.CustomerRepository;
import com.stefco.webapp.model.Customer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.Random;

@SpringBootApplication
public class WebappApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Random random = new Random();
            Faker faker = new Faker();
            String firstname = faker.name().firstName();
            String lastname = faker.name().lastName();
            Customer customer = new Customer(firstname + " " + lastname, firstname.toLowerCase()+"."+lastname.toLowerCase()+"@gmail.com", random.nextInt(99-16+1)+16);
            customerRepository.save(customer);
        };
    }
}