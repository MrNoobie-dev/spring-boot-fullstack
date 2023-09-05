package com.stefco.webapp.controller;

import com.stefco.webapp.dto.CustomerDto;
import com.stefco.webapp.model.Customer;
import com.stefco.webapp.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping()
    public List<Customer> getCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public Customer getCustomer(@PathVariable("customerId") Long customerId) {
        return customerService.getCustomer(customerId);
    }


    @PostMapping()
    public void saveCustomer(@RequestBody CustomerDto customerDto) {
        customerService.addCustomer(customerDto);
    };

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Long id) {
        customerService.deleteCustomer(id);
    };

    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Long customerId,@RequestBody CustomerDto customerDto) {
        customerService.updateCustomer(customerId,customerDto);
    };
}
