package com.stefco.webapp.dao;

import com.stefco.webapp.AbstractTestContainers;
import com.stefco.webapp.dao.helpers.CustomerRowMapper;
import com.stefco.webapp.model.Customer;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerDaoImplJdbcTest extends AbstractTestContainers {

    private CustomerDaoImplJdbc underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    @BeforeEach
    void setUp() {
        underTest = new CustomerDaoImplJdbc(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        //Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        //When
        List<Customer> actual = underTest.selectAllCustomers();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c ->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        //Given
        Long id = (long) -1;

        //When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        //When
        underTest.insertCustomer(customer);
        List<Customer> actual = underTest.selectAllCustomers();
        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        //When
        boolean actual = underTest.existsPersonWithEmail(email);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailDoesNotExists() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        //When
        boolean actual = underTest.existsPersonWithEmail(email);
        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomerWithId() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //When
        boolean actual = underTest.existsCustomerWithId(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdFalse() {
        //Given
        Long id = (long) -1;
        //When
        boolean actual = underTest.existsCustomerWithId(id);
        //Then
        assertThat(actual).isFalse();

    }

    @Test
    void deleteCustomerWithId() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //When
        underTest.deleteCustomerWithId(id);
        //Then
        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isNotPresent();
    }


    @Test
    void updateCustomerAllProperties() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String updatedEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer updatedCustomer = new Customer(
                id,
                FAKER.name().fullName(),
                updatedEmail,
                FAKER.number().numberBetween(16,99));
        //When
        underTest.updateCustomer(updatedCustomer);
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValue(updatedCustomer);
    }

    @Test
    void updateCustomerName() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer(
                id,
                FAKER.name().fullName(),
                customer.getEmail(),
                customer.getAge());
        //When
        underTest.updateCustomer(updatedCustomer);
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedCustomer.getName());
            assertThat(c.getEmail()).isEqualTo(updatedCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(updatedCustomer.getAge());
        });
    }
    void updateCustomerEmail() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String updatedEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer updatedCustomer = new Customer(
                id,
                customer.getName(),
                updatedEmail,
                customer.getAge());
        //When
        underTest.updateCustomer(updatedCustomer);
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedCustomer.getName());
            assertThat(c.getEmail()).isEqualTo(updatedCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(updatedCustomer.getAge());
        });
    }
    void updateCustomerAge() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer(
                id,
                customer.getName(),
                customer.getEmail(),
                FAKER.number().numberBetween(16,99));
        //When
        underTest.updateCustomer(updatedCustomer);
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedCustomer.getName());
            assertThat(c.getEmail()).isEqualTo(updatedCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(updatedCustomer.getAge());
        });
    }
    @Test
    void willNotUpdateWhenNothingToUpdate() {
        //Given
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                FAKER.number().numberBetween(16,99)
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Customer updatedCustomer = new Customer(id, customer.getName(),customer.getEmail(),customer.getAge());

        //When
        underTest.updateCustomer(updatedCustomer);
        Optional<Customer> actual = underTest.selectCustomerById(id);
        //Then
        assertThat(actual).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updatedCustomer.getName());
            assertThat(c.getEmail()).isEqualTo(updatedCustomer.getEmail());
            assertThat(c.getAge()).isEqualTo(updatedCustomer.getAge());
        });
    }
}