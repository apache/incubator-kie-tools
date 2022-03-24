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

import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

/**
 * A factory to provide an instance of {@link TextPropertyProvider} for a given {@link Element}.
 */
public interface TextPropertyProviderFactory {

    /**
     * The priority of any {@link TextPropertyProvider} that should behave as the "catch all" implementation.
     */
    int CATCH_ALL_PRIORITY = Integer.MAX_VALUE;

    /**
     * Gets a provider for the given {@link Element}.
     * @param element The element for which to retrieve the property representing the caption.
     * @return The caption.
     */
    TextPropertyProvider getProvider(final Element<? extends Definition> element);
}
