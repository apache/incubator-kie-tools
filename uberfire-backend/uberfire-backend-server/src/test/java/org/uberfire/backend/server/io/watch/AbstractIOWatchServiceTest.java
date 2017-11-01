/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.io.watch;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Filter;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.ClosedWatchServiceException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InterruptedException;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.Watchable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AbstractIOWatchServiceTest {

    @Test
    public void testAddWatchServiceException() {
        // BZ1323572
        try {
            System.setProperty("org.uberfire.watcher.autostart",
                               "false");

            AbstractIOWatchService service = new AbstractIOWatchService(null,
                                                                        null,
                                                                        null,
                                                                        null,
                                                                        null,
                                                                        Executors.newCachedThreadPool(new DescriptiveThreadFactory())) {

                @Override
                public boolean doFilter(WatchEvent<?> t) {
                    return false;
                }
            };

            WatchService ws = new WatchService() {

                @Override
                public void close() throws IOException {
                }

                @Override
                public WatchKey poll() throws ClosedWatchServiceException {
                    return null;
                }

                @Override
                public WatchKey poll(long timeout,
                                     TimeUnit unit) throws ClosedWatchServiceException, InterruptedException {
                    return null;
                }

                @Override
                public WatchKey take() throws ClosedWatchServiceException, InterruptedException {
                    return new WatchKey() {

                        @Override
                        public boolean isValid() {
                            return false;
                        }

                        @Override
                        public List<WatchEvent<?>> pollEvents() {
                            return null;
                        }

                        @Override
                        public boolean reset() {
                            return false; // exit the loop in asyncWatchService.execute()
                        }

                        @Override
                        public void cancel() {
                        }

                        @Override
                        public Watchable watchable() {
                            return null;
                        }
                    };
                }

                @Override
                public boolean isClose() {
                    return false;
                }
            };

            service.addWatchService(mock(FileSystem.class),
                                    ws);

            Set<AsyncWatchService> watchThreads = null;
            try {
                Field field = AbstractIOWatchService.class.getDeclaredField("watchThreads");
                field.setAccessible(true);
                watchThreads = (Set<AsyncWatchService>) field.get(service);
            } catch (Exception e) {
                fail(e.getMessage());
            }
            AsyncWatchService asyncWatchService = watchThreads.iterator().next();

            IOWatchServiceExecutor wsExecutor = (watchKey, filter) -> {
                throw new RuntimeException("dummy");
            };

            try {
                asyncWatchService.execute(wsExecutor);
                assertTrue(true);
            } catch (Exception e) {
                fail("Exception is thrown from asyncWatchService.execute()");
            }
        } finally {
            System.clearProperty("org.uberfire.watcher.autostart");
        }
    }
}
