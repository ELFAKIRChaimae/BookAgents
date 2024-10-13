package org.example.containers;
import jade.core.ProfileImpl;
import jade.core.Runtime ;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MyMainContainers {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime=Runtime.instance();
        ProfileImpl profileImpl=new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.GUI,"true");
        AgentContainer mainContainer=  runtime.createMainContainer(profileImpl);
        mainContainer.start() ;
    }}
