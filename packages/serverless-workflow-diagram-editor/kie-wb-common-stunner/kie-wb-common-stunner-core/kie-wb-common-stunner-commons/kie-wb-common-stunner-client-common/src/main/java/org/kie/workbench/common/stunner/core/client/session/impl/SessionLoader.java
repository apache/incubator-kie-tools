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


package org.kie.workbench.common.stunner.core.client.session.impl;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistryLoader;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class SessionLoader {

    private final DefinitionUtils definitionUtils;
    private final StunnerPreferencesRegistryLoader preferencesRegistryLoader;
    private final ManagedInstance<SessionInitializer> initializerInstances;
    private final List<SessionInitializer> initializers;

    @Inject
    public SessionLoader(final DefinitionUtils definitionUtils,
                         final StunnerPreferencesRegistryLoader preferencesRegistryLoader,
                         final @Any ManagedInstance<SessionInitializer> beanInstances) {
        this.definitionUtils = definitionUtils;
        this.preferencesRegistryLoader = preferencesRegistryLoader;
        this.initializerInstances = beanInstances;
        this.initializers = new LinkedList<>();
    }

    public void load(final Metadata metadata,
                     final ParameterizedCommand<StunnerPreferences> completeCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        final String definitionSetId = metadata.getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(definitionSetId);
        preferencesRegistryLoader.load(metadata,
                                       prefs -> {
                                           loadInitializers(metadata,
                                                            qualifier,
                                                            () -> {
                                                                completeCallback.execute(prefs);
                                                            });
                                       },
                                       errorCallback);
    }

    public void destroy() {
        initializers.forEach(SessionInitializer::destroy);
        initializers.clear();
        initializerInstances.destroyAll();
    }

    private void loadInitializers(final Metadata metadata,
                                  final Annotation qualifier,
                                  final Command callback) {
        initializerInstances.select(DefinitionManager.DEFAULT_QUALIFIER).forEach(initializers::add);
        initializerInstances.select(qualifier).forEach(initializers::add);
        loadInitializer(metadata,
                        0,
                        callback);
    }

    private void loadInitializer(final Metadata metadata,
                                 final int index,
                                 final Command callback) {
        if (index < initializers.size()) {
            final SessionInitializer bean = initializers.get(index);
            bean.init(metadata,
                      () -> {
                          loadInitializer(metadata,
                                          index + 1,
                                          callback);
                      });
        } else {
            callback.execute();
        }
    }

    List<SessionInitializer> getInitializers() {
        return initializers;
    }
}
