/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.workitem.service;

public class WorkItemDefinitionRemoteRequest {

    public static WorkItemDefinitionRemoteRequest build(final String uri) {
        return new WorkItemDefinitionRemoteRequest(uri, new String[0]);
    }

    public static WorkItemDefinitionRemoteRequest build(final String uri,
                                                        final String[] names) {
        return new WorkItemDefinitionRemoteRequest(uri,
                                                   names);
    }

    public static WorkItemDefinitionRemoteRequest build(final String uri,
                                                        final String names) {
        return new WorkItemDefinitionRemoteRequest(uri,
                                                   parse(names));
    }

    private static String[] parse(final String names) {
        return null != names && names.trim().length() > 0 ?
                names.split("\\s*,\\s*") :
                new String[0];
    }

    private final String uri;
    private final String[] names;

    private WorkItemDefinitionRemoteRequest(final String uri,
                                            final String[] names) {
        this.uri = uri;
        this.names = names;
    }

    public String getUri() {
        return uri;
    }

    public String[] getNames() {
        return names;
    }
}
