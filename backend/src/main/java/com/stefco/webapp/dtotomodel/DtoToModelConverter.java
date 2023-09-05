package com.stefco.webapp.dtotomodel;


import com.stefco.webapp.dto.CustomerDto;
import com.stefco.webapp.model.Customer;

public class DtoToModelConverter {
    public static Customer convertDtoToCustomer(CustomerDto customerDto) {
        return new Customer(customerDto.getName(), customerDto.getEmail(), customerDto.getAge());
    }
}
