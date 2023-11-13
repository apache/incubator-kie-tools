/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.uberfire.promise;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import elemental2.promise.Promise;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.uberfire.promise.SyncPromises.Status.REJECTED;
import static org.uberfire.promise.SyncPromises.Status.RESOLVED;

public class SyncPromisesTest {

    private final SyncPromises promises = new SyncPromises();

    @Test
    public void testBasicChaining() {
        final Promise<Integer> p = promises.resolve("a").then(a -> {
            assertTrue("a".equals(a));
            return promises.resolve("b");
        }).then(b -> {
            assertTrue("b".equals(b));
            return promises.resolve(2);
        }).catch_(err -> {
            fail("Catch should've not been called");
            return promises.resolve(5);
        }).then(two -> {
            assertTrue(two == 2);
            return promises.resolve(3);
        });

        final SyncPromises.SyncPromise<Integer> sp = (SyncPromises.SyncPromise<Integer>) p;
        assertTrue(sp.value == 3);
        assertTrue(sp.status == RESOLVED);
    }

    @Test
    public void testErrorHandling() {
        final Promise<Long> p = promises.resolve("a").then(a -> {
            assertTrue("a".equals(a));
            return promises.reject("b");
        }).then(b -> {
            fail("This 'then' should've been jumped over");
            return promises.resolve(2);
        }).catch_(err -> {
            assertTrue("b" == err);
            return promises.resolve(5);
        }).then(five -> {
            assertTrue(five == 5);
            return promises.reject(8L);
        });

        final SyncPromises.SyncPromise<Long> sp = (SyncPromises.SyncPromise<Long>) p;
        assertTrue(sp.value == 8L);
        assertTrue(sp.status == REJECTED);
    }

    @Test
    public void testErrorHandlingDoubleRejection() {
        final Promise<Integer> p = promises.resolve("a").then(a -> {
            assertTrue("a".equals(a));
            return promises.reject("b");
        }).catch_(err -> {
            assertTrue("b" == err);
            return promises.reject('4');
        }).catch_(four -> {
            assertTrue(four.equals('4'));
            return promises.resolve(12);
        });

        final SyncPromises.SyncPromise<Integer> sp = (SyncPromises.SyncPromise<Integer>) p;
        assertTrue(sp.value == 12);
        assertTrue(sp.status == RESOLVED);
    }

    @Test
    public void testErrorHandlingWhenExceptionOccurs() {
        final RuntimeException te = new RuntimeException("Test exception");

        final Promise<Integer> p = promises.resolve("a").then(a -> {
            throw te;
        }).then(i -> {
            fail("This 'then' should've been jumped over");
            return promises.resolve();
        }).catch_(err -> {
            assertEquals(err, te);
            return promises.resolve(17);
        });

        final SyncPromises.SyncPromise<Integer> sp = (SyncPromises.SyncPromise<Integer>) p;
        assertTrue(sp.value == 17);
        assertTrue(sp.status == RESOLVED);
    }

    @Test
    public void testAllWithOneRejection() {
        final Promise<Integer> resolved1 = promises.resolve(1);
        final Promise<Integer> resolved2 = promises.resolve(2);
        final Promise<Integer> rejected = promises.reject(0);

        Arrays.asList(
                this.promises.all(rejected, resolved1, resolved2),
                this.promises.all(resolved2, rejected, resolved1),
                this.promises.all(resolved1, resolved2, rejected)).forEach(p -> {
            p.then(i -> {
                fail("Promise should've not been resolved!");
                return this.promises.resolve();
            }).catch_(zero -> {
                assertEquals(0, zero);
                return this.promises.resolve();
            });
        });
    }

