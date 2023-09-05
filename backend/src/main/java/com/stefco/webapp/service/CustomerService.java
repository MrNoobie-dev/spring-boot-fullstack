package com.stefco.webapp.service;

import com.stefco.webapp.dao.CustomerDao;
import com.stefco.webapp.dto.CustomerDto;

import static com.stefco.webapp.dtotomodel.DtoToModelConverter.convertDtoToCustomer;

import com.stefco.webapp.model.Customer;
import com.stefco.webapp.service.exception.CustomerNotFoundException;
import com.stefco.webapp.service.exception.DuplicateResourceException;
import com.stefco.webapp.service.exception.RequestValidationException;
import com.stefco.webapp.service.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {


    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long id) {
        return customerDao.selectCustomerById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("customer with id [%s] not found",id)));
    }

    public void addCustomer(CustomerDto customerDto) {

        if (customerDao.existsPersonWithEmail(customerDto.getEmail())) {
            throw new DuplicateResourceException("Email already taken");
        }

        customerDao.insertCustomer(convertDtoToCustomer(customerDto));
    };

    public void deleteCustomer(Long id) {
        if (!customerDao.existsCustomerWithId(id)){
            throw new CustomerNotFoundException(String.format("customer with id [%s] not found",id));
        };
        customerDao.deleteCustomerWithId(id);
    }

    public void updateCustomer(Long id, CustomerDto customerDto) {
        Customer oldCustomer = getCustomer(id);

        boolean changed = false;

        if (customerDto.getName() != null && !customerDto.getName().equals(oldCustomer.getName())) {
            oldCustomer.setName(customerDto.getName());
            changed = true;
        }

        if (customerDto.getEmail() != null && !customerDto.getEmail().equals(oldCustomer.getEmail())) {
            if (customerDao.existsPersonWithEmail(customerDto.getEmail())) {
                throw new DuplicateResourceException("email already taken");
            }
            oldCustomer.setEmail(customerDto.getEmail());
            changed = true;
        }

        if (customerDto.getAge() != null && !customerDto.getAge().equals(oldCustomer.getAge())) {
            oldCustomer.setAge(customerDto.getAge());
            changed = true;
        }

        if (!changed) {
            throw new RequestValidationException("no data changed found");
        }

        customerDao.updateCustomer(oldCustomer);

    }
}
