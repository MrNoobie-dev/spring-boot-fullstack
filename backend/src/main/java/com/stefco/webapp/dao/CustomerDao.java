package com.stefco.webapp.dao;

import com.stefco.webapp.model.Customer;

import java.util.List;
import java.util.Optional;


public interface CustomerDao {

    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    boolean existsCustomerWithId(Long id);
    void deleteCustomerWithId(Long id);
    void updateCustomer(Customer update);


}
