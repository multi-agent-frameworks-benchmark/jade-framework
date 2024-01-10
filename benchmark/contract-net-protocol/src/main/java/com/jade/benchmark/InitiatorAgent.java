package com.jade.benchmark;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class InitiatorAgent extends Agent {

    private static final String TASK_DETAILS = "Details: Design and implement a new e-commerce website with payment gateway integration.";

    private final Set<String> contractorsNames;


    public InitiatorAgent(Set<String> contractorsNames) {
        this.contractorsNames = contractorsNames;
    }

    @Override
    protected void setup() {
        System.out.println("Initiator Agent " + getAID().getName() + " is ready.");

        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

        contractorsNames.forEach(contractorsName -> cfp.addReceiver(new AID(contractorsName, AID.ISLOCALNAME)));
        cfp.setContent(TASK_DETAILS);
        send(cfp);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    System.out.println("Received proposal from " + msg.getSender().getName());
                } else {
                    block();
                }
            }
        });

    }
}