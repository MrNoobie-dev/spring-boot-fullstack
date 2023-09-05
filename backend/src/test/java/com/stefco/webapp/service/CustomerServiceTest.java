package com.stefco.webapp.service;

import com.stefco.webapp.dao.CustomerDao;
import com.stefco.webapp.dto.CustomerDto;
import com.stefco.webapp.model.Customer;
import com.stefco.webapp.service.exception.CustomerNotFoundException;
import com.stefco.webapp.service.exception.DuplicateResourceException;
import com.stefco.webapp.service.exception.RequestValidationException;
import com.stefco.webapp.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        long id = 10;
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("customer with id [%s] not found",id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "alex@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        CustomerDto customerDto = new CustomerDto("Alex", email, 33);
        //When
        underTest.addCustomer(customerDto);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerDto.getName());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerDto.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerDto.getAge());
    }
    @Test
    void willThrowWhenEmailExistsWhileAddCustomer() {
        //Given
        String email = "alex@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
        CustomerDto customerDto = new CustomerDto("Alex", email, 33);
        //When
        assertThatThrownBy(() -> underTest.addCustomer(customerDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");
        //Then
        verify(customerDao, never()).insertCustomer(any());
    }

     @Test
    void canDeleteCustomer() {
        //Given
        long id = 10;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomer(id);
        //Then
        verify(customerDao).deleteCustomerWithId(id);
    }

    @Test
    void throwExceptionDeleteCustomer() {
        //Given
        long id = 10;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(String.format("customer with id [%s] not found",id));
        //Then
        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alesandro@gmail.com";
        CustomerDto customerDto = new CustomerDto("Alesandro", newEmail, 55);
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id,customerDto);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(customerDto.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerDto.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customerDto.getName());
    }

    @Test
    void canUpdateOnlyCustomerNameProperty() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerDto customerDto = new CustomerDto("Alesandro", null, null);
        //When
        underTest.updateCustomer(id,customerDto);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customerDto.getName());
    }

    @Test
    void canUpdateOnlyEmailCustomerProperties() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alesandro@gmail.com";
        CustomerDto customerDto = new CustomerDto(null, newEmail, null);
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        //When
        underTest.updateCustomer(id,customerDto);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerDto.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
    }

    @Test
    void canUpdateOnlyCustomerAgeProperty() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerDto customerDto = new CustomerDto(null, null, 16);
        //When
        underTest.updateCustomer(id,customerDto);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getAge()).isEqualTo(customerDto.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
    }

    @Test
    void willThrowWhenTryingUpdateOnlyEmailCustomerPropertiesAlreadyTaken() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        String newEmail = "alesandro@gmail.com";
        CustomerDto customerDto = new CustomerDto(null, newEmail, null);
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        //Then
        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void willThrowWhenUpdateHasNoChanges() {
        //Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "Alex@gmail.com", 32);
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerDto customerDto = new CustomerDto(customer.getName(), customer.getEmail(), customer.getAge());
        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerDto))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changed found");
        //Then
        verify(customerDao, never()).updateCustomer(any());
    }
}