/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common;

import java.util.Optional;

import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.AbstractCanvasHandlerElementEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

public class GuidedTourUtils {

    private final TextPropertyProviderFactory textPropertyProviderFactory;

    @Inject
    public GuidedTourUtils(final TextPropertyProviderFactory textPropertyProviderFactory) {
        this.textPropertyProviderFactory = textPropertyProviderFactory;
    }

    public Optional<String> getName(final AbstractCanvasHandlerElementEvent event) {
        final Element<?> element = event.getElement();
        if (element instanceof NodeImpl) {
            return Optional.ofNullable(getName(element));
        }
        return Optional.empty();
    }

    public String getName(final Element<?> element) {
        return getName(asNodeImpl(element));
    }

    public String getName(final NodeImpl<View> node) {
        final TextPropertyProvider provider = textPropertyProviderFactory.getProvider(node);
        return provider.getText(node);
    }

    @SuppressWarnings("unchecked")
    public NodeImpl<View> asNodeImpl(final Element<?> element) {
        return (NodeImpl<View>) element.asNode();
    }
}
