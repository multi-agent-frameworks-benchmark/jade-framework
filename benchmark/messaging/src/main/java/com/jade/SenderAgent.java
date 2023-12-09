package com.jade;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class SenderAgent extends Agent {
    private int value = 0;

    @Override
    protected void setup() {
        addBehaviour(new SenderBehaviour());
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
            } else {
                block();
            }
        }
    }
}
