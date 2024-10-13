package org.example.containers;

import jade.core.ProfileImpl;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jade.core.Runtime ;
import org.example.Agents.AcheteurAgent;
import javafx.scene.control.ListView;



public class AcheteurContainers extends Application {

    public AcheteurAgent acheteurAgent;
    ListView<String> listViewMessage;
    ObservableList<String>  observableList;
    public static void main(String[] args) {
             launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        stage.setTitle("acheteur");
        BorderPane borderPane =new BorderPane();
        VBox vbox=new VBox();
        vbox.setPadding(new Insets(10));
        observableList= FXCollections.observableArrayList();
        listViewMessage=new ListView<String>(observableList);
        vbox.getChildren().add(listViewMessage);
        borderPane.setCenter(vbox);
        Scene scene=new Scene(borderPane,400,500);
        stage.setScene(scene);
        stage.show();
    }
    private void startContainer() throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer agentContainer=runtime.createAgentContainer(profile);
        AgentController agentController=agentContainer.createNewAgent("AcheteurAgent",AcheteurAgent.class.getName(),new Object[]{this});
        agentController.start();

    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
           System.out.println(aclMessage.getContent());
          observableList.add(aclMessage.getSender().getName()+"=>"+aclMessage.getContent());
  });

}
}
