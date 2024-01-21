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
        addCyclicBehaviour();
    }

    private void addCyclicBehaviour() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = blockingReceive(mt);

                if (msg != null) {
                    handleProposal(msg, this.getAgent().getName());
                } else {
                    block();
                }
            }
        });
    }

    private void handleProposal(ACLMessage msg, String agentName) {
        System.out.println("Agent " + agentName + " - Received proposal from "
                + msg.getSender().getName());
        agentsFinishedLatch.countDown();
    }
}