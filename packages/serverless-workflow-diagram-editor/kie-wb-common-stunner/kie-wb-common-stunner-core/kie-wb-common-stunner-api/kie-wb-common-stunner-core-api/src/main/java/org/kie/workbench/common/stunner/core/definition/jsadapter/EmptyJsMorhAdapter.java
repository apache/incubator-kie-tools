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


package org.kie.workbench.common.stunner.core.definition.jsadapter;

import java.util.ArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import org.kie.workbench.common.stunner.core.definition.adapter.MorphAdapter;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.morph.MorphProperty;

@ApplicationScoped
public class EmptyJsMorhAdapter implements MorphAdapter<Object> {

    @Override
    public <T> Iterable<MorphDefinition> getMorphDefinitions(T definition) {
        return new ArrayList<>();
    }

    @Override
    public Iterable<MorphDefinition> getMorphDefinitions(String id, String baseId) {
        return new ArrayList<>();
    }

    @Override
    public <T> Iterable<MorphProperty> getMorphProperties(T definition) {
        return new ArrayList<>();
    }

    @Override
    public <T> Iterable<String> getTargets(T definition, MorphDefinition morphDefinition) {
        return new ArrayList<>();
    }

    @Override
    public <T> T morph(Object source, MorphDefinition definition, String target) {
        return null;
    }

    @Override
    public boolean accepts(Class<?> type) {
        return true;
    }
}
