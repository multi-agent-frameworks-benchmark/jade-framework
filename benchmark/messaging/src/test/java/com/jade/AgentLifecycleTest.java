package com.jade;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class AgentLifecycleTest {

    private Runtime runtime;
    private ContainerController containerController;

    @Before
    public void setup() {
        runtime = Runtime.instance();
    }

    @After
    public void teardown() {
        runtime.shutDown();
    }

    @Test
    public void testAgentLifecycle() throws StaleProxyException, InterruptedException {
        long expectedAgentLifeCycleMillis = 5000; // 5 seconds

        Profile profile = new ProfileImpl();
        containerController = runtime.createMainContainer(profile);

        AgentController agent1 = containerController.createNewAgent("Agent1", TestAgent.class.getName(), null);
        agent1.start();

        AgentController agent2 = containerController.createNewAgent("Agent2", TestAgent.class.getName(), null);
        agent2.start();

        // Wait for agents to finish
        Thread.sleep(expectedAgentLifeCycleMillis);

        agent1.kill();
        agent2.kill();

        assertTrue(TestAgent.isAgent1Finished());
        assertTrue(TestAgent.isAgent2Finished());

        long agent1LifeCycleMillis = TestAgent.getAgent1LifeCycleMillis();
        long agent2LifeCycleMillis = TestAgent.getAgent2LifeCycleMillis();

        assertTrue(agent1LifeCycleMillis >= expectedAgentLifeCycleMillis);
        assertTrue(agent2LifeCycleMillis >= expectedAgentLifeCycleMillis);
    }

    public static class TestAgent extends Agent {
        private static boolean agent1Finished = false;
        private static boolean agent2Finished = false;
        private static long agent1LifeCycleMillis;
        private static long agent2LifeCycleMillis;

        public static boolean isAgent1Finished() {
            return agent1Finished;
        }

        public static boolean isAgent2Finished() {
            return agent2Finished;
        }

        public static long getAgent1LifeCycleMillis() {
            return agent1LifeCycleMillis;
        }

        public static long getAgent2LifeCycleMillis() {
            return agent2LifeCycleMillis;
        }

        protected void setup() {
            addBehaviour(new OneShotBehaviour(this) {
                public void action() {
                    // Agent behavior
                    long startTime = System.currentTimeMillis();
                    // ... agent behavior ...
                    long endTime = System.currentTimeMillis();
                    agent1LifeCycleMillis = endTime - startTime;
                    agent1Finished = true;
                }
            });

            addBehaviour(new CyclicBehaviour(this) {
                public void action() {
                    // Agent behavior
                    long startTime = System.currentTimeMillis();
                    // ... agent behavior ...
                    long endTime = System.currentTimeMillis();
                    agent2LifeCycleMillis = endTime - startTime;
                    agent2Finished = true;
                }
            });
        }
    }
}
