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


package org.kie.workbench.common.stunner.core.client.shape;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;

@ApplicationScoped
public class ImageStripRegistry {

    private final ManagedInstance<ImageStrip> stripInstances;

    // CDI proxy.
    protected ImageStripRegistry() {
        this(null);
    }

    @Inject
    public ImageStripRegistry(final @Any ManagedInstance<ImageStrip> stripInstances) {
        this.stripInstances = stripInstances;
    }

    public ImageStrip get(final String name) {
        return StreamSupport.stream(stripInstances.spliterator(),
                                    false)
                .filter(strip -> getName(strip).equals(name))
                .findAny()
                .get();
    }

    public ImageStrip get(final Class<? extends ImageStrip> stripType) {
        final ManagedInstance<? extends ImageStrip> i = stripInstances.select(stripType);
        if (!i.isUnsatisfied() && !i.isAmbiguous()) {
            return i.get();
        }
        return null;
    }

    public ImageStrip[] get(final Annotation... qualifiers) {
        final List<ImageStrip> result = new LinkedList<>();
        for (Annotation qualifier : qualifiers) {
            final ManagedInstance<ImageStrip> i = stripInstances.select(qualifier);
            if (!i.isUnsatisfied() && !i.isAmbiguous()) {
                result.add(i.get());
            }
        }
        return result.toArray(new ImageStrip[result.size()]);
    }

    @PreDestroy
    public void destroy() {
        stripInstances.destroyAll();
    }

    @SuppressWarnings("unchecked")
    public static String getName(final ImageStrip strip) {
        // Notice use of the super class as the instances consumed are managed instances (proxies)
        return getName((Class<? extends ImageStrip>) strip.getClass().getSuperclass());
    }

    public static String getName(final Class<? extends ImageStrip> stripType) {
        return stripType.getName();
    }
}
