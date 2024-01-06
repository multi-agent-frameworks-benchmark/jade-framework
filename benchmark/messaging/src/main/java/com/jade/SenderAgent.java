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

    @Override
    protected void takeDown() {

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

                if (value >= 100) {
                    System.out.println("Agent " + myAgent.getLocalName() + " reached 100. Notifying CounterAgent to terminate...");

                    // Wysyłamy komunikat informujący o zakończeniu
                    ACLMessage terminateMsg = new ACLMessage(ACLMessage.INFORM);
                    terminateMsg.addReceiver(getAID("CounterAgent"));
                    terminateMsg.setContent("terminate");
                    send(terminateMsg);

                    // Teraz możemy zakończyć agenta
                    myAgent.doDelete();
                }
            } else {
                block();
            }
        }
    }
}
