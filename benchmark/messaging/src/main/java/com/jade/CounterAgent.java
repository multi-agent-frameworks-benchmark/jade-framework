package com.jade;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class CounterAgent extends Agent {
    private int value = 0;

    @Override
    protected void setup() {
        doDelete();
        addBehaviour(new CounterBehaviour());
    }

    private class CounterBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if ("terminate".equals(msg.getContent())) {
                    // Obsługa komunikatu informującego o zakończeniu
                    System.out.println("Agent " + myAgent.getLocalName() + " received termination signal. Terminating...");
                    myAgent.doDelete();
                } else {
                    int receivedValue = Integer.parseInt(msg.getContent());
                    receivedValue++;

                    System.out.println("Agent " + myAgent.getLocalName() + " received: " + receivedValue);

                    if (receivedValue <= 100) {
                        ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                        reply.addReceiver(msg.getSender());
                        reply.setContent(Integer.toString(receivedValue));
                        send(reply);
                    } else {
                        System.out.println("Agent " + myAgent.getLocalName() + " reached 100. Notifying SenderAgent to terminate...");

                        // Wysyłamy komunikat informujący o zakończeniu
                        ACLMessage terminateMsg = new ACLMessage(ACLMessage.INFORM);
                        terminateMsg.addReceiver(msg.getSender());
                        terminateMsg.setContent("terminate");
                        send(terminateMsg);

                        // Teraz możemy zakończyć agenta
                        myAgent.doDelete();
                    }
                }
            } else {
                block();
            }
        }
    }
}
