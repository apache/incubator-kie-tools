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
 *
 */

package org.kie.workbench.common.screens.examples.model;

import java.util.Arrays;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ExampleProjectError {

    private final String id;
    private final String description;
    private Object[] values;

    public ExampleProjectError(final @MapsTo("id") String id,
                               final @MapsTo("description") String description,
                               final @MapsTo("value") Object... values) {
        this.id = id;
        this.description = description;
        if (values == null) {
            this.values = new Object[0];
        } else {
            this.values = values;
        }
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Object[] getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExampleProjectError)) {
            return false;
        }

        ExampleProjectError that = (ExampleProjectError) o;

        return id.equals(that.id) &&
                description.equals(that.description) &&
                values == that.values;
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = ~~result;
        result = 31 * result + (values != null ? Arrays.hashCode(values) : 0);
        result = ~~result;
        return result;
    }
}
