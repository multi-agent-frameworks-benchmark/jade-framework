package com.jade.system;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Set;

public class InitiatorAgent extends Agent {

    private static final String TASK_DETAILS = "Details: Design and implement a new e-commerce " +
                                               "website with payment gateway integration.";

    private final Set<String> contractorsNames;

    public InitiatorAgent(Set<String> contractorsNames) {
        this.contractorsNames = contractorsNames;
    }

    @Override
    protected void setup() {
        System.out.println("Initiator Agent " + getAID().getName() + " is ready.");
        sendCallForProposals();
        addProposalHandlingBehaviour();
    }

    private void sendCallForProposals() {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        contractorsNames.forEach(contractorsName -> cfp.addReceiver(
                new AID(contractorsName, AID.ISLOCALNAME)));
        cfp.setContent(TASK_DETAILS);
        send(cfp);
    }

    private void addProposalHandlingBehaviour() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage msg = blockingReceive(mt);

                if (msg != null) {
                    System.out.println("Received proposal from " + msg.getSender().getName());
                } else {
                    block();
                }
            }
        });
    }
}
