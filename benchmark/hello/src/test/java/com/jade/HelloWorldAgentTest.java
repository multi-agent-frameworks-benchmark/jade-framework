package com.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

@State(Scope.Thread)
public class HelloWorldAgentTest {

    private AgentContainer container;

    @Param({"1", "2", "5", "10", "50", "100"})
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
    @Test
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
            fail("Test failed: " + e.getMessage());
        }
    }
}
