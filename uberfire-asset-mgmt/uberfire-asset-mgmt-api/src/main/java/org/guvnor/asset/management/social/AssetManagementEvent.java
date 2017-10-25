/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.asset.management.social;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AssetManagementEvent {

    private String processName;

    private String user;

    private String repositoryAlias;

    private String rootURI;

    private Long timestamp;

    private Map<String, String> params = new HashMap<String, String>();

    private List<String> errors = new ArrayList<String>();

    public AssetManagementEvent() {
    }

    public AssetManagementEvent(String processName,
                                String repositoryAlias,
                                String rootURI,
                                String user,
                                Long timestamp,
                                Map<String, String> params) {
        this.processName = processName;
        this.repositoryAlias = repositoryAlias;
        this.rootURI = rootURI;
        this.user = user;
        this.timestamp = timestamp;
        this.params = params;
    }

    public AssetManagementEvent(String processName,
                                String repositoryAlias,
                                String rootURI,
                                String user,
                                Long timestamp) {
        this.processName = processName;
        this.repositoryAlias = repositoryAlias;
        this.rootURI = rootURI;
        this.user = user;
        this.timestamp = timestamp;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRepositoryAlias() {
        return repositoryAlias;
    }

    public void setRepositoryAlias(String repositoryAlias) {
        this.repositoryAlias = repositoryAlias;
    }

    public String getRootURI() {
        return rootURI;
    }

    public void setRootURI(String rootURI) {
        this.rootURI = rootURI;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addParam(String name,
                         String value) {
        params.put(name,
                   value);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public void addError(String error) {
        errors.add(error);
    }
}