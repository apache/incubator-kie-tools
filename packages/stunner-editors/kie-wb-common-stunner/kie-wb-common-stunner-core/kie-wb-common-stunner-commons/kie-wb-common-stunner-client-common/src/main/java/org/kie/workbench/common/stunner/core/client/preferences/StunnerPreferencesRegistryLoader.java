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


package org.kie.workbench.common.stunner.core.client.preferences;

import java.lang.annotation.Annotation;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.session.impl.InstanceUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.preferences.StunnerPreferences;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
public class StunnerPreferencesRegistryLoader {

    private final DefinitionUtils definitionUtils;
    private final ManagedInstance<StunnerPreferencesRegistryHolder> preferencesHolders;
    private final StunnerPreferences preferences;
    private final StunnerTextPreferences textPreferences;

    @Inject
    public StunnerPreferencesRegistryLoader(final DefinitionUtils definitionUtils,
                                            final @Any ManagedInstance<StunnerPreferencesRegistryHolder> preferencesHolders,
                                            final StunnerPreferences preferences,
                                            final StunnerTextPreferences textPreferences) {
        this.definitionUtils = definitionUtils;
        this.preferencesHolders = preferencesHolders;
        this.preferences = preferences;
        this.textPreferences = textPreferences;
    }

    public void load(final Metadata metadata,
                     final ParameterizedCommand<StunnerPreferences> loadCompleteCallback,
                     final ParameterizedCommand<Throwable> errorCallback) {
        final String definitionSetId = metadata.getDefinitionSetId();
        final Annotation qualifier = definitionUtils.getQualifier(definitionSetId);
        final StunnerPreferencesRegistryHolder holder = InstanceUtils.lookup(preferencesHolders,
                                                                             qualifier);
        holder.set(preferences, StunnerPreferences.class);
        holder.set(textPreferences, StunnerTextPreferences.class);
        loadCompleteCallback.execute(preferences);
    }

    @PreDestroy
    public void destroy() {
        preferencesHolders.destroyAll();
    }
}