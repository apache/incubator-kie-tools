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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;

@ApplicationScoped
public class JsDefinitionSetAdapter implements DefinitionSetAdapter<Object> {

    private StunnerTranslationService translationService;
    private String definitionsField;
    private Annotation domainQualifier;

    @Override
    public String getId(Object pojo) {
        return BindableAdapterUtils.getDefinitionSetId(pojo.getClass());
    }

    @Override
    public String getDomain(Object pojo) {
        return BindableAdapterUtils.getDefinitionSetDomain(pojo.getClass());
    }

    @Override
    public String getDescription(Object pojo) {
        return translationService.getDefinitionSetDescription(getId(pojo));
    }

    @Override
    public Set<String> getDefinitions(Object pojo) {
        JsPropertyMap<Object> map = Js.asPropertyMap(pojo);
        String definitions = Js.uncheckedCast(map.get(definitionsField));
        String[] split = definitions.split(",");
        return Arrays.stream(split).collect(Collectors.toSet());
    }

    @Override
    public Class<? extends ElementFactory> getGraphFactoryType(Object pojo) {
        return GraphFactory.class;
    }

    @Override
    public Annotation getQualifier(Object pojo) {
        return domainQualifier;
    }

    @Override
    public Optional<String> getSvgNodeId(Object pojo) {
        return translationService.getDefinitionSetSvgNodeId(getId(pojo));
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accepts(Class<?> type) {
        return true;
    }

    public void setDomainQualifier(Annotation domainQualifier) {
        this.domainQualifier = domainQualifier;
    }

    public void setDefinitionsField(String definitionsField) {
        this.definitionsField = definitionsField;
    }

    public void setTranslationService(StunnerTranslationService translationService) {
        this.translationService = translationService;
    }

    public static String toClassNames(Class... types) {
        return Arrays.stream(types).map(t -> t.getName()).collect(Collectors.joining(","));
    }
}
