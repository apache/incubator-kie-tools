package org.uberfire.commons.concurrent;

import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExecutorServiceProducerTest {

    @Before
    public void setUp() {
        System.clearProperty(ExecutorServiceProducer.MANAGED_LIMIT_PROPERTY);
    }

    @Test
    public void testSystemPropertySet() {
        System.setProperty(ExecutorServiceProducer.MANAGED_LIMIT_PROPERTY,
                           "1000");
        ExecutorServiceProducer producer = new ExecutorServiceProducer();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) producer.buildFixedThreadPoolExecutorService(ExecutorServiceProducer.MANAGED_LIMIT_PROPERTY);
        assertEquals(1000,
                     executor.getMaximumPoolSize());
    }

    @Test
    public void testSystemPropertyNotSet() {
        System.clearProperty(ExecutorServiceProducer.MANAGED_LIMIT_PROPERTY);
        ExecutorServiceProducer producer = new ExecutorServiceProducer();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) producer.buildFixedThreadPoolExecutorService(ExecutorServiceProducer.MANAGED_LIMIT_PROPERTY);
        assertEquals(2147483647,
                     executor.getMaximumPoolSize());
    }
}