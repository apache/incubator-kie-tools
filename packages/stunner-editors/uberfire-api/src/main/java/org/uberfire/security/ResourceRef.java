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


package org.uberfire.security;

import java.util.ArrayList;
import java.util.List;

/**
 * A resource reference. Useful when a link to the real Resource object is not available.
 */
public class ResourceRef implements Resource {

    private String identifier;
    private ResourceType type;
    private List<Resource> dependencies;

    public ResourceRef(String identifier,
                       ResourceType type) {
        this(identifier,
             type,
             null);
    }

    public ResourceRef(String identifier,
                       ResourceType type,
                       List<Resource> dependencies) {
        this.identifier = identifier;
        this.type = type;
        this.dependencies = new ArrayList<>();
        this.setDependencies(dependencies);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public ResourceType getResourceType() {
        return type;
    }

    public void setResourceType(ResourceType type) {
        this.type = type;
    }

    @Override
    public List<Resource> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Resource> deps) {
        this.dependencies.clear();
        if (deps != null) {
            this.dependencies.addAll(deps);
        }
    }
}
