package com.jade.system;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.concurrent.CountDownLatch;

public class ContractorAgent extends Agent {

    private final CountDownLatch agentsFinishedLatch;

    public ContractorAgent(CountDownLatch agentsFinishedLatch) {
        this.agentsFinishedLatch = agentsFinishedLatch;
    }

    @Override
    protected void setup() {
        System.out.println("Contractor Agent " + getAID().getName() + " is ready.");

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = receive(mt);

                if (msg != null) {
                    System.out.println("Agent " + this.getAgent().getName() +
                            " - Received proposal from " + msg.getSender().getName());
                    agentsFinishedLatch.countDown();
                } else {
                    block();
                }
            }
        });
    }
}