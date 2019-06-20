package UserInterface;

import Classes.Asset;
import Classes.Component;
import Classes.Depreciation;
import Classes.Service;
import Database.DBConnection;
import QRGenerator.*;
import com.google.zxing.WriterException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;

import javax.swing.*;

import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;


import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import static java.util.concurrent.TimeUnit.DAYS;

public class Controller implements Initializable{

    @FXML private Tab assetsTab;
    @FXML private TableView<Asset> tableAssets;
    @FXML private TableColumn<Asset, Image> pictureCol;
    @FXML private TableColumn<Asset, String> serialCol, nameCol, descriptionCol, groupCol, locationCol, conditionCol, lifeCol;
    @FXML private TableView<Component> tableComponent;
    @FXML private TableColumn<Component, String> compNameCol, compSerialCol, compConditionCol, compDescCol;
    @FXML private Button searchButton, addButton, imageButton, resetButton, updateAsset, updateAddInfo, saveService, calcDepre, clearDepre, qrPrint, generateService, printDepreciation, retireButton, verifyButton;
    @FXML private TextField fieldName, fieldSerial, fieldBarcode, fieldModel, fieldBrand, fieldDescription, fieldCondition, fieldCost, fieldVendor, fieldGroup, fieldLocation, fieldDepartment, fieldCustodian, fieldImagepath, fieldQrpath;
    @FXML private TextField editName, editDescription, editModel, editSerial, editBarcode, editBrand, editVendor, editCost, editCondition, editGroup, editLocation, editDepartment, editCustodian;
    @FXML private TextField serviceProv, contract, serviceCost, serviceSerial, serviceName;
    @FXML private DatePicker fieldPurchaseDate, editPurchaseDate, serviceDate, warranty;
    @FXML private TextArea serviceDesc;
    @FXML private ImageView imageView,qrImageView, assetInfoImage, assetInfoQR;
    @FXML private Image image,qrImage;
    @FXML private TreeView<String> homeTreeview, reportTreeview;
    @FXML private TreeItem<String> rootItem, groupItem, locationItem, departmentItem, custodianItem, newitem;
    @FXML private StackPane treePane;
    @FXML private TextField depreName, depreSerial, depreMethod, depreDate, depreCost, depreYear, depreMonth;
    @FXML private TableView<Depreciation> tableDepreciation;
    @FXML private TableColumn<Depreciation, String> depremonthCol, depreagedCol, deprebookCol, deprethisCol, depreaccCol;
    @FXML private RadioButton depreComm, depreFis;
    @FXML private ToggleGroup toggleGroup;
    @FXML private TableView<Service> tableService;
    @FXML private TableColumn<Service, String> sernameCol, serdateCol, serwarrantyCol, serprovCol, sercontCol, sercostCol, serdesCol;
    @FXML private Label reportDate, reportTitle;
    @FXML private Pane reportPane;
    @FXML private TextField retireName, retireSerial, retireMode;
    @FXML private DatePicker retireDate;

    //create list to hold database data
    private ObservableList<Asset>data;
    private DBConnection dc;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        populateTable("All Assets", "all");
        rootItem=new TreeItem<String>("All Assets");
        rootItem.setExpanded(true);
        groupItem=makeBranch("Group",rootItem);
        locationItem=makeBranch("Location",rootItem);
        departmentItem=makeBranch("Department",rootItem);
        custodianItem=makeBranch("Custodian",rootItem);
        homeTreeview.setRoot(rootItem);