    @Test
    public void testAllWithNoRejections() {
        final Promise<Integer> resolved1 = promises.resolve(1);
        final Promise<Integer> resolved2 = promises.resolve(2);
        final Promise<Integer> resolved3 = promises.resolve(3);

        promises.all(resolved1, resolved2, resolved3).then(i -> {
            assertEquals((Integer) 3, i);
            return promises.resolve();
        }).catch_(e -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void testAllMappingWithNoRejections() {
        promises.all(Arrays.asList(1, 2, 3, 4), promises::resolve).then(i -> {
            assertEquals((Integer) 4, i);
            return promises.resolve();
        }).catch_(e -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void testAllMappingWithOneRejection() {
        promises.all(Arrays.asList(1, 2, 3, 4), i -> i == 3 ? promises.reject(i) : promises.resolve(i)).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        }).catch_(e -> {
            assertEquals(3, e);
            return promises.resolve();
        });
    }

    @Test
    public void testReduceLazilyWithNoRejections() {

        final AtomicInteger sum = new AtomicInteger(0);

        promises.reduceLazily(Arrays.asList(1, 2, 4, 8), i -> promises.resolve().then(e -> {
            assertTrue(sum.get() < i);
            sum.addAndGet(i);
            return promises.resolve(i);
        })).then(i -> {
            assertEquals((Integer) 8, i);
            return promises.resolve();
        }).catch_(e -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        }).then(ignore -> {
            assertEquals((Integer) 15, (Integer) sum.get());
            return promises.resolve();
        });
    }

    @Test
    public void testReduceLazilyWithOneRejection() {

        final AtomicInteger sum = new AtomicInteger(0);

        promises.reduceLazily(Arrays.asList(1, 2, 4, 8), i -> i == 4 ? promises.reject(4) : promises.resolve().then(e -> {
            assertTrue(sum.get() < i);
            sum.addAndGet(i);
            return promises.resolve(i);
        })).then(i -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        }).catch_(e -> {
            assertEquals((Integer) 4, e);
            return promises.resolve();
        }).then(ignore -> {
            assertEquals((Integer) 3, (Integer) sum.get());
            return promises.resolve();
        });
    }

    @Test
    public void testReduceLazilyChainingWithNoInterruptions() {

        final AtomicInteger sum = new AtomicInteger(0);

        promises.reduceLazilyChaining(Arrays.asList(1, 2, 4, 8, 16), (chain, i) -> {
            assertTrue(sum.get() < i);
            sum.addAndGet(i);
            return promises.resolve(i);
        }).catch_(e -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        }).then(chainResult -> {
            assertEquals((Integer) 16, chainResult);
            assertEquals((Integer) 31, (Integer) sum.get());
            return promises.resolve();
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReduceLazilyChainingWithInterruption() {

        final AtomicInteger sum = new AtomicInteger(0);

        promises.reduceLazilyChaining(Arrays.asList(1, 2, 4, 8, 16), (chain, i) -> {
            if (i == 4) {
                // Skips the '4' step
                return promises.reject(chain);
            } else {
                assertTrue(sum.get() < i);
                sum.addAndGet(i);
                return promises.resolve(i);
            }
        }).then(invalid -> {
            fail("Promise should've not been resolved!");
            return promises.resolve();
        }).catch_(chain -> {
            assertEquals((Integer) 3, (Integer) sum.get());
            return ((Supplier<Promise<Integer>>) chain).get();
        }).then(chainResult -> {
            assertNull(chainResult);
            assertEquals((Integer) 27, (Integer) sum.get());
            return promises.resolve();
        }).catch_(e -> {
            fail("Promise should've been resolved!");
            return promises.resolve();
        });
    }

    @Test
    public void reduceWithOrIsTrueOperatorTest() {
        final List<Promise<Boolean>> promisesToReduce = Arrays.asList(promises.resolve(true),
                                                                      promises.resolve(false));
        promises.reduce(promises.resolve(false), promisesToReduce, (p1, p2) -> p1.then(resultP1 -> p2.then(resultP2 -> this.promises.resolve(resultP1 || resultP2)))).then(resultIsTrue -> {
            assertTrue(resultIsTrue);
            return promises.resolve();
        });
    }

    @Test
    public void reduceWithOrIsFalseOperatorTest() {
        final List<Promise<Boolean>> promisesToReduce = Arrays.asList(promises.resolve(false),
                                                                      promises.resolve(false));
        promises.reduce(promises.resolve(false), promisesToReduce, (p1, p2) -> p1.then(resultP1 -> p2.then(resultP2 -> this.promises.resolve(resultP1 || resultP2)))).then(resultIsTrue -> {
            assertFalse(resultIsTrue);
            return promises.resolve();
        });
    }

    @Test
    public void reduceWithAndIsTrueOperatorTest() {
        final List<Promise<Boolean>> promisesToReduce = Arrays.asList(promises.resolve(true),
                                                                      promises.resolve(true));
        promises.reduce(promises.resolve(true), promisesToReduce, (p1, p2) -> p1.then(resultP1 -> p2.then(resultP2 -> this.promises.resolve(resultP1 && resultP2)))).then(resultIsTrue -> {
            assertTrue(resultIsTrue);
            return promises.resolve();
        });
    }

    @Test
    public void reduceWithAndIsFalseOperatorTest() {
        final List<Promise<Boolean>> promisesToReduce = Arrays.asList(promises.resolve(false),
                                                                      promises.resolve(true));
        promises.reduce(promises.resolve(true), promisesToReduce, (p1, p2) -> p1.then(resultP1 -> p2.then(resultP2 -> this.promises.resolve(resultP1 && resultP2)))).then(resultIsTrue -> {
            assertFalse(resultIsTrue);
            return promises.resolve();
        });
    }
}