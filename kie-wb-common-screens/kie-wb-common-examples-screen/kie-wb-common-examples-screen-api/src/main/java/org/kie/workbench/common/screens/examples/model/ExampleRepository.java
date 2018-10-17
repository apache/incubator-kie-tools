/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.examples.model;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ExampleRepository {

    private String url;
    private Credentials credentials;
    private boolean isUrlValid = true;

    public ExampleRepository(final String url) {
        this.url = url;
        this.isUrlValid = true;
    }

    public ExampleRepository(final @MapsTo("url") String url,
                             final @MapsTo("credentials") Credentials credentials) {
        this.url = url;
        this.credentials = credentials;
    }

    public String getUrl() {
        return url;
    }

    public boolean isUrlValid() {
        return isUrlValid;
    }

    public void setUrlValid(final boolean isUrlValid) {
        this.isUrlValid = isUrlValid;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExampleRepository)) {
            return false;
        }

        ExampleRepository that = (ExampleRepository) o;

        if (isUrlValid != that.isUrlValid) {
            return false;
        }
        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        return !(credentials != null ? !credentials.equals(that.credentials) : that.credentials != null);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (isUrlValid ? 1 : 0);
        result = ~~result;
        result = 31 * result + (credentials != null ? credentials.hashCode() : 0);
        result = ~~result;
        return result;
    }
}
