/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.lookup.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.Set;

@Portable
public final class DefinitionRepresentationImpl implements DefinitionRepresentation {

    private final String id;
    private final boolean isNode;
    private final Set<String> labels;

    public DefinitionRepresentationImpl( @MapsTo( "id" ) String id,
                                         @MapsTo( "isNode" ) boolean isNode,
                                         @MapsTo( "labels" ) Set<String> labels ) {
        this.id = id;
        this.isNode = isNode;
        this.labels = labels;
    }

    @Override
    public String getDefinitionId() {
        return id;
    }

    @Override
    public boolean isNode() {
        return isNode;
    }

    @Override
    public Set<String> getLabels() {
        return labels;
    }
}
