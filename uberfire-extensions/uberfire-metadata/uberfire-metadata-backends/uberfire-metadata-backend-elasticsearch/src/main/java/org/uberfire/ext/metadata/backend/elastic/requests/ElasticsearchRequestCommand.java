/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.metadata.backend.elastic.requests;

import java.io.IOException;

import org.elasticsearch.client.RestClient;
import org.uberfire.ext.metadata.backend.elastic.exceptions.MetadataException;

public abstract class ElasticsearchRequestCommand {

    public static String GET = "GET";
    public static String POST = "POST";
    public static String PUT = "PUT";

    protected final RestClient client;
    private String method;
    private String endpoint;

    public ElasticsearchRequestCommand(RestClient lowLevel) {
        this.client = lowLevel;
    }

    public RestClient getClient() {
        return client;
    }

    public void get() {
        try {
            this.client.performRequest(this.getMethod(),
                                       this.getEndpoint());
        } catch (IOException e) {
            throw new MetadataException("Can't perform request",
                                        e);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
