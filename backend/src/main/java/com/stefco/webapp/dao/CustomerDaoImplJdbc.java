package com.stefco.webapp.dao;

import com.stefco.webapp.dao.helpers.CustomerRowMapper;
import com.stefco.webapp.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerDaoImplJdbc implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerDaoImplJdbc(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }


    @Override
    public List<Customer> selectAllCustomers() {
        String sql ="""
                    SELECT id, name, email, age FROM customer
                    """;

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        String sql = """
                SELECT id, name, email, age 
                FROM customer 
                WHERE id = ?
                """;

        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        String sql = """
                    INSERT INTO customer(name, email, age)
                    VALUES (?,?,?)
                    """;
        int updated = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());
        System.out.println("Total Values updated " + updated);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        String sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count!= null && count > 0;

    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        String sql = """
                SELECT count(id)
                FROM customer
                WHERE id = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count!= null && count > 0;
    }

    @Override
    public void deleteCustomerWithId(Long id) {
        String sql = """
                DELETE FROM customer
                WHERE id = ?;
                """;
        int updated = jdbcTemplate.update(sql, id);
        System.out.println("Customers Deleted " + updated);
    }

    @Override
    public void updateCustomer(Customer update) {
        String sql = """
                UPDATE customer
                SET name = ?, email = ?, age = ?
                WHERE id = ?
                """;
        if (update.getName() != null && update.getEmail() != null && update.getAge() != null) {
            int updated = jdbcTemplate.update(sql, update.getName(), update.getEmail(), update.getAge(), update.getId());
            System.out.println("Customers Updated " + updated);
        }
    }
}
