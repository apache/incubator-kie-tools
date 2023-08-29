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

package org.kie.workbench.common.kogito.webapp.base.client.workarounds;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.IThenable;
import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.ResourceContentService;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.promise.Promises;

/**
 * Class used to provide <i>resources</i> access to <i>kogito editors</i>.
 * It is a simple wrapper around {@link ResourceContentService}
 */
@ApplicationScoped
public class KogitoResourceContentService {

    private ResourceContentService resourceContentService;
    private Promises promises;

    private KogitoResourceContentService() {
        //CDI proxy
    }

    @Inject
    public KogitoResourceContentService(final ResourceContentService resourceContentService,
                                        final Promises promises) {
        this.resourceContentService = resourceContentService;
        this.promises = promises;
    }

    /**
     * Load the file at given <b>uri</code> and returns its content inside the given callback
     *
     * @param fileUri       the resource <b>uri</code> relative to the workspace/project
     * @param callback      The <code>RemoteCallback</code> to be invoked on success
     * @param errorCallback The <code>ErrorCallback</code> to be invoked on failure
     * @see ResourceContentService#get(String)
     */
    public void loadFile(final String fileUri,
                         final RemoteCallback<String> callback,
                         final ErrorCallback<Object> errorCallback) {
        resourceContentService.get(fileUri).then((IThenable.ThenOnFulfilledCallbackFn<String, Void>) fileContent -> {
            callback.callback(fileContent);
            return promises.resolve();
        })
                .catch_(error -> {
                    errorCallback.error("Error " + error, new Throwable("Failed to load file " + fileUri));
                    return null;
                })
        ;
    }

    /**
     * Get the <code>List&lt;String&gt;</code> from the project/workspace where the editor is running
     * and returns it inside the given callback
     *
     * @param callback      The <code>RemoteCallback</code> to be invoked on success
     * @param errorCallback The <code>ErrorCallback</code> to be invoked on failure
     * @see ResourceContentService#list(String)
     */
    public void getAllItems(final RemoteCallback<List<String>> callback,
                            final ErrorCallback<Object> errorCallback) {
        getFilteredItems("*", callback, errorCallback);
    }

    /**
     * Get <b>filtered</b> <code>List&lt;String&gt;</code> from the project/workspace where the editor is running
     * and returns it inside the given callback
     *
     * @param pattern       A GLOB pattern to filter files. To list all files use "*"
     * @param callback      The <code>RemoteCallback</code> to be invoked on success
     * @param errorCallback The <code>ErrorCallback</code> to be invoked on failure
     * @see ResourceContentService#list(String)
     */
    public void getFilteredItems(final String pattern,
                                 final RemoteCallback<List<String>> callback,
                                 final ErrorCallback<Object> errorCallback) {
        resourceContentService.list(pattern).then(fileList -> {
            callback.callback(Arrays.asList(fileList));
            return promises.resolve();
        })
                .catch_(error -> {
                    errorCallback.error("Error " + error, new Throwable("Failed to retrieve files with pattern " + pattern));
                    return null;
                });
    }

    /**
     * Get <b>filtered</b> <code>List&lt;String&gt;</code> from the project/workspace where the editor is running
     * and returns it in an Promise
     *
     * @param pattern A GLOB pattern to filter files. To list all files use "*"
     * @param options The <code>ResourceListOptions</code> used to find the files
     * @return A <code>Promise</code> with the files
     */
    public Promise<String[]> getFilteredItems(final String pattern,
                                              final ResourceListOptions options) {
        return resourceContentService.list(pattern, options);
    }

    /**
     * Load the file at given <b>uri</code> and returns its content in an Promise
     *
     * @param fileUri The resource <b>uri</code> relative to the workspace/project or full path to the file
     * @return The content of the file in an <code>Promise</code>
     */
    public Promise<String> loadFile(final String fileUri) {
        return resourceContentService.get(fileUri);
    }
}
