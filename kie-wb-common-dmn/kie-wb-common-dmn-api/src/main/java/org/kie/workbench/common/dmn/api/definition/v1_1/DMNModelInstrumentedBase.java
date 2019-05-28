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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;

public abstract class DMNModelInstrumentedBase implements DMNDefinition {

    private Map<String, String> nsContext = new HashMap<>();
    private Map<QName, String> additionalAttributes = new HashMap<>();

    private DMNModelInstrumentedBase parent;

    @Portable
    public enum Namespace {

        FEEL("feel", org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_FEEL),
        DMN("dmn", org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMN),
        KIE("kie", org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_KIE),
        DEFAULT(QName.DEFAULT_NS_PREFIX, "https://kiegroup.org/dmn/"),
        DMNDI("dmndi", org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DMNDI),
        DI("di", org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DI),
        DC("dc", org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase.URI_DC);

        private String prefix;
        private String uri;

        Namespace(final String prefix,
                  final String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getUri() {
            return uri;
        }

    }

    @NonPortable
    protected static abstract class BaseNodeBuilder<T extends DMNModelInstrumentedBase> implements Builder<T> {

    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Map<String, String> getNsContext() {
        return nsContext;
    }

    public void setAdditionalAttributes(Map<QName, String> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public Map<QName, String> getAdditionalAttributes() {
        return additionalAttributes;
    }

    public DMNModelInstrumentedBase getParent() {
        return parent;
    }

    public void setParent(final DMNModelInstrumentedBase parent) {
        this.parent = parent;
    }

    public Optional<String> getPrefixForNamespaceURI(final String namespaceURI) {
        if (nsContext != null && nsContext.containsValue(namespaceURI)) {
            return nsContext.entrySet().stream()
                    .filter(kv -> Objects.equals(kv.getValue(), namespaceURI))
                    .findFirst()
                    .map(Map.Entry::getKey);
        }
        if (this.parent != null) {
            return parent.getPrefixForNamespaceURI(namespaceURI);
        }
        return Optional.empty();
    }

    public String getDefaultNamespace() {
        if (nsContext != null && nsContext.containsKey("")) {
            return nsContext.entrySet().stream()
                       .filter(kv -> Objects.equals(kv.getKey(), ""))
                       .findFirst()
                       .map(Map.Entry::getValue)
                       .get();
        }
        if (this.parent != null) {
            return parent.getDefaultNamespace();
        }
        return "";
    }
}