        homeTreeview.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends TreeItem<String>> observable,
                            TreeItem<String> old_val, TreeItem<String> new_val) {
                        TreeItem<String> selectedItem = new_val;
                        String selected = selectedItem.getValue();

                        if (selected=="All Assets") {
                            populateTable(selected,"all");
                        }

                        else if (selected=="Group" || selected=="Location" || selected=="Department" || selected=="Custodian") {
                            populateTree(selected);
                        }

                        else {
                                if(selectedItem.getParent().getValue()=="Group"){populateTable(selected,"class");}
                                else if(selectedItem.getParent().getValue()=="Location"){populateTable(selected,"location");}
                                else if(selectedItem.getParent().getValue()=="Department"){populateTable(selected,"department");}
                                else if(selectedItem.getParent().getValue()=="Custodian"){populateTable(selected,"custodian");}
                        }
                    }
                });

    }

    public TreeItem<String> makeBranch(String title, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem(title);
        parent.getChildren().add(item);
        return item;
    }

    public void populateTree(String criteria) {
        dc = new DBConnection();
        Connection conn = dc.getConnection();
        String record=null;
        TreeItem<String> parent=null;

        if (criteria=="Group") {record="class"; parent=groupItem;}
        else if (criteria=="Location"){record="location"; parent=locationItem;}
        else if (criteria=="Department"){record="department"; parent=departmentItem;}
        else if (criteria=="Custodian"){record="custodian"; parent=custodianItem;}

        try {
            ResultSet rs = conn.createStatement().executeQuery("SELECT DISTINCT "+record+" FROM assets;");
            while (rs.next()) {
                newitem=makeBranch(rs.getString(1),parent);
            }
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        }
    }

    public void populateTable(String filter, String recordName) {
        dc = new DBConnection();
        Connection conn = dc.getConnection();
        data = FXCollections.observableArrayList();
        String query;
        if(filter=="All Assets" && recordName=="all") {query="SELECT asset_image,name,serial_number,asset_desc,condition,class,location,purchase_date FROM assets WHERE asset_status=1";}
        else {query="SELECT asset_image,name,serial_number,asset_desc,condition,class,location,purchase_date FROM assets WHERE asset_status=1 AND"+recordName+"='"+filter+"';";}

        //execute query and store data in resultset
        try {
            ResultSet rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                data.add(new Asset(new Image(new File(rs.getString(1)).toURI().toString(),200,200,true,true), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),rs.getString(8)));
            }
            tableAssets.setItems(data);
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        }

        pictureCol.setCellValueFactory(new PropertyValueFactory<Asset, Image>("image"));
        pictureCol.setCellFactory(param -> {
            //Set up the ImageView
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(55);
            imageview.setFitWidth(70);

            //Set up the Table
            TableCell<Asset,Image> cell = new TableCell<Asset,Image>(){
                public void updateItem(Image item, boolean empty) {
                    if (item != null){
                        imageview.setImage(item);
                        setGraphic(imageview); }
                }
            };
            return cell;
        });

        nameCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("name"));
        serialCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("serial"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("description"));
        conditionCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("condition"));
        groupCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("group"));
        locationCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("location"));
        lifeCol.setCellValueFactory(new PropertyValueFactory<Asset, String>("life"));

        tableAssets.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (! row.isEmpty() && event.getButton()== MouseButton.PRIMARY && event.getClickCount() == 1) {

                    Asset clickedRow = row.getItem();
                    String name = clickedRow.getName();
                    String serial= clickedRow.getSerial();

                    dc = new DBConnection();
                    Connection con = dc.getConnection();
                    data = FXCollections.observableArrayList();

                    try {
                        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM assets WHERE name='"+name+"' AND serial_number='"+serial+"';");
                        while (rs.next()) {
                            serviceName.setText((rs.getString(2))); serviceSerial.setText(rs.getString(3));
                            editName.setText(rs.getString(2)); editSerial.setText(rs.getString(3)); editBarcode.setText(rs.getString(4));
                            editModel.setText(rs.getString(5)); editBrand.setText(rs.getString(6));
                            editCost.setText(rs.getString(7)); editVendor.setText(rs.getString(9));
                            editDescription.setText(rs.getString(10)); editCondition.setText(rs.getString(11));
                            editGroup.setText(rs.getString(12)); editLocation.setText(rs.getString(13)); editDepartment.setText(rs.getString(14));
                            editCustodian.setText(rs.getString(15)); assetInfoImage.setImage(new Image(new File(rs.getString(17)).toURI().toString(),220,200,false,true));
                            assetInfoQR.setImage(new Image(new File(rs.getString(18)).toURI().toString(),250,250,false,true));
                        }
                    }
                    catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex);
                    }
                    finally {
                        if (conn != null) {
                            try {
                                conn.close();
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, e.getMessage());
                            }
                        }
                    }

                    dc = new DBConnection();
                    Connection compConn = dc.getConnection();
                    ObservableList<Component>data2 = FXCollections.observableArrayList();

                    try {
                        ResultSet rs = compConn.createStatement().executeQuery("SELECT * FROM assets t1 LEFT JOIN components t2 ON t1.asset_id=t2.asset_id WHERE name='"+name+"' AND serial_number='"+serial+"';");
                        while (rs.next()) {
                            data2.add(new Component(rs.getString(21),rs.getString(22),rs.getString(23),rs.getString(24)));
                        }
                        tableComponent.setItems(data2);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex);
                    }
                    finally {
                        if (compConn != null) {
                            try {
                                compConn.close();
                            } catch (SQLException e) {
                                JOptionPane.showMessageDialog(null, e.getMessage());
                            }
                        }
                    }
                    compNameCol.setCellValueFactory(new PropertyValueFactory<Component, String>("name"));
                    compSerialCol.setCellValueFactory(new PropertyValueFactory<Component,String>("serial"));
                    compConditionCol.setCellValueFactory(new PropertyValueFactory<Component, String>("condition"));
                    compDescCol.setCellValueFactory(new PropertyValueFactory<Component, String>("description"));
                }
            });
            return row ;
        });
    }

    //Add Asset to Database
    @FXML private void saveData(ActionEvent event){
        if(fieldName.getText().isEmpty() || fieldSerial.getText().isEmpty() || fieldCost.getText().isEmpty() || fieldGroup.getText().isEmpty() || fieldLocation.getText().isEmpty() || fieldPurchaseDate.getValue()==null ){JOptionPane.showMessageDialog(null, "Fill All Required Fields");}
        else {
            String name = fieldName.getText(), serial = fieldSerial.getText(), barcode = fieldBarcode.getText(), model = fieldModel.getText(),
                    brand = fieldBrand.getText(), description = fieldDescription.getText(), condition = fieldCondition.getText(),
                    vendor = fieldVendor.getText(), group = fieldGroup.getText(), location = fieldLocation.getText(),
                    department = fieldDepartment.getText(), custodian = fieldCustodian.getText(), image = fieldImagepath.getText(), qrpath = fieldQrpath.getText();
            double cost = Double.parseDouble(fieldCost.getText());
            java.sql.Date purchaseDate = java.sql.Date.valueOf(fieldPurchaseDate.getValue());

            dc = new DBConnection();
            Connection conn = dc.getConnection();
            String insertTable = "INSERT INTO assets (name,serial_number,barcode,model,brand,cost,purchase_date,vendor,asset_desc,condition,asset_status,class,location,department,custodian,asset_image,qr_image) " +
                    "VALUES" + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            //execute query and store data in resultset
            try {
                PreparedStatement stm = conn.prepareStatement(insertTable);
                stm.setString(1, name);
                stm.setString(2, serial);
                stm.setString(3, barcode);
                stm.setString(4, model);
                stm.setString(5, brand);
                stm.setDouble(6, cost);
                stm.setDate(7, purchaseDate);
                stm.setString(8, vendor);
                stm.setString(9, description);
                stm.setString(10, condition);
                stm.setInt(11, 1);
                stm.setString(12, group);
                stm.setString(13, location);
                stm.setString(14, department);
                stm.setString(15, custodian);
                stm.setString(16, image);
                stm.setString(17, qrpath);
                stm.executeUpdate();

                JOptionPane.showMessageDialog(null, "Successfully Added!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
            clear();
        }
    }

    private void clear() {
        fieldName.clear(); fieldSerial.clear(); fieldBarcode.clear(); fieldModel.clear(); fieldBrand.clear();
        fieldDescription.clear(); fieldCondition.clear(); fieldCost.clear(); fieldVendor.clear(); fieldQrpath.clear();
        fieldGroup.clear(); fieldLocation.clear(); fieldDepartment.clear(); fieldCustodian.clear(); fieldImagepath.clear();
        imageView.setImage(null); qrImageView.setImage(null);
    }

    @FXML private void resetFields(ActionEvent event) {
        clear();
    }

    @FXML private void addImage(ActionEvent event) {
        FileChooser fileChooser=new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fieldImagepath.setText(file.getAbsolutePath());
            image = new Image(file.toURI().toString(),200,200,true,true);
            imageView.setImage(image);
        }
    }

    @FXML private void addQRCode(ActionEvent event) {
        QRCodeGenerator qr = new QRCodeGenerator();
        String qrSerial=fieldSerial.getText();
        String QR_CODE_IMAGE_PATH = "/home/eriq/Documents/Projects/Java/Asset Management/Images/QRCodes/"+qrSerial+".png";
        try {
            qr.generateQRCodeImage(qrSerial,QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            JOptionPane.showMessageDialog(null, "Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Could not generate QR Code, IOException :: " + e.getMessage());
        }

        File file2 = new File(QR_CODE_IMAGE_PATH);
        qrImage = new Image(file2.toURI().toString(),250,250,true,true);
        qrImageView.setImage(qrImage);
        fieldQrpath.setText(QR_CODE_IMAGE_PATH);
    }

    @FXML private void updateAssetinfo(ActionEvent event) {
        if(editPurchaseDate.getValue()==null){JOptionPane.showMessageDialog(null,"Enter Purchase Date");}
        else {
            String name = editName.getText(), description = editDescription.getText(), model = editModel.getText(), serial = editSerial.getText(),
                    barcode = editBarcode.getText(), brand = editBrand.getText(), vendor = editVendor.getText(), condition = editCondition.getText(),
                    group = editGroup.getText(), location = editLocation.getText(), department = editDepartment.getText(), custodian = editCustodian.getText();
            Double cost = Double.parseDouble(editCost.getText());
            java.sql.Date date = java.sql.Date.valueOf(editPurchaseDate.getValue());

            dc = new DBConnection();
            Connection conn = dc.getConnection();
            String updatesql = "UPDATE assets SET name=?,barcode=?,model=?,brand=?,cost=?,purchase_date=?, vendor=?,asset_desc=?,condition=?,class=?,location=?,department=?,custodian=? WHERE serial_number=?;";

            try {
                PreparedStatement stm = conn.prepareStatement(updatesql);
                stm.setString(1, name);
                stm.setString(2, barcode);
                stm.setString(3, model);
                stm.setString(4, brand);
                stm.setDouble(5, cost);
                stm.setDate(6, date);
                stm.setString(7, vendor);
                stm.setString(8, description);
                stm.setString(9, condition);
                stm.setString(10, group);
                stm.setString(11, location);
                stm.setString(12, department);
                stm.setString(13, custodian);
                stm.setString(14, serial);
                stm.executeUpdate();

                JOptionPane.showMessageDialog(null, "Successfully Updated!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
        }
    }

    @FXML private void addService(ActionEvent event) {
        if(serviceDate.getValue()==null || serviceProv.getText().isEmpty() || serviceCost.getText().isEmpty()){JOptionPane.showMessageDialog(null,"Fill All Required Fields");}
        else {
            String name = serviceName.getText(), serial = serviceSerial.getText();
            int assetId;
            dc = new DBConnection();
            Connection conn = dc.getConnection();
            String service = "SELECT * FROM assets t1 LEFT JOIN service t2 ON t1.asset_id=t2.asset_id WHERE name='" + name + "' AND serial_number='" + serial + "';";
            try {
                ResultSet rs = conn.createStatement().executeQuery(service);
                java.sql.Date warr = java.sql.Date.valueOf(warranty.getValue());
                java.sql.Date servDate = java.sql.Date.valueOf(serviceDate.getValue());
                while (rs.next()) {
                    assetId = rs.getInt(1);
                    String serviceSql = "INSERT INTO service (asset_id,service_provider,service_date,warranty,contract,cost,service_description) VALUES(?,?,?,?,?,?,?);";
                    try {
                        PreparedStatement stm = conn.prepareStatement(serviceSql);
                        stm.setInt(1, assetId);
                        stm.setString(2, serviceProv.getText());
                        stm.setDate(3, servDate);
                        stm.setDate(4, warr);
                        stm.setString(5, contract.getText());
                        stm.setDouble(6, Double.parseDouble(serviceCost.getText()));
                        stm.setString(7, serviceDesc.getText());
                        stm.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Service Information Successfully Added!");
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
            serviceProv.clear();
            contract.clear();
            serviceCost.clear();
            serviceDesc.clear();
        }
    }

    @FXML private void calculateDepre(ActionEvent event) {
        if((depreYear.getText().equals("0") && depreMonth.getText().equals("0")) || (depreName.getText().isEmpty() && depreSerial.getText().isEmpty())){JOptionPane.showMessageDialog(null,"Enter All Required Information");}
        else {
            String name = depreName.getText(), serial = depreSerial.getText();
            ObservableList<Depreciation> data = FXCollections.observableArrayList();
            dc = new DBConnection();
            Connection conn = dc.getConnection();
            String query = "SELECT cost,purchase_date,name,serial_number FROM assets WHERE asset_status=1 AND serial_number='" + serial + "'OR name='" + name + "';";
            try {
                ResultSet rs = conn.createStatement().executeQuery(query);
                while (rs.next()) {
                    double cost = rs.getDouble(1);
                    Date date = rs.getDate(2);
                    depreMethod.setText("Straight Line");
                    depreDate.setText(rs.getString(2));
                    depreCost.setText(rs.getString(1));
                    depreName.setText(rs.getString(3));
                    depreSerial.setText(rs.getString(4));
                    Date today = new Date();
                    long time = today.getTime() - date.getTime();
                    long days = DAYS.convert(time, TimeUnit.MILLISECONDS) / 30;
                    int months = (int) days;

                    int uppermonth = 1;
                    int life = Integer.parseInt(depreYear.getText()) * 12 + Integer.parseInt(depreMonth.getText());
                    double thisMonth = cost / life;
                    double bookValue = cost * 0.8;

                    while (uppermonth <= months) {
                        String aged = uppermonth + " months";
                        int month = uppermonth;
                        bookValue = bookValue - thisMonth;
                        double accDepre = cost - bookValue;
                        data.add(new Depreciation(Integer.toString(month), aged, Double.toString(bookValue), Double.toString(thisMonth), Double.toString(accDepre)));
                        uppermonth++;
                    }
                }
                tableDepreciation.setItems(data);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
            depremonthCol.setCellValueFactory(new PropertyValueFactory<Depreciation, String>("month"));
            depreagedCol.setCellValueFactory(new PropertyValueFactory<Depreciation, String>("aged"));
            deprebookCol.setCellValueFactory(new PropertyValueFactory<Depreciation, String>("bookValue"));
            deprethisCol.setCellValueFactory(new PropertyValueFactory<Depreciation, String>("thisMonth"));
            depreaccCol.setCellValueFactory(new PropertyValueFactory<Depreciation, String>("accDepre"));
        }
    }

    @FXML private void clearDepreciation(ActionEvent event){
        depreMethod.clear(); depreDate.clear(); depreCost.clear();
        depreName.clear(); depreSerial.clear(); tableDepreciation.getItems().clear();
    }

    @FXML private void printQr(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(assetInfoQR.getScene().getWindow())){
            boolean success = job.printPage(assetInfoQR);
            if (success) {
                job.endJob();
            }
        }
    }

    @FXML private void serviceReport(ActionEvent event) {
        String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date());
        dc = new DBConnection();
        Connection conn = dc.getConnection();
        ObservableList<Service>data = FXCollections.observableArrayList();

        try{
            ResultSet rs = conn.createStatement().executeQuery("SELECT t2.name,t1.service_provider,t1.service_date,t1.warranty,t1.contract,t1.cost,t1.service_description FROM service t1 JOIN assets t2 on t1.asset_id=t2.asset_id;");
            while(rs.next()){
                data.add(new Service(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)));
            }
            tableService.setItems(data);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        }

        sernameCol.setCellValueFactory(new PropertyValueFactory<Service, String>("assetName"));
        serdateCol.setCellValueFactory(new PropertyValueFactory<Service, String>("servicedate"));
        serwarrantyCol.setCellValueFactory(new PropertyValueFactory<Service, String>("warranty"));
        serprovCol.setCellValueFactory(new PropertyValueFactory<Service, String>("provider"));
        sercontCol.setCellValueFactory(new PropertyValueFactory<Service, String>("contract"));
        sercostCol.setCellValueFactory(new PropertyValueFactory<Service, String>("sercost"));
        serdesCol.setCellValueFactory(new PropertyValueFactory<Service, String>("description"));

        reportTitle.setText("Service Report");
        reportDate.setText(today);
    }

    @FXML private void printDepreciation(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(tableDepreciation.getScene().getWindow())){
            boolean success = job.printPage(tableDepreciation);
            if (success) {
                job.endJob();
            }
        }
    }

    @FXML private void printReports(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(reportPane.getScene().getWindow())){
            boolean success = job.printPage(reportPane);
            if (success) {
                job.endJob();
            }
        }
    }

    @FXML private void clearReport(ActionEvent event) {
        reportTitle.setText("Report Title");
        reportDate.setText("Date Generated");
        tableService.getItems().clear();
    }

    @FXML private void retireAsset(ActionEvent event) {
        if(retireName.getText().isEmpty() || retireSerial.getText().isEmpty() || retireDate.getValue()==null){JOptionPane.showMessageDialog(null,"Enter All Required Information");}
        else {
            String name=retireName.getText(), serial=retireSerial.getText(), mode=retireMode.getText();
            java.sql.Date date = java.sql.Date.valueOf(retireDate.getValue());

            dc = new DBConnection();
            Connection conn = dc.getConnection();
            String sql="UPDATE assets SET asset_status=0 WHERE name=? OR serial_number=?;";
            String insert="INSERT INTO retirement (asset_name,asset_serial,retirement_mode,retirement_date) VALUES(?,?,?,?);";

            try{
                PreparedStatement stm = conn.prepareStatement(sql);
                stm.setString(1,name);
                stm.setString(2,serial);
                stm.executeUpdate();

                PreparedStatement stm2 = conn.prepareStatement(insert);
                stm2.setString(1,name);
                stm2.setString(2,serial);
                stm2.setString(3,mode);
                stm2.setDate(4,date);
                stm2.executeUpdate();

                JOptionPane.showMessageDialog(null,"Asset Retired Successfully!");
            }
            catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
            retireName.clear();
            retireSerial.clear();
            retireMode.clear();
        }
    }

    @FXML private void verifyAsset(ActionEvent event){
        if(retireName.getText().isEmpty() && retireSerial.getText().isEmpty()){JOptionPane.showMessageDialog(null,"Enter Asset Name or Serial");}
        else{
            String name=retireName.getText(), serial=retireSerial.getText();
            dc = new DBConnection();
            Connection conn = dc.getConnection();

            String select="SELECT name,serial_number FROM assets WHERE asset_status=1 AND name='"+name+"' OR serial_number='"+serial+"';";
            try {
                ResultSet rs = conn.createStatement().executeQuery(select);
                while (rs.next()) {
                    retireName.setText(rs.getString(1));
                    retireSerial.setText(rs.getString(2));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                    }
                }
            }
        }
    }
}
