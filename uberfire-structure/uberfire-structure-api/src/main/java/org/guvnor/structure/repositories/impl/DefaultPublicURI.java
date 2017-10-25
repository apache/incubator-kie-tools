/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.repositories.impl;

import org.guvnor.structure.repositories.PublicURI;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DefaultPublicURI implements PublicURI {

    private String protocol;
    private String uri;

    public DefaultPublicURI() {
    }

    public DefaultPublicURI(final String uri) {
        this("",
             uri);
    }

    public DefaultPublicURI(final String protocol,
                            final String uri) {
        this.protocol = protocol;
        this.uri = uri;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getURI() {
        return uri;
    }

    public void setURI(final String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultPublicURI)) {
            return false;
        }

        DefaultPublicURI publicURI = (DefaultPublicURI) o;

        if (uri != null ? !uri.equals(publicURI.uri) : publicURI.uri != null) {
            return false;
        }
        if (protocol != null ? !protocol.equals(publicURI.protocol) : publicURI.protocol != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = protocol != null ? protocol.hashCode() : 0;
        result = ~~result;
        result = 31 * result + (uri != null ? uri.hashCode() : 0);
        result = ~~result;
        return result;
    }

    @Override
    public String toString() {
        return "DefaultPublicURI{" +
                "protocol='" + protocol + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
