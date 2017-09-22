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

package org.kie.workbench.common.stunner.core.processors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;

import org.kie.workbench.common.stunner.core.definition.property.PropertyMetaTypes;

public class ProcessingContext {

    private static ProcessingContext context;
    private ProcessingEntity definitionSet;
    private final List<ProcessingRule> rules = new ArrayList<>();

    private final ProcessingDefinitionSetAnnotations defSetAnnotations = new ProcessingDefinitionSetAnnotations();
    private final ProcessingDefinitionAnnotations definitionAnnotations = new ProcessingDefinitionAnnotations();
    private final Set<Element> definitionElements = new LinkedHashSet<>();
    private final ProcessingPropertySetAnnotations propertySetAnnotations = new ProcessingPropertySetAnnotations();
    private final Set<Element> propertySetElements = new LinkedHashSet<>();
    private final ProcessingPropertyAnnotations propertyAnnotations = new ProcessingPropertyAnnotations();
    private final Set<Element> propertyElements = new LinkedHashSet<>();
    private final Map<PropertyMetaTypes, String> metaPropertyTypes = new LinkedHashMap<>();
    private final ProcessingMorphingAnnotations morphingAnnotations = new ProcessingMorphingAnnotations();
    private final Set<Element> containmentRuleElementsProcessed = new HashSet<>();
    private final Set<Element> dockingRuleElementsProcessed = new HashSet<>();

    public synchronized static ProcessingContext getInstance() {
        if (null == context) {
            context = new ProcessingContext();
        }
        return context;
    }

    public ProcessingContext() {
    }

    public ProcessingEntity getDefinitionSet() {
        return definitionSet;
    }

    public void setDefinitionSet(final String packageName,
                                 final String className) {
        if (null != this.definitionSet) {
            throw new RuntimeException("Only a single definition set allowed for a single processing.");
        }
        this.definitionSet = new ProcessingEntity(packageName + "." + className,
                                                  className);
    }

    public void addRule(final String id,
                        final ProcessingRule.TYPE type,
                        final StringBuffer content) {
        rules.add(new ProcessingRule(id,
                                     type,
                                     content));
    }

    public List<ProcessingRule> getRules() {
        return rules;
    }

    public ProcessingDefinitionSetAnnotations getDefSetAnnotations() {
        return defSetAnnotations;
    }

    public ProcessingDefinitionAnnotations getDefinitionAnnotations() {
        return definitionAnnotations;
    }

    public ProcessingPropertySetAnnotations getPropertySetAnnotations() {
        return propertySetAnnotations;
    }

    public ProcessingPropertyAnnotations getPropertyAnnotations() {
        return propertyAnnotations;
    }

    public Map<PropertyMetaTypes, String> getMetaPropertyTypes() {
        return metaPropertyTypes;
    }

    public ProcessingMorphingAnnotations getMorphingAnnotations() {
        return morphingAnnotations;
    }

    public Set<Element> getDefinitionElements() {
        return definitionElements;
    }

    public Set<Element> getPropertySetElements() {
        return propertySetElements;
    }

    public Set<Element> getPropertyElements() {
        return propertyElements;
    }

    public Set<Element> getContainmentRuleElementsProcessed() {
        return containmentRuleElementsProcessed;
    }

    public Set<Element> getDockingRuleElementsProcessed() {
        return dockingRuleElementsProcessed;
    }
}
