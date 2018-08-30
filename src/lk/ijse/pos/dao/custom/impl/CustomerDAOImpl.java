/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.pos.dao.custom.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import lk.ijse.pos.dao.CrudUtil;
import lk.ijse.pos.dao.custom.CustomerDAO;
import lk.ijse.pos.entity.Customer;

/**
 *
 * @author SDMROX
 */
public class CustomerDAOImpl implements CustomerDAO{

    @Override
    public Boolean save(Customer entity) throws Exception {
        return CrudUtil.executeUpdate("INSERT INTO customer VALUES(?,?,?)", entity.getId(),entity.getName(),entity.getAddress());
    }

    @Override
    public Boolean delete(String id) throws Exception {
       return CrudUtil.executeUpdate("delete from customer where id=?", id);
    }

    @Override
    public Boolean update(Customer entity) throws Exception {
        return CrudUtil.executeUpdate("update customer set name=?,address=? where id=?", entity.getName(),entity.getAddress(),entity.getId());
    }

    @Override
    public ArrayList<Customer> getAll() throws Exception {
        ArrayList<Customer> customers = new ArrayList<>();
        ResultSet rs = CrudUtil.executeQuery("select * from customer");
        while (rs.next()) {
            Customer customer = new Customer(rs.getString(1), rs.getString(2), rs.getString(3));
            customers.add(customer);
        }
        return customers;
        
    }

    @Override
    public Customer findByID(String id) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("select * from customer where id = ?", id);
        rs.next();
        Customer customer = new Customer(rs.getString(1), rs.getString(2), rs.getString(3));
        return customer;
    }
}
