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


package org.appformer.kogito.bridge.client.resource;

import elemental2.promise.Promise;
import org.appformer.kogito.bridge.client.resource.interop.ResourceContentOptions;
import org.appformer.kogito.bridge.client.resource.interop.ResourceListOptions;

/**
 * Service to access resources in the project or workspace where the editor is open
 *
 */
public interface ResourceContentService {

    /**
     * Returns a resource's content
     *
     * @param uri
     *  the resource URI relative to the workspace/project
     * @return
     * The resource content or null if the resource is not available
     */
    public Promise<String> get(String uri);

    /**
     * Returns a resource's content
     * 
     * @param uri
     *  The resource URI relative to the workspace/project
     * @param options
     *  Options when retrieving the resource content
     * @return
     * The resource content or null if the resource is not available
     */
    public Promise<String> get(String uri, ResourceContentOptions options);

    /**
     * List files from the project/workspace where the editor is running
     *
     * @param pattern
     * A GLOB pattern to filter files. To list all files use "*"
     * @return
     * The list of matched resources URIs
     */
    public Promise<String[]> list(String pattern);

    /**
     * List files from the project/workspace where the editor is running
     *
     * @param pattern
     * A GLOB pattern to filter files. To list all files use "*"
     * @param options
     *  Options when retrieving the resource list
     * @return
     * The list of matched resources URIs
     */
    public Promise<String[]> list(String pattern, ResourceListOptions options);

}
