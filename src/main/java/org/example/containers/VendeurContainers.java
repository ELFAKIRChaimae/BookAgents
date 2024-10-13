package org.example.containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.Agents.AcheteurAgent;
import org.example.Agents.VendeurAgent;


public class VendeurContainers extends Application {

    public VendeurAgent vendeurAgent;
    ListView<String> listViewMessage;
    AgentContainer agentContainer;
    AgentController agentController;
    ObservableList<String>  observableList;
    public static void main(String[] args) {
             launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        startContainer();
        HBox hBox=new HBox();
        Label label=new Label("Agent name :");
        TextField fieldAgent=new TextField();
        Button buttonDeploy=new Button("Deploy");
        hBox.getChildren().addAll(label,fieldAgent,buttonDeploy);
        hBox.setPadding(new Insets(10));
        hBox.setSpacing(10);
        BorderPane borderPane =new BorderPane();
        borderPane.setTop(hBox);
        VBox vbox=new VBox();
        vbox.setPadding(new Insets(10));
        observableList= FXCollections.observableArrayList();
        listViewMessage=new ListView<String>(observableList);
        vbox.getChildren().add(listViewMessage);
        borderPane.setCenter(vbox);
        Scene scene=new Scene(borderPane,400,500);
        stage.setScene(scene);
        stage.show();
        buttonDeploy.setOnAction((actionEvent -> {
            try {
                String name=fieldAgent.getText();
                 agentController=agentContainer.createNewAgent(name,VendeurAgent.class.getName(),new Object[]{this});
                 agentController.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        }));
    }
    private void startContainer() throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        agentContainer=runtime.createAgentContainer(profile);
        agentContainer=runtime.createAgentContainer(profile);
        agentContainer.start();

    }

    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
           System.out.println(aclMessage.getContent());
          observableList.add(aclMessage.getSender().getName()+"=>"+aclMessage.getContent());

        });

}
}
