package org.example.Agents;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.example.containers.AcheteurContainers;

import java.util.ArrayList;
import java.util.List;

public class AcheteurAgent extends GuiAgent {
    private AcheteurContainers gui;
    private AID[] vendeurs;

    @Override
    protected void setup() {
        if (getArguments().length == 1) {
            gui = (AcheteurContainers) getArguments()[0];
            gui.acheteurAgent = this;
        }

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        parallelBehaviour.addSubBehaviour(new TickerBehaviour(this, 50000) {
            @Override
            protected void onTick() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("transaction");
                serviceDescription.setName("vente_livres");
                dfAgentDescription.addServices(serviceDescription);
                try {
                    DFAgentDescription[] results = DFService.search(myAgent, dfAgentDescription);
                    vendeurs = new AID[results.length];
                    for (int i = 0; i < vendeurs.length; i++) {
                        vendeurs[i] = results[i].getName();
                    }
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });

        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {
            private int compteur = 0;
            private List<ACLMessage> aclMessages = new ArrayList<>();

            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.or(
                        MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                        MessageTemplate.or(
                                MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
                                MessageTemplate.or(
                                        MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                                        MessageTemplate.MatchPerformative(ACLMessage.REFUSE))));

                ACLMessage aclMessage = receive(messageTemplate);
                if (aclMessage != null) {
                    switch (aclMessage.getPerformative()) {
                        case ACLMessage.REQUEST:
                            String livre = aclMessage.getContent();
                            ACLMessage aclMessage2 = new ACLMessage(ACLMessage.CFP);
                            aclMessage2.setContent(livre);
                            for (AID aid : vendeurs) {
                                aclMessage2.addReceiver(aid);
                            }
                            send(aclMessage2);
                            break;
                        case ACLMessage.PROPOSE:
                            ++compteur;
                            aclMessages.add(aclMessage);
                            if (compteur == vendeurs.length) {
                                ACLMessage meilleureOffre = aclMessages.get(0);
                                double mini = Double.parseDouble(meilleureOffre.getContent());
                                for (ACLMessage offre : aclMessages) {
                                    double price = Double.parseDouble(offre.getContent());
                                    if (price < mini) {
                                        meilleureOffre = offre;
                                        mini = price;
                                    }
                                }
                                ACLMessage aclMessageAccept = meilleureOffre.createReply();
                                aclMessageAccept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                send(aclMessageAccept);
                            }
                            break;
                        case ACLMessage.AGREE:
                            ACLMessage aclMessage3 = new ACLMessage(ACLMessage.CONFIRM);
                            aclMessage3.addReceiver(new AID("Consumer", AID.ISLOCALNAME));
                            aclMessage3.setContent(aclMessage.getContent());
                            send(aclMessage3);
                            break;
                        case ACLMessage.REFUSE:
                            break;
                    }
                    gui.logMessage(aclMessage);
                    ACLMessage reply = aclMessage.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("trying to buy=>" + aclMessage.getContent());
                    send(reply);
                } else {
                    block();
                }
            }
        });
        addBehaviour(parallelBehaviour);
    }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {
        if (guiEvent.getType() == 1) {
            String bookName = (String) guiEvent.getParameter(0);
            System.out.println("Agent=>" + getAID().getName() + "=>" + bookName);
            ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
            aclMessage.setContent(bookName);
            for (AID vendeur : vendeurs) {
                aclMessage.addReceiver(vendeur);
            }
            send(aclMessage);
        }
    }
}

