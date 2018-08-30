/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.business.BOFactory;
import lk.ijse.pos.business.custom.ItemBO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.view.util.tblmodel.ItemTM;

/**
 *
 * @author SDMROX
 */
public class ManageItemFormController implements Initializable{

    @FXML
    private AnchorPane root;
    @FXML
    private JFXTextField txtItemCode;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private TableView<ItemTM> tblItems;
    @FXML
    private JFXTextField txtQty;
    
    private ItemBO itemBO=(ItemBO) BOFactory.getInstance().getBO(BOFactory.BOType.ItemBO);
    
     private boolean newItm=false;
     
     
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        loadAll();
    }
    @FXML
    private void navigateToHome(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView img = (ImageView) event.getSource();
            Parent root = null;
            switch (img.getId()) {
                case "imgHome":
                    root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/pos/view/MainForm.fxml"));
                    break;
                

            }
            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();
                 primaryStage.show();
                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }

    }
    }

    @FXML
    private void btnAddNewItem_OnAction(ActionEvent event) {
        newItm=true;
        clearfields();
    }

    @FXML
    private void btnSave_OnAction(ActionEvent event) {
         if(newItm){
              save();
              loadAll();
              clearfields();
        }else if(tblItems.getSelectionModel().getFocusedIndex()>=0&& newItm==false){
                  updateItem();
                  loadAll();
        
        }

    }

    @FXML
    private void btnDelete_OnAction(ActionEvent event) {
         int selectedIndex = tblItems.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex>=0) {
            deleteItem();
            loadAll();
            clearfields();
       
        }else{
            new Alert(Alert.AlertType.ERROR, "Please select item to delete", ButtonType.OK).show();
        }
    }

    private void save(){
        try {
            String code = txtItemCode.getText();
            String description = txtDescription.getText();
            BigDecimal unitPrice =  new BigDecimal(txtUnitPrice.getText());
            int qty = Integer.valueOf(txtQty.getText()) ;
            ItemDTO itemDTO = new ItemDTO(code, description, unitPrice,qty);
            
            Boolean result = itemBO.saveItem(itemDTO);
            if(result){
                new Alert(Alert.AlertType.INFORMATION, "Item has been saved successfully", ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.INFORMATION, "Error on saving Item", ButtonType.OK).show();
            }
        } catch (Exception ex) {
            Logger.getLogger(ManageItemFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    
    private void clearfields(){
        
    txtItemCode.setText("");
    txtDescription.setText("");
    txtQty.setText("");
    txtUnitPrice.setText("");
            
    
    }
    
    
    
    private void loadAll(){
        try {
            ArrayList<ItemDTO> allItems = itemBO.getAllItem();
            ArrayList<ItemTM> addItems = new ArrayList<>();
            for (ItemDTO items : allItems) {
                ItemTM item = new ItemTM(items.getCode(), items.getDescription(), items.getUnitPrice(),items.getQtyOnHand());
                addItems.add(item);
            }
            tblItems.setItems(FXCollections.observableArrayList(addItems));
            
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    } 
    
    private void updateItem(){
       
        ItemTM selectedItem =  tblItems.getSelectionModel().getSelectedItem();
        String code = selectedItem.getCode();
        String description = txtDescription.getText();
        BigDecimal unitPrice = new BigDecimal(txtUnitPrice.getText());
        int qty = Integer.parseInt( txtQty.getText()) ;
        ItemDTO item = new ItemDTO(code, description, unitPrice,qty);
        
        try {
            Boolean result = itemBO.updateItem(item);
            if(result){
                new Alert(Alert.AlertType.INFORMATION, "Item has been Updated successfully..", ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.INFORMATION, "Error on update Item...", ButtonType.OK).show();
            }
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     private void deleteItem(){
       ItemTM item = tblItems.getSelectionModel().getSelectedItem();
       String id = item.getCode();
        try {
            boolean result = itemBO.deleteItem(id);
            if(result){
                new Alert(Alert.AlertType.INFORMATION, "Item has been deleted successfully..", ButtonType.OK).show();
            }else{
                new Alert(Alert.AlertType.INFORMATION, "Error when deleting Item..", ButtonType.OK).show();
            }
                    
        } catch (Exception ex) {
            Logger.getLogger(ManageCustomerFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
   }

    @FXML
    private void tblItemOnMouseClick(MouseEvent event) {
         newItm=false;
        ItemTM selectedItem = tblItems.getSelectionModel().getSelectedItem();
        txtDescription.setText(selectedItem.getDescription());
        txtItemCode.setText(selectedItem.getCode());
        txtQty.setText(Integer.toString(selectedItem.getQtyOnHand()));
        BigDecimal unitPrice = selectedItem.getUnitPrice();
        txtUnitPrice.setText(unitPrice.toString());
    }
       
}
