/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lk.ijse.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.pos.business.BOFactory;
import lk.ijse.pos.business.custom.CustomerBO;
import lk.ijse.pos.business.custom.ItemBO;
import lk.ijse.pos.business.custom.ItemdetailBO;
import lk.ijse.pos.business.custom.OrdersBO;
import lk.ijse.pos.business.custom.PlaceOrderBO;
import lk.ijse.pos.db.DBConnection;
import lk.ijse.pos.dto.CustomerDTO;
import lk.ijse.pos.dto.ItemDTO;
import lk.ijse.pos.dto.ItemdetailDTO;
import lk.ijse.pos.dto.OrdersDTO;
import lk.ijse.pos.view.util.tblmodel.OrderTM;


/**
 *
 * @author SDMROX
 */
public class PlaceOrderFormController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private JFXTextField txtOrderId;
    @FXML
    private JFXDatePicker txtOrderDate;
    @FXML
    private JFXComboBox<String> cmbCustomerId;
    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXComboBox<String> cmbItemCode;
    @FXML
    private JFXTextField txtDescription;
    @FXML
    private JFXTextField txtStock;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private JFXTextField txtQty;
    @FXML
    private JFXButton btnAdd;
    @FXML
    private JFXButton btnRemove;
    @FXML
    private TableView<OrderTM> tblOrderDetails;
    @FXML
    private Label lblTotal;
    @FXML
    private JFXButton btnPlaceOrder;
    @FXML
    private Label lblDisplayTot;

    private BigDecimal bigTtl = new BigDecimal(0);

    private boolean isnew = false;

    ArrayList<OrderTM> tblarray = new ArrayList<OrderTM>();

    PlaceOrderBO placeorderbo;

    private CustomerBO customerBO = (CustomerBO) BOFactory.getInstance().getBO(BOFactory.BOType.CustomerBO);
    private ItemBO itemBO = (ItemBO) BOFactory.getInstance().getBO(BOFactory.BOType.ItemBO);
    OrdersBO orderBO = (OrdersBO) BOFactory.getInstance().getBO(BOFactory.BOType.OrdersBO);
    ItemdetailBO itemdetailBO = (ItemdetailBO) BOFactory.getInstance().getBO(BOFactory.BOType.ItemdetailBO);

    public PlaceOrderFormController() {
        this.placeorderbo = (PlaceOrderBO) BOFactory.getInstance().getBO(BOFactory.BOType.PlaceOrder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tblOrderDetails.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblOrderDetails.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblOrderDetails.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        tblOrderDetails.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblOrderDetails.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("total"));
        loadAllCusID();
        loadAllItmId();
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
    private void customerCodeComboBox(ActionEvent event) {
        loadtxtCustomer();
    }

    @FXML
    private void itemCodeComboBox(ActionEvent event) {
        loadtxtItem();
    }

    @FXML
    private void addItem(ActionEvent event) {

        if (isnew == false) {
            System.out.println("SSS");
            updateTblQty();
//                bigT();
            System.out.println("DDD");
        } else {

            addItemToTeble();
//                bigT();
//                clearAll();
        }

    }

    @FXML
    private void removeItem(ActionEvent event) {
    }

    @FXML
    private void placeOrderPressed(ActionEvent event) {
        
        try {
            DBConnection.getInstance().getConnection().setAutoCommit(false);
            boolean rst1=insertRecordInToOrderTable();
            boolean rst2=insertRecordInToItemDetailTable();
            
            if(rst1 && rst2){
                DBConnection.getInstance().getConnection().commit();
                new Alert(Alert.AlertType.CONFIRMATION, "Success", ButtonType.OK).show();
            }else{
                DBConnection.getInstance().getConnection().rollback();
            }
            
        } catch (Exception ex) {
            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (Exception ex) {
                Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

//    private void clearAll() {
////    cmbCustomerId.setValue("");
//    cmbItemCode.setValue("");
////    txtCustomerName.setText("");
//    txtDescription.setText("");
////    txtOrderDate.setValue(null);
////    txtOrderId.setText("");
//    txtQty.setText("");
//    txtStock.setText("");
//    txtUnitPrice.setText("");
//        System.out.println(txtOrderDate.getValue());
//    }
    private void loadAllCusID() {
        try {

            ArrayList<String> cusids = new ArrayList<>();

            ArrayList<CustomerDTO> allCustomers = customerBO.getAllCustomer();

            for (CustomerDTO allCustomer : allCustomers) {

                String id = allCustomer.getId();
                cusids.add(id);

            }
            cmbCustomerId.setItems(FXCollections.observableArrayList(cusids));

        } catch (Exception ex) {
            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadAllItmId() {
        try {
            ArrayList<String> itmids = new ArrayList<>();

            ArrayList<ItemDTO> allItem = itemBO.getAllItem();

            for (ItemDTO itemrow : allItem) {
                String code = itemrow.getCode();
                itmids.add(code);

            }
            cmbItemCode.setItems(FXCollections.observableArrayList(itmids));
        } catch (Exception ex) {
            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadtxtCustomer() {
        try {
            String cusId = cmbCustomerId.getValue();

            CustomerDTO sltdRow = customerBO.findByID(cusId);
            String name = sltdRow.getName();
            txtCustomerName.setText(name);

        } catch (Exception ex) {
            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadtxtItem() {
        try {
            String itmcode = cmbItemCode.getValue();
            ItemDTO itmrow = itemBO.findByID(itmcode);
            txtDescription.setText(itmrow.getDescription());
            txtStock.setText(Integer.toString(itmrow.getQtyOnHand()));
            txtUnitPrice.setText(itmrow.getUnitPrice().toString());
        } catch (Exception ex) {
            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addItemToTeble() {
        String code = cmbItemCode.getValue();
        String descryption = txtDescription.getText();
        String unitPrice = txtUnitPrice.getText();
        String qty = txtQty.getText();
        int qt = Integer.parseInt(qty);
        BigDecimal total = new BigDecimal(qty).multiply(new BigDecimal(unitPrice));

        OrderTM orderTMdata = new OrderTM(code, descryption, new BigDecimal(unitPrice), qt, total);

        tblarray.add(orderTMdata);

        tblOrderDetails.setItems(FXCollections.observableList(tblarray));
        bigTtl = bigTtl.add(orderTMdata.getTotal());
        lblDisplayTot.setText(bigTtl + "");

    }

    private void updateTblQty() {
        OrderTM selectedItem = tblOrderDetails.getSelectionModel().getSelectedItem();

        ObservableList<OrderTM> items = tblOrderDetails.getItems();

        for (OrderTM item : items) {

            int ttlqty = (Integer.parseInt(txtQty.getText()) + item.getQty());
            if (item.getCode().equals(cmbItemCode.getValue())) {

                selectedItem.setQty(ttlqty);

                String unitPrice = txtUnitPrice.getText();
                selectedItem.setTotal(new BigDecimal(unitPrice).multiply(new BigDecimal(ttlqty)));
                loadtxtItem();
                int index = items.indexOf(item);
                items.set(index, selectedItem);
                
//                System.out.println(tblarray);

                bigTtl = bigTtl.add(new BigDecimal(txtQty.getText()).multiply(new BigDecimal(txtUnitPrice.getText())));
                lblDisplayTot.setText(bigTtl + "");

                return;
            }
        }

    }

    @FXML
    private void tblOnmouseClicked(MouseEvent event) {
        isnew = false;
    }

    @FXML
    private void itmIdOnmunuseClick(MouseEvent event) {
        isnew = true;
    }

//    private void bigT(){
//        ObservableList<OrderTM> itmtt = tblOrderDetails.getItems();
//        for (OrderTM item : itmtt) {
//            BigDecimal Total=new BigDecimal(bigTtl).add(item.getTotal());
//            
//            lblDisplayTot.setText(Total.toString());
//        }
//    
//    
//    }
    
    
    
    private boolean insertRecordInToOrderTable(){
        String odrId = txtOrderId.getText();
        String cusId = cmbCustomerId.getValue();
        LocalDate odrDate = txtOrderDate.getValue();
        Date date=new Date();
        boolean rst=true;
        try {
             date = new SimpleDateFormat("yyyy-MM-dd").parse(odrDate.toString());
            
            
        } catch (ParseException ex) {
            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
           OrdersDTO ordersDTO = new OrdersDTO(odrId, date, cusId);
            try {
                 rst = orderBO.saveOrders(ordersDTO);
                
                
            } catch (Exception ex) {
                rst=false;
                Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return rst;
    }
    
    
    private boolean insertRecordInToItemDetailTable(){
    
        String odrId = txtOrderId.getText();
        boolean rst=true;
        for (OrderTM orderTM : tblarray) {
            
            ItemdetailDTO itemdetailDTO = new ItemdetailDTO(odrId, orderTM.getCode(), orderTM.getQty(), orderTM.getUnitPrice());
            try {
                 rst = itemdetailBO.saveItemdetail(itemdetailDTO);
            } catch (Exception ex) {
                rst=false;
                Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        return rst;
    }
    
    
//     private boolean updateQtuOnHand(){
//        String itmCode = cmbItemCode.getValue();
//        
//        try {
//            ArrayList<ItemDTO> allItem = itemBO.getAllItem();
//            
//        } catch (Exception ex) {
//            Logger.getLogger(PlaceOrderFormController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//    }
}
