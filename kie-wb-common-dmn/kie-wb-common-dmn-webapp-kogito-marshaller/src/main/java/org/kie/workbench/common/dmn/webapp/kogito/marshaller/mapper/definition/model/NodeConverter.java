/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

interface NodeConverter<D, W extends DMNModelInstrumentedBase> {

    Node<View<W>, ?> nodeFromDMN(final D source,
                                 final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer);

    D dmnFromNode(final Node<View<W>, ?> source,
                  final Consumer<JSITComponentWidths> componentWidthsConsumer);
}
