package org.example.Agents;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import org.example.containers.ConsumerContainer;

public class ConsumerAgent extends GuiAgent {
    protected ConsumerContainer consumerContainer;

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            consumerContainer = (ConsumerContainer) getArguments()[0];
            consumerContainer.consumerAgent = this;
        }
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage aclMessage = receive();
                if (aclMessage != null) {
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.CONFIRM:
                            consumerContainer.logMessage(aclMessage);
                            break;
                    }
                    System.out.println("Sender :" + aclMessage.getSender().getName());
                    System.out.println("Content " + aclMessage.getContent());
                    System.out.println("SpeechAct" + ACLMessage.getPerformative(aclMessage.getPerformative()));
                    consumerContainer.logMessage(aclMessage);
                } else {
                    System.out.println("bloc ...");
                    block();
                }
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    public void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            String bookName = (String) guiEvent.getParameter(0);
            System.out.println("Agent=>" + getAID().getName() + "=>" + bookName);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(bookName);
            aclMessage.addReceiver(new AID("AcheteurAgent", AID.ISLOCALNAME));
            send(aclMessage);
        }
    }
}

