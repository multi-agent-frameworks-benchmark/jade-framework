package com.jade.benchmark;

import com.jade.system.ContractorAgent;
import com.jade.system.InitiatorAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class CallForProposalsBenchmarkPath {

    private AgentContainer container;
    private CountDownLatch agentsFinishedLatch;

//    @Param({"1", "2", "5", "10", "50", "100", "1000", "10000}) -- If you have strong PC
    @Param({"1", "2", "4"})
    private int numberOfContractorsAgentsInStarTopology;

    @Setup
    public void setup() {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        container = runtime.createMainContainer(profile);
        agentsFinishedLatch = new CountDownLatch(numberOfContractorsAgentsInStarTopology);
    }

    @TearDown
    public void teardown() {
        try {
            agentsFinishedLatch.await();
            container.kill();
        } catch (StaleProxyException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 1)
    @Fork(value = 4)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void callForProposalBenchmarkPath() {
        try {
            Map<String, ContractorAgent> contractors = createContractors();
            InitiatorAgent initiator = new InitiatorAgent(contractors.keySet());

            List<AgentController> contractorControllers = createContractorControllers(contractors);
            AgentController initiatorAgentController = container.acceptNewAgent("Initiator", initiator);

            startAgentControllers(contractorControllers);
            initiatorAgentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private Map<String, ContractorAgent> createContractors() {
        Map<String, ContractorAgent> contractors = new HashMap<>();
        for (int i = 1; i <= numberOfContractorsAgentsInStarTopology; i++) {
            contractors.put("Contractor" + i, new ContractorAgent(agentsFinishedLatch));
        }
        return contractors;
    }

    private List<AgentController> createContractorControllers(Map<String, ContractorAgent> contractors) {
        List<AgentController> contractorControllers = new ArrayList<>();
        contractors.forEach((contractorName, contractor) -> {
            try {
                contractorControllers.add(container.acceptNewAgent(contractorName, contractor));
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        });
        return contractorControllers;
    }

    private void startAgentControllers(List<AgentController> agentControllers) {
        agentControllers.forEach(agentController -> {
            try {
                agentController.start();
            } catch (StaleProxyException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
