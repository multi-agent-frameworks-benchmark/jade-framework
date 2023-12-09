package com.jade;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class CounterAgent extends Agent {
    private int value = 0;

    @Override
    protected void setup() {
        addBehaviour(new CounterBehaviour());
    }

    private class CounterBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                int receivedValue = Integer.parseInt(msg.getContent());

                receivedValue++;

                System.out.println("Agent " + myAgent.getLocalName() + " received: " + receivedValue);

                if (receivedValue <= 100) {
                    ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                    reply.addReceiver(msg.getSender());
                    reply.setContent(Integer.toString(receivedValue));
                    send(reply);
                } else {
                    System.out.println("Agent " + myAgent.getLocalName() + " reached 100. Terminating...");
                    myAgent.doDelete();
                }
            } else {
                block();
            }
        }
    }
}
