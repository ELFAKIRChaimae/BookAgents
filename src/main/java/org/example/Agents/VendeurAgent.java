package org.example.Agents;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import org.example.containers.VendeurContainers;

import java.util.Random;

public class VendeurAgent extends GuiAgent {

    private VendeurContainers gui;

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            gui = (VendeurContainers) getArguments()[0];
            gui.vendeurAgent = this;
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription agentDescription = new DFAgentDescription();
                agentDescription.setName(getAID());
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente_livres");
                agentDescription.addServices(serviceDescription);
                try {
                    DFService.register(myAgent, agentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    gui.logMessage(aclMessage);
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CFP:
                            ACLMessage reply = aclMessage.createReply();
                            reply.setPerformative(ACLMessage.PROPOSE);
                            reply.setContent(String.valueOf(500 + new Random().nextInt(1000)));
                            send(reply);
                            break;
                        case ACLMessage.ACCEPT_PROPOSAL:
                            ACLMessage aclMessage1 = aclMessage.createReply();
                            aclMessage1.setPerformative(ACLMessage.AGREE);
                            send(aclMessage1);
                            break;
                    }
                } else {
                    block();
                }
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
