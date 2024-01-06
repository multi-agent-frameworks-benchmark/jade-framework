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

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

@State(Scope.Thread)
public class HelloWorldAgenttTest {

    private AgentContainer container;

    @Param({"1000", "10000", "100000", "1000000"})
    private int numberOfMessageTransfersInBothWays;

    public void setup() {
        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        container = runtime.createMainContainer(profile);
    }

    public void teardown() {
        try {
            Thread.sleep(2000);
            container.kill();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @Measurement(iterations = 1)
    @Fork(value = 1)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Test
    public void testHelloWorldAgent() {
        // Before
        setup();

        try {
            AgentController counterAgentController = container.createNewAgent("CounterAgent", CounterAgent.class.getName(), null);
            AgentController senderAgentController = container.createNewAgent("SenderAgent", SenderAgent.class.getName(), null);
            counterAgentController.start();
            senderAgentController.start();


//            Scanner scanner = new Scanner(System.in);
//
//            while (true) {
//                String input = scanner.nextLine();
//
//                if (input.contains("Agent SenderAgent reached 100")) {
                    // After
                    teardown();
//                    break;
//                }
//            }
//
//            scanner.close();


        } catch (StaleProxyException e) {
            e.printStackTrace();
            fail("Test failed: " + e.getMessage());
        }

        // After
//        teardown();
    }
}
