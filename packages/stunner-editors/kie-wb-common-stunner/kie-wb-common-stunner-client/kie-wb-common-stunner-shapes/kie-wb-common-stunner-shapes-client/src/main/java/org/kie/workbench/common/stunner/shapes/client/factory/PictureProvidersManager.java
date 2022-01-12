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
package org.kie.workbench.common.stunner.shapes.client.factory;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureProvider;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class PictureProvidersManager {

    private static Logger LOGGER = Logger.getLogger(PictureProvidersManager.class.getName());

    private final ManagedInstance<PictureProvider> pictureProviderManagedInstances;
    private final List<PictureProvider> providers = new LinkedList<>();

    @Inject
    public PictureProvidersManager(final ManagedInstance<PictureProvider> pictureProviderManagedInstances) {
        this.pictureProviderManagedInstances = pictureProviderManagedInstances;
    }

    @PostConstruct
    public void init() {
        pictureProviderManagedInstances.forEach(providers::add);
    }

    @SuppressWarnings("unchecked")
    public SafeUri getUri(final Object source) {
        checkNotNull("source",
                     source);
        final Class<?> type = source.getClass();
        PictureProvider provider = providers.stream()
                .filter(pictureProvider -> pictureProvider.getSourceType().equals(type)).findFirst().orElse(null);
        if (null != provider) {
            return provider.getThumbnailUri(source);
        } else {
            LOGGER.log(Level.SEVERE,
                       "Picture provider not found for [" + source + "]");
            return null;
        }
    }

    @PreDestroy
    public void destroy() {
        providers.clear();
        pictureProviderManagedInstances.destroyAll();
    }
}
