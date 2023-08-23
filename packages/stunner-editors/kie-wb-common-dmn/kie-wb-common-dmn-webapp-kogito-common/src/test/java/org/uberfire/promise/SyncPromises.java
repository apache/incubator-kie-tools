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

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.uberfire.client.promise.Promises;

public class SyncPromises extends Promises {

    @Override
    public <T> Promise<T> create(final Promise.PromiseExecutorCallbackFn<T> executor) {
        return new SyncPromise<>(executor);
    }

    public static class SyncPromise<T> extends Promise<T> {

        public Status status;
        public T value;

        private SyncPromise(final PromiseExecutorCallbackFn<T> executor) {
            super(executor);
            status = Status.PENDING;
            executor.onInvoke(new Resolver(), new Rejecter());
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> Promise<V> then(final ThenOnFulfilledCallbackFn<? super T, ? extends V> onFulfilled) {
            try {
                if (status == Status.RESOLVED) {
                    return (SyncPromise<V>) onFulfilled.onInvoke(value);
                } else {
                    return new SyncPromise<>((res, rej) -> rej.onInvoke(value));
                }
            } catch (final Exception e) {
                return new SyncPromise<>((res, rej) -> rej.onInvoke(e));
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public <V> Promise<V> catch_(final CatchOnRejectedCallbackFn<? extends V> onRejected) {
            try {
                if (status == Status.REJECTED) {
                    return (SyncPromise<V>) onRejected.onInvoke(this.value);
                } else {
                    return new SyncPromise<>((res, rej) -> res.onInvoke((V) value));
                }
            } catch (final Exception e) {
                return new SyncPromise<>((res, rej) -> rej.onInvoke(e));
            }
        }

        private class Resolver implements PromiseExecutorCallbackFn.ResolveCallbackFn<T> {

            @Override
            public void onInvoke(final T value) {
                resolve(value);
            }

            @Override
            public void onInvoke(final IThenable<T> thenable) {
                if (thenable == null) {
                    value = null;
                } else {
                    thenable.then(v -> {
                        value = v;
                        return SyncPromise.resolve(v);
                    });
                }
                status = Status.RESOLVED;
            }

            @Override
            public void onInvoke(final ResolveUnionType<T> value) {
                throw new RuntimeException("Not supported");
            }

            private void resolve(final T v) {
                value = v;
                status = Status.RESOLVED;
            }
        }

        private class Rejecter implements PromiseExecutorCallbackFn.RejectCallbackFn {

            @Override
            @SuppressWarnings("unchecked")
            public void onInvoke(final Object error) {
                value = (T) error;
                status = Status.REJECTED;
            }
        }
    }

    public enum Status {
        PENDING,
        RESOLVED,
        REJECTED;
    }
}