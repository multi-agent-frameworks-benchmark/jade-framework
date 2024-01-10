package com.jade.system;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.CountDownLatch;

public class SenderAgent extends Agent {

    private final CountDownLatch agentsFinishedLatch;

    private final int receiveValueLimit;

    private int value = 0;

    public SenderAgent(CountDownLatch agentsFinishedLatch, int receiveValueLimit) {
        this.agentsFinishedLatch = agentsFinishedLatch;
        this.receiveValueLimit = receiveValueLimit;
    }

    @Override
    protected void setup() {
        addBehaviour(new SenderBehaviour());
    }

    @Override
    protected void takeDown() {
        this.doDelete();
        agentsFinishedLatch.countDown();
    }

    private class SenderBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(getAID("CounterAgent"));
            msg.setContent(Integer.toString(value));
            send(msg);

            ACLMessage reply = receive();
            if (reply != null) {
                value = Integer.parseInt(reply.getContent());

                System.out.println("Agent " + myAgent.getLocalName() + " received: " + value);

                if (value >= receiveValueLimit) {
                    System.out.println("Agent " + myAgent.getLocalName() + " reached " + receiveValueLimit + ". Notifying CounterAgent to terminate...");

                    ACLMessage terminateMsg = new ACLMessage(ACLMessage.INFORM);
                    terminateMsg.addReceiver(getAID("CounterAgent"));
                    terminateMsg.setContent("terminate");
                    send(terminateMsg);

                    myAgent.doDelete();
                    agentsFinishedLatch.countDown();
                }
            } else {
                block();
            }
        }
    }
}