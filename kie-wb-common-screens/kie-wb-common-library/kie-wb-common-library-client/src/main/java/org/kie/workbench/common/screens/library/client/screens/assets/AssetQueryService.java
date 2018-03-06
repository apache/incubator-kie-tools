/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.library.client.screens.assets;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.library.api.AssetQueryResult;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectAssetsQuery;

@Dependent
public class AssetQueryService {

    private final Caller<LibraryService> libraryServiceCaller;

    private Object tokenForGetProjectAssets = null;
    private Object tokenForGetNumberOfAssets = null;

    @Inject
    public AssetQueryService(final Caller<LibraryService> libraryServiceCaller) {
        this.libraryServiceCaller = libraryServiceCaller;
    }

    public Invoker<AssetQueryResult> getAssets(ProjectAssetsQuery query) {
        return new Invoker<>(libraryService -> libraryService.getProjectAssets(query),
                             () -> tokenForGetProjectAssets,
                             newToken -> tokenForGetProjectAssets = newToken);
    }

    public Invoker<Integer> getNumberOfAssets(ProjectAssetsQuery query) {
        return new Invoker<>(libraryService -> libraryService.getNumberOfAssets(query),
                             () -> tokenForGetNumberOfAssets,
                             newToken -> tokenForGetNumberOfAssets = newToken);
    }

    public class Invoker<T> {

        private final Function<LibraryService, T> methodCall;
        private final Supplier<Object> activeTokenGetter;
        private final Consumer<Object> activeTokenSetter;

        private Invoker(Function<LibraryService, T> methodCall, Supplier<Object> activeTokenGetter, Consumer<Object> activeTokenSetter) {
            this.methodCall = methodCall;
            this.activeTokenGetter = activeTokenGetter;
            this.activeTokenSetter = activeTokenSetter;
        }

        public void call(RemoteCallback<T> callback, ErrorCallback<Message> errorCallback) {
            Object token = new Object();
            activeTokenSetter.accept(token);
            methodCall.apply(libraryServiceCaller.call(wrap(token, callback), wrap(token, errorCallback)));
        }

        private ErrorCallback<Message> wrap(Object token, ErrorCallback<Message> errorCallback) {
            return (msg, error) -> {
                return validToken(token) && errorCallback != null && errorCallback.error(msg, error);
            };
        }

        private RemoteCallback<T> wrap(Object token, RemoteCallback<T> callback) {
            return t -> {
                if (validToken(token)) {
                    callback.callback(t);
                }
            };
        }

        private boolean validToken(Object token) {
            return token == activeTokenGetter.get();
        }
    }

}
