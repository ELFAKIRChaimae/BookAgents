package org.example.containers;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;




public class SimpleContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileImp=new ProfileImpl();
        profileImp.setParameter(ProfileImpl.MAIN_HOST,"localhost");
        AgentContainer container= (AgentContainer) runtime.createAgentContainer(profileImp);
        container.start() ;
    }
}
