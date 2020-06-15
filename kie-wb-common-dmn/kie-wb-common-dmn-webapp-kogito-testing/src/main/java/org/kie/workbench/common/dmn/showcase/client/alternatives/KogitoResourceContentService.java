/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.showcase.client.alternatives;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.promise.Promises;

@Alternative
public class KogitoResourceContentService extends org.kie.workbench.common.kogito.webapp.base.client.workarounds.KogitoResourceContentService {

    final static String BASE_MODEL = "Base Model.dmn";
    final static String MODEL_WITH_IMPORTS = "Model With Imports.dmn";

    @Inject
    public KogitoResourceContentService(final ResourceContentService resourceContentService,
                                        final Promises promises) {
        super(resourceContentService, promises);
    }

    @Override
    public void getFilteredItems(final String pattern,
                                 final RemoteCallback<List<String>> successCallback,
                                 final ErrorCallback<Object> errorCallback) {

        successCallback.callback(Arrays.asList(BASE_MODEL, MODEL_WITH_IMPORTS));
    }

    @Override
    public void loadFile(final String fileUri,
                         final RemoteCallback<String> callback,
                         final ErrorCallback<Object> errorCallback) {
        if (BASE_MODEL.equals(fileUri)) {
            callback.callback(DMNClientModels.BASE_FILE);
        } else if (MODEL_WITH_IMPORTS.equals(fileUri)) {
            callback.callback(DMNClientModels.MODEL_WITH_IMPORTS);
        } else {
            errorCallback.error("File '" + fileUri + "'not found", new Throwable("File not found: '" + fileUri + "'"));
        }
    }

    @Override
    public Promise<String[]> getFilteredItems(final String pattern, final ResourceListOptions options) {
        return Promise.resolve(new String[]{BASE_MODEL, MODEL_WITH_IMPORTS});
    }

    @Override
    public Promise<String> loadFile(final String fileUri) {
        if (BASE_MODEL.equals(fileUri)) {
            return Promise.resolve(DMNClientModels.BASE_FILE);
        } else if (MODEL_WITH_IMPORTS.equals(fileUri)) {
            return Promise.resolve(DMNClientModels.MODEL_WITH_IMPORTS);
        } else {
            throw new IllegalStateException("The required file ('" + fileUri + "') is not defined.");
        }
    }
}
