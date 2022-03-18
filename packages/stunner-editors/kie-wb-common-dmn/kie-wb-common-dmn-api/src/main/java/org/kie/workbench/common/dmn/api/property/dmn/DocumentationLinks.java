/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.api.property.dmn;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.DMNProperty;

@Portable
public class DocumentationLinks implements DMNProperty {

    private List<DMNExternalLink> links;

    public DocumentationLinks() {
        this.links = new ArrayList<>();
    }

    public List<DMNExternalLink> getLinks() {
        return links;
    }

    public void setLinks(final List<DMNExternalLink> links) {
        this.links = links;
    }

    public void addLink(final DMNExternalLink dmnExternalLink) {
        getLinks().add(dmnExternalLink);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentationLinks)) {
            return false;
        }

        final DocumentationLinks that = (DocumentationLinks) o;

        return links != null ? links.equals(that.links) : that.links == null;
    }

    @Override
    public int hashCode() {
        return links != null ? links.hashCode() : 0;
    }
}
