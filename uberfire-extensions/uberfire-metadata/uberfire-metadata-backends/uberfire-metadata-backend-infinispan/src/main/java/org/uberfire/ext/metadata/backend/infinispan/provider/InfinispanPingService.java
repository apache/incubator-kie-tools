/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.infinispan.provider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfinispanPingService {

    private Logger logger = LoggerFactory.getLogger(InfinispanPingService.class);
    protected static final String PING = "org.appformer.ext.metadata.infinispan.ping";

    private final ExecutorService executor;
    private long sleep;
    private boolean alive = false;

    private RemoteCacheManager cacheManager;
    private boolean stop = false;

    public InfinispanPingService(RemoteCacheImpl remoteCache) {

        sleep = this.getTimeoutOrElse(PING, 5);

        this.executor = Executors.newSingleThreadExecutor();
        this.executor.submit(() -> {
            while (!stop) {
                try {
                    this.alive = remoteCache.ping().isSuccess();
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.error("Infinispan server is not started");
                    }
                    if (logger.isTraceEnabled()) {
                        logger.error("Infinispan server is not started", e);
                    }
                    this.alive = false;
                }
                try {
                    Thread.sleep(this.sleep * 1000);
                } catch (InterruptedException e) {
                    // Do Nothings
                }
            }
        });
    }

    protected int getTimeoutOrElse(String timeout, int defaultValue) {
        String t = System.getProperty(timeout);
        return t != null && !t.isEmpty() ? Integer.parseInt(t) : defaultValue;
    }

    public boolean ping() {
        return this.alive;
    }

    public void stop() {
        this.stop = true;
        this.executor.shutdownNow();
    }
}
