/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appechoexchange;

import echoExchange.Client;
import filegetclient.fileClient;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author oleg
 */
public class AppEchoExchange extends Application {
    
    private ComboBox<Combo> cbRegion; 
    private ComboBox<Combo> cbNetwork; 
    String appIp = "localhost";//"31.173.217.131"; //IP app server
    int appPort = 15444;        //   port
    private final static  String fconfPath = 
            System.getProperty("user.dir");
    private final static  String fconf = 
            fconfPath+File.separator+"ovpn/proxy.txt" ;
    private TextField keyFild;
    private Button btnConnect;
    private Button btnKey;
    
    
    @Override
    public void start(Stage primaryStage) {

        Label labelr = new Label("Please, select a Region to work in.");
        Label labeln = new Label("Please, select a Network to work in.");
        cbNetwork = new ComboBox<Combo>();
        
        cbRegion.setOnAction((ActionEvent event) -> {
            if (cbRegion.getValue().getId()>0){
                loadNetwork(cbRegion.getValue().getId());
            }
        });
        
        cbNetwork.setOnAction((ActionEvent event) -> {
            if (cbNetwork.getValue().getId()>0){
                loadVpnProxy(cbNetwork.getValue().getId());
                loadproxyGw(cbNetwork.getValue().getId());
            }
        });
        
        
       // btnKey = new Button("GetConfig!");
        btnConnect = new Button("Go!");

        
        StackPane root = new StackPane();
        Pane pane = new Pane();
        labelr.setLayoutX(10.0);
        labelr.setLayoutY(10.0);
        labelr.setTextFill(Color.web("#0076a3"));
        labeln.setLayoutX(10.0);
        labeln.setLayoutY(70.0);
        labeln.setTextFill(Color.web("#0076a3"));
        
        cbRegion.setLayoutX(10.0);
        cbRegion.setPrefWidth(200.0);
        cbRegion.setLayoutY(40.0);
        
        cbNetwork.setLayoutX(10.0);
        cbNetwork.setPrefWidth(200.0);
        cbNetwork.setLayoutY(100.0);
        
        pane.getChildren().add(cbRegion);
        pane.getChildren().add(cbNetwork);
        pane.getChildren().add(labelr);
        pane.getChildren().add(labeln);
        root.getChildren().add(pane);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("Выбор сети");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /*
    Загружаем список регионов
    */
    public void init(){
        cbRegion = new ComboBox<Combo>();
        Client cl = new Client(appIp,appPort,"region:0") ;
        try {
            cl.start();
        } catch (Exception ex) {
        }
        try {
            cbRegion = new ComboBox<Combo>();
            cbRegion.getItems().addAll(cl.getListCombo());
            cbRegion.getSelectionModel().selectFirst();
        } catch (Exception ex) {
        }    
    }
   
    /*
    Загружаем список Сетей
    */
    public void loadNetwork(int reg){
        Client cl = new Client(appIp,appPort,"network:"+
                String.valueOf(reg));
        try {
            cl.start();
        } catch (Exception ex) {
        }
        try {
            cbNetwork.getItems().clear();
            cbNetwork.getItems().addAll(cl.getListCombo());
            cbNetwork.getSelectionModel().selectFirst();
        } catch (Exception ex) {
        }    
        
    }
    
    public void loadVpnProxy(int network){
        Client cl1 = new Client(appIp,appPort,"file1:"+
                String.valueOf(network));
        try {
            cl1.start();
        } catch (Exception ex) {
        }
        Client cl2 = new Client(appIp,appPort,"file2:"+
                String.valueOf(network));
        try {
            cl2.start();
        } catch (Exception ex) {
        }
        Client cl3 = new Client(appIp,appPort,"file3:"+
                String.valueOf(network));
        try {
            cl3.start();
        } catch (Exception ex) {
        }
        Client cl4 = new Client(appIp,appPort,"file4:"+
                String.valueOf(network));
        try {
            cl4.start();
        } catch (Exception ex) {
        }
    }
    
    private void loadproxyGw(int network){
        Client cl = new Client(appIp,appPort,"proxy:"+
                String.valueOf(network));
        try {
            cl.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try{
            ArrayList ara = (ArrayList) cl.getListServIp();
            try (FileWriter writer = new FileWriter(fconf)) {
                int size = ara.size();
                for (int i=0;i<size;i++) {
                    String str = ara.get(i).toString();
                    writer.write(str);
                    if(i < size-1){
                        writer.write("\n");
                    }
                }
            
        }
        }catch(Exception ex){
           ex.printStackTrace();
        }
        
    }

}
