package com.jade.benchmark;

import com.jade.system.HelloWorldAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class HelloWorldAgentTest {

    private AgentContainer container;

//    @Param({"1", "2", "5", "10", "50", "100", "1000", "10000}) -- If you have strong PC
    @Param({"1", "3"})
    private int numberOfAgentsCreated;

    public void setup() {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        container = runtime.createMainContainer(profile);
    }

    public void teardown() {
        try {
            container.kill();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 2)
    @Fork(value = 4)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testHelloWorldAgent() {
        // Before
        setup();

        for (int i = 0; i < numberOfAgentsCreated; i++) {
            createHelloWorldAgent(i);
        }

        // After
        teardown();
    }

    private void createHelloWorldAgent(int agentIndex) {
        try {
            AgentController agentController = container.createNewAgent("helloAgent" + agentIndex, HelloWorldAgent.class.getName(), null);
            agentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
