/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.ijse.pos.business.BOFactory;
import lk.ijse.pos.business.custom.CustomerBO;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.view.util.tblmodel.CustomerTM;

/**
 *
 * @author SDMROX
 */
public class ManageCustomerFormController implements Initializable{

    @FXML
    private AnchorPane root;
    @FXML
    private JFXTextField txtCustomerId;
    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXTextField txtCustomerAddress;
    @FXML
    private TableView<CustomerTM> tblCustomers;
    
    private boolean newCus=false;
    
    private CustomerBO customerBO=(CustomerBO) BOFactory.getInstance().getBO(BOFactory.BOType.CustomerBO);
    @FXML
    private JFXButton btnAddNewCus;
    
     @Override
    public void initialize(URL location, ResourceBundle resources) {
        newCus=true;
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        loaddAll();
    }

    @FXML
    private void navigateToHome(MouseEvent event) {
    }

    @FXML
    private void btnAddNewCustomer_OnAction(ActionEvent event) {
        newCus=true;
        txtCustomerId.setText("");
        txtCustomerName.setText("");
        txtCustomerAddress.setText("");
        tblCustomers.getSelectionModel().clearSelection();
        
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
        
        if(newCus){
              saveCustomer();
              loaddAll();
              clearFealds();
        }else if(tblCustomers.getSelectionModel().getFocusedIndex()>=0&& newCus==false){
                  updateCustomer();
                  loaddAll();
        
        }


        
    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {
        int selectedIndex = tblCustomers.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex>=0) {
            delete();
            loaddAll();
            clearFealds();
       
        }else{
            new Alert(Alert.AlertType.ERROR, "Please select item to delete", ButtonType.OK).show();
        }
        
        
    }
    
    private void clearFealds(){
     txtCustomerId.setText("");
        txtCustomerName.setText("");
        txtCustomerAddress.setText("");
    
    }

    
    private void delete(){
        try {
            CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
            String id = selectedItem.getId();
            
            boolean deleteCustomer = customerBO.deleteCustomer(id);
            
            if(deleteCustomer){
                new Alert(Alert.AlertType.CONFIRMATION, "Succssfully Deleted", ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.ERROR, "Not Deleted", ButtonType.OK).show();
            }
            
            
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveCustomer(){
    
        try {
            String cusId = txtCustomerId.getText();
            String cusName = txtCustomerName.getText();
            String cusAddress = txtCustomerAddress.getText();
            
            CustomerDTO customerDTO = new CustomerDTO(cusId, cusName, cusAddress);
            
            boolean rst = customerBO.saveCustomer(customerDTO);
            if(rst){
                new Alert(Alert.AlertType.CONFIRMATION, "Succssfully Saved", ButtonType.OK).show();
//                 txtCustomerId.requestFocus();
//                txtCustomerId.selectAll();
//                txtCustomerId.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            }else{
                new Alert(Alert.AlertType.ERROR, "Not Saved", ButtonType.OK).show();
                 txtCustomerId.requestFocus();
                txtCustomerId.selectAll();
            }
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    private void updateCustomer(){
        CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
        
        try {
            
            String cusId = selectedItem.getId();
            String cusName = txtCustomerName.getText();
            String cusAddress = txtCustomerAddress.getText();
            CustomerDTO customerDTO = new CustomerDTO(cusId, cusName, cusAddress);
            
         
            
            boolean rst = customerBO.updateCustomer(customerDTO);
            
            if(rst){
                new Alert(Alert.AlertType.CONFIRMATION, "Succssfully Updated", ButtonType.OK).show();
            }else{
               new Alert(Alert.AlertType.ERROR, "Succssfully Saved", ButtonType.OK).show();
            }
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    
    }
   
    
    
    private void loaddAll(){
        try {
            ArrayList<CustomerDTO> allCustomer = customerBO.getAllCustomer();
            ArrayList<CustomerTM> addCustomer=new ArrayList<>();
            for (CustomerDTO customer : allCustomer) {
                
                CustomerTM cuStomer = new CustomerTM(customer.getId(),customer.getName(),customer.getAddress());
                addCustomer.add(cuStomer);
            }
            
            tblCustomers.setItems(FXCollections.observableArrayList(addCustomer));
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

    @FXML
    private void tblOnMouseClick(MouseEvent event) {
       newCus=false;
        CustomerTM selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
        txtCustomerId.setText(selectedItem.getId());
        txtCustomerName.setText(selectedItem.getName());
        txtCustomerAddress.setText(selectedItem.getAddress());
    }
}
