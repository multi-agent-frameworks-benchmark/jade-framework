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
public class HelloWorldBenchmarkPath {

    private AgentContainer container;

    @Param({"100", "200", "300", "400", "500", "1000", "1500", "2000", "2500", "3500", "5000", "7000", "8750", "10000"})
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
    @Measurement(iterations = 1)
    @Fork(value = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testHelloWorldAgent() {

        setup();

        for (int i = 0; i < numberOfAgentsCreated; i++) {
            createHelloWorldAgent(i);
        }

        teardown();
    }

    private void createHelloWorldAgent(int agentIndex) {
        try {
            AgentController agentController = container.createNewAgent(
                    "helloAgent" + agentIndex, HelloWorldAgent.class.getName(), null);
            agentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
