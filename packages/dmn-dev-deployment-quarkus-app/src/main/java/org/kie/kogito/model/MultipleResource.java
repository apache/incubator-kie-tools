/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.model;

import java.util.List;
import java.util.Objects;

public class MultipleResource {

    private String mainUri;
    private List<Resource> resources;

    public MultipleResource(final String mainUri, final List<Resource> resources) {
        this.mainUri = mainUri;
        this.resources = resources;
    }

    public String getMainUri() {
        return mainUri;
    }

    public void setMainUri(String mainUri) {
        this.mainUri = mainUri;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultipleResource that = (MultipleResource) o;
        return mainUri.equals(that.mainUri) && resources.equals(that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainUri, resources);
    }
}
