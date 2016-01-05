/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.commons.cluster;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

import org.uberfire.commons.message.MessageType;

abstract class BaseLockExecuteNotifyReleaseTemplate<V> {
    // default timeout 30 sec
    public static final int TIMEOUT = Integer.parseInt(System.getProperty("org.uberfire.cluster.timeout", "30000"));

    public V execute( final ClusterService clusterService,
                      final RunnableFuture<V> task ) {
        try {
            clusterService.lock();

            task.run();

            final V result = task.get();

            sendMessage( clusterService );

            return result;
        } catch ( final ExecutionException e ) {
            throwException( e.getCause() );
        } catch ( final Exception e ) {
            throwException( e );
        } finally {
            clusterService.unlock();
        }
        return null;
    }

    private void throwException( final Throwable e ) {
        if ( e instanceof RuntimeException ) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException( e );
    }

    abstract void sendMessage( final ClusterService clusterService );

    public abstract MessageType getMessageType();

    public abstract String getServiceId();

    public abstract Map<String, String> buildContent();
}
