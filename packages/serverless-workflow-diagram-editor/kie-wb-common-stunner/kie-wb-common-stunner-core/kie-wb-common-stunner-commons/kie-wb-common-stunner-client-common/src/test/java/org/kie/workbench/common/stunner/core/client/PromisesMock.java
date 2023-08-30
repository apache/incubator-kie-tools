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


package org.kie.workbench.common.stunner.core.client;

import java.util.function.Function;

import elemental2.promise.Promise;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.promise.Promises;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

public class PromisesMock {

    public static Promises build() {
        return promisify(spy(new Promises()));
    }

    @SuppressWarnings("unchecked")
    public static Promises promisify(final Promises promises) {
        doAnswer(invocationOnMock -> {
            final Caller caller = (Caller) invocationOnMock.getArguments()[0];
            final Function call = (Function) invocationOnMock.getArguments()[1];
            final Promise<Object>[] promise = new Promise[1];
            final Object service = caller.call(response -> promise[0] = PromiseMock.success(response));
            call.apply(service);
            return promise[0];
        })
                .when(promises)
                .promisify(any(Caller.class),
                           any(Function.class));
        return promises;
    }
}
