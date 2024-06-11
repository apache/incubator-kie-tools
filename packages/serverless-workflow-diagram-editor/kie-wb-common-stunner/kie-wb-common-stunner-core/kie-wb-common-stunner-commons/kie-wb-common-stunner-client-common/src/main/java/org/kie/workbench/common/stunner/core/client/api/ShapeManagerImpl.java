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


package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.ShapeSetThumbProvider;

@ApplicationScoped
public class ShapeManagerImpl implements ShapeManager {

    private final ManagedInstance<ShapeSet> shapeSetsInstances;
    private final ManagedInstance<ShapeSetThumbProvider> thumbProvidersInstances;

    private final List<ShapeSet<?>> shapeSets = new LinkedList<>();
    private final List<ShapeSetThumbProvider> thumbProviders = new LinkedList<>();

    protected ShapeManagerImpl() {
        this(null,
             null);
    }

    @Inject
    public ShapeManagerImpl(final @Any ManagedInstance<ShapeSet> shapeSetsInstances,
                            final @Any ManagedInstance<ShapeSetThumbProvider> thumbProvidersInstances) {
        this.shapeSetsInstances = shapeSetsInstances;
        this.thumbProvidersInstances = thumbProvidersInstances;
    }

    @PostConstruct
    public void init() {
        shapeSetsInstances.forEach(shapeSets::add);
        thumbProvidersInstances.forEach(thumbProviders::add);
    }

    @Override
    public Collection<ShapeSet<?>> getShapeSets() {
        return shapeSets;
    }

    @Override
    public ShapeSet<?> getShapeSet(final String id) {
        checkNotNull("id", id);
        return shapeSets.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ShapeSet<?> getDefaultShapeSet(final String defSetId) {
        checkNotNull("defSetId", defSetId);
        return shapeSets.stream()
                .filter(s -> defSetId.equals(s.getDefinitionSetId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getThumbnail(final String definitionSetId) {
        checkNotNull("definitionSetId", definitionSetId);
        final ShapeSetThumbProvider p = thumbProviders.stream()
                .filter(t -> t.thumbFor(definitionSetId))
                .findFirst()
                .orElse(null);
        return null != p ? p.getThumbnailUri() : null;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }
}
