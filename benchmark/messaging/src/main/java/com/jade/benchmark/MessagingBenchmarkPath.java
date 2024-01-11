package com.jade.benchmark;

import com.jade.system.CounterAgent;
import com.jade.system.SenderAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@State(Scope.Thread)
public class MessagingBenchmarkPath {

    private AgentContainer container;
    private CountDownLatch agentsFinishedLatch;

//    @Param({"1000", "10000", "100000", "1000000"}) -- If you have strong CPU
    @Param({"1000", "2000"})
    private int numberOfMessageTransfersInBothWays;

    @Setup
    public void setup() {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        container = runtime.createMainContainer(profile);
        agentsFinishedLatch = new CountDownLatch(2);
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
    @Fork(value = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void testHelloWorldAgent() {
        try {
            CounterAgent counterAgent =
                    new CounterAgent(agentsFinishedLatch, numberOfMessageTransfersInBothWays);
            SenderAgent senderAgent =
                    new SenderAgent(agentsFinishedLatch, numberOfMessageTransfersInBothWays);

            AgentController counterAgentController = container.acceptNewAgent(
                    "CounterAgent", counterAgent);
            AgentController senderAgentController = container.acceptNewAgent(
                    "SenderAgent", senderAgent);

            counterAgentController.start();
            senderAgentController.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}