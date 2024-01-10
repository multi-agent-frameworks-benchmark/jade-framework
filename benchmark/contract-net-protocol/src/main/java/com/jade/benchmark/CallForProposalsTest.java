package com.jade.benchmark;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class CallForProposalsTest {

    private AgentContainer container;
    private CountDownLatch agentsFinishedLatch;

//    @Param({"1", "2", "3", "4"})
    @Param({"2", "2", "3", "4"})
    private int numberOfContractorsAgentsInStarTopology;

    public void setup() {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        container = runtime.createMainContainer(profile);
        agentsFinishedLatch = new CountDownLatch(numberOfContractorsAgentsInStarTopology);
    }

    public void teardown() {
        try {
            agentsFinishedLatch.await();
            Thread.sleep(1000);
            container.kill();
        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 1)
    @Fork(value = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testCallForProposal() {
        // Before
        setup();

        try {
            Map<String, ContractorAgent> contractors = new HashMap<>();
            for (int i = 1; i <= numberOfContractorsAgentsInStarTopology; i++) {
                contractors.put("Contractor" + i, new ContractorAgent(agentsFinishedLatch));
            }
            InitiatorAgent initiator = new InitiatorAgent(contractors.keySet());


            List<AgentController> contractorControllers = new ArrayList<>();
            contractors.forEach((contractorName, contractor) -> {
                try {
                    contractorControllers.add(container.acceptNewAgent(contractorName, contractor));
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }
            });
            AgentController initiatorAgentController = container.acceptNewAgent("Initiator", initiator);


            contractorControllers.forEach(contractorController -> {
                try {
                    contractorController.start();
                } catch (StaleProxyException e) {
                    throw new RuntimeException(e);
                }
            });
            initiatorAgentController.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        // After
        teardown();
    }
}

