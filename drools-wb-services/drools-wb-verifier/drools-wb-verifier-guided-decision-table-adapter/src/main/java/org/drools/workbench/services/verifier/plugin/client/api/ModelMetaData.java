/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client.api;

import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class ModelMetaData {

    private final Pattern52 pattern;

    private final String factType;
    private final String binding;

    private final ModelMetaDataEnhancer.PatternType patternType;

    public ModelMetaData(final Pattern52 pattern,
                         final ModelMetaDataEnhancer.PatternType patternType) {
        this.pattern = pattern;
        this.patternType = patternType;
        this.factType = null;
        this.binding = null;
    }

    public ModelMetaData(final String factType,
                         final String binding,
                         final ModelMetaDataEnhancer.PatternType patternType) {
        this.patternType = patternType;
        this.factType = factType;
        this.binding = binding;
        this.pattern = null;
    }

    ModelMetaData(@MapsTo("pattern") final Pattern52 pattern,
                  @MapsTo("factType") final String factType,
                  @MapsTo("binding") final String binding,
                  @MapsTo("patternType") final ModelMetaDataEnhancer.PatternType patternType) {
        this.pattern = pattern;
        this.factType = factType;
        this.binding = binding;
        this.patternType = patternType;
    }

    public Pattern52 getPattern() {
        return pattern;
    }

    public ModelMetaDataEnhancer.PatternType getPatternType() {
        return patternType;
    }

    public String getFactType() {
        if (pattern != null) {
            return pattern.getFactType();
        } else {
            return factType;
        }
    }

    public String getBoundName() {
        if (pattern != null) {
            return pattern.getBoundName();
        } else {
            return binding;
        }
    }
}
