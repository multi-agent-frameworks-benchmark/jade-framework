package com.jade.system;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.CountDownLatch;

public class CounterAgent extends Agent {

    private final CountDownLatch agentsFinishedLatch;
    private final int receiveValueLimit;

    public CounterAgent(CountDownLatch agentsFinishedLatch, int receivedValueLimit) {
        this.agentsFinishedLatch = agentsFinishedLatch;
        this.receiveValueLimit = receivedValueLimit;
    }

    @Override
    protected void setup() {
        addBehaviour(new CounterBehaviour());
    }

    private class CounterBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                processMessage(msg);
            } else {
                block();
            }
        }

        private void processMessage(ACLMessage msg) {
            if ("terminate".equals(msg.getContent())) {
                handleTermination();
            } else {
                handleIncrementValue(msg);
            }
        }

        private void handleTermination() {
            System.out.println("Agent " + myAgent.getLocalName() + " received termination signal. Terminating...");
            myAgent.doDelete();
            agentsFinishedLatch.countDown();
        }

        private void handleIncrementValue(ACLMessage msg) {
            int receivedValue = Integer.parseInt(msg.getContent());
            receivedValue++;

            System.out.println("Agent " + myAgent.getLocalName() + " received: " + receivedValue);

            if (receivedValue <= receiveValueLimit) {
                sendIncrementedValue(msg, receivedValue);
            } else {
                handleValueLimitReached(msg);
            }
        }

        private void sendIncrementedValue(ACLMessage msg, int receivedValue) {
            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
            reply.addReceiver(msg.getSender());
            reply.setContent(Integer.toString(receivedValue));
            send(reply);
        }

        private void handleValueLimitReached(ACLMessage msg) {
            System.out.println("Agent " + myAgent.getLocalName() + " reached " + receiveValueLimit + ". Notifying SenderAgent to terminate...");

            ACLMessage terminateMsg = new ACLMessage(ACLMessage.INFORM);
            terminateMsg.addReceiver(msg.getSender());
            terminateMsg.setContent("terminate");
            send(terminateMsg);

            myAgent.doDelete();
            agentsFinishedLatch.countDown();
        }
    }
}
