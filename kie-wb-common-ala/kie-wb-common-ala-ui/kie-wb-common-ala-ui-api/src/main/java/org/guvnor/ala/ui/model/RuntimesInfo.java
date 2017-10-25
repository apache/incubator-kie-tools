/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.model;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.MapsTo;

/**
 * Class for modelling the runtimes associated with a provider.
 */
public class RuntimesInfo {

    private Provider provider;

    private Collection<RuntimeListItem> runtimeItems = new ArrayList<>();

    public RuntimesInfo(@MapsTo("provider") final Provider provider,
                        @MapsTo("runtimeItems") final Collection<RuntimeListItem> runtimeItems) {
        this.provider = provider;
        this.runtimeItems = runtimeItems;
    }

    public Provider getProvider() {
        return provider;
    }

    public Collection<RuntimeListItem> getRuntimeItems() {
        return runtimeItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RuntimesInfo that = (RuntimesInfo) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) {
            return false;
        }
        return runtimeItems != null ? runtimeItems.equals(that.runtimeItems) : that.runtimeItems == null;
    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (runtimeItems != null ? runtimeItems.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
