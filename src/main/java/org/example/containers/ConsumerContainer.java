package org.example.containers;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
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
import org.example.Agents.ConsumerAgent;


public class ConsumerContainer  extends Application {
    ObservableList<String> observableListData;
    public ConsumerAgent consumerAgent;

    public static void main(String[] args) throws ControllerException {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
          startContainer();
          primaryStage.setTitle("Consumer Container");
          BorderPane borderPane=new BorderPane();
          HBox hBox1=new HBox();
          hBox1.setPadding(new Insets(10));
          hBox1.setSpacing(10);
          Label labelBook=new Label("Book Name :");
          TextField textfieldBookName=new TextField();
          Button buttonOk=new Button("OK");
          hBox1.getChildren().addAll(labelBook,textfieldBookName,buttonOk);
          borderPane.setTop(hBox1);
          observableListData= FXCollections.observableArrayList();
          ListView<String> listViewMessagec= new ListView<>(observableListData);
          VBox vBox2=new VBox();
          vBox2.setPadding(new Insets(10));
          vBox2.setSpacing(10);
          vBox2.getChildren().add(listViewMessagec);
          borderPane.setCenter(vBox2);
          buttonOk.setOnAction(actionEvent -> {
              String BookName=textfieldBookName.getText();
              GuiEvent event =new GuiEvent(this,1);
              event.addParameter(BookName);
              consumerAgent.onGuiEvent(event) ;
          });
          Scene scene=new Scene( borderPane,400,600);
          primaryStage.setScene(scene);
          primaryStage.show();
    }

    private  void startContainer() throws StaleProxyException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileImp=new ProfileImpl();
        profileImp.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container= (AgentContainer) runtime.createAgentContainer(profileImp);
        AgentController consumerController=container.createNewAgent("Consumer", ConsumerAgent.class.getName(),new Object[]{this});//par exemple dans le paramétre on donne le nom de livre  // trois arguments 1-> le nom de l'agent->2 le nom de classe de container ->3les parametres
        consumerController.start();
    }
    public void logMessage(ACLMessage aclMessage){
        Platform.runLater(()->{
            System.out.println(aclMessage.getContent());
            observableListData.add(aclMessage.getSender().getName()+"=>"+aclMessage.getContent());
        });

    }
}
