/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.api;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.ShapeSet;
import org.kie.workbench.common.stunner.core.client.ShapeSetThumbProvider;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

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
        checkNotNull("id",
                     id);
        return shapeSets.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public ShapeSet<?> getDefaultShapeSet(final String defSetId) {
        checkNotNull("defSetId",
                     defSetId);
        return shapeSets.stream()
                .filter(s -> defSetId.equals(s.getDefinitionSetId()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public SafeUri getThumbnail(final String definitionSetId) {
        checkNotNull("definitionSetId",
                     definitionSetId);
        final ShapeSetThumbProvider p = thumbProviders.stream()
                .filter(t -> t.thumbFor(definitionSetId))
                .findFirst()
                .orElse(null);
        return null != p ? p.getThumbnailUri() : null;
    }
}
