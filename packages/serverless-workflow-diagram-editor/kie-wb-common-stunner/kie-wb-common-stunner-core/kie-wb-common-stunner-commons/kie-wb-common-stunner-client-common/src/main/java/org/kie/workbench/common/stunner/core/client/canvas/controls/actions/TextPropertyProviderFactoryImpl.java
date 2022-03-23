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
package org.kie.workbench.common.stunner.core.client.canvas.controls.actions;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class TextPropertyProviderFactoryImpl implements TextPropertyProviderFactory {

    private List<TextPropertyProvider> providers = new ArrayList<>();

    public TextPropertyProviderFactoryImpl() {
        //CDI proxy
    }

    @Inject
    public TextPropertyProviderFactoryImpl(final ManagedInstance<TextPropertyProvider> providers) {
        for (TextPropertyProvider provider : providers) {
            this.providers.add(provider);
        }
        this.providers.sort((p1,
                             p2) -> p1.getPriority() - p2.getPriority());
    }

    @Override
    public TextPropertyProvider getProvider(final Element<? extends Definition> element) {
        final TextPropertyProvider provider = providers
                .stream()
                .filter((p) -> p.supports(element))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No TextPropertyProvider found for '" + element.getClass().getName() + "'."));
        return provider;
    }
}
