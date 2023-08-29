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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.util.function.Supplier;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class PromiseMock<T> {

    private final Promise<T> promise;

    public static <T> Promise<T> success(final T instance) {
        return new PromiseMock<T>()
                .then(() -> instance)
                .asPromise();
    }

    public static <T> Promise<T> error(final Throwable t) {
        return new PromiseMock<T>()
                .error(() -> t)
                .asPromise();
    }

    @SuppressWarnings("unchecked")
    public PromiseMock() {
        this.promise = mock(Promise.class);
    }

    @SuppressWarnings("unchecked")
    public PromiseMock<T> then(final Supplier<T> instance) {
        doAnswer(invokation -> {
            ((IThenable.ThenOnFulfilledCallbackFn) invokation.getArguments()[0]).onInvoke(instance.get());
            return promise;
        })
                .when(promise)
                .then(any(IThenable.ThenOnFulfilledCallbackFn.class));
        return this;
    }

    public PromiseMock<T> error(final Supplier<Throwable> t) {
        doAnswer(invokation -> {
            ((Promise.CatchOnRejectedCallbackFn) invokation.getArguments()[0]).onInvoke(t.get());
            return promise;
        })
                .when(promise)
                .catch_(any(Promise.CatchOnRejectedCallbackFn.class));
        return this;
    }

    public Promise<T> asPromise() {
        return promise;
    }
}
