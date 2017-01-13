/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder;

import java.util.Map;

import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class BootstrapObjectBuilder<W, T extends Element<View<W>>> extends AbstractObjectBuilder<W, T> {

    GraphObjectBuilderFactory buildersFactory;

    public BootstrapObjectBuilder(final GraphObjectBuilderFactory buildersFactory) {
        super();
        this.buildersFactory = buildersFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractObjectBuilder<W, T> stencil(final String oryxStencilId) {
        assert nodeId != null;
        AbstractObjectBuilder<W, T> builder = (AbstractObjectBuilder<W, T>) buildersFactory.builderFor(oryxStencilId);
        builder.nodeId(this.nodeId);
        if (!properties.isEmpty()) {
            for (Map.Entry<String, String> entry : this.properties.entrySet()) {
                builder.property(entry.getKey(),
                                 entry.getValue());
            }
        }
        if (!outgoingResourceIds.isEmpty()) {
            for (String outRefId : outgoingResourceIds) {
                builder.out(outRefId);
            }
        }
        if (null != boundLR) {
            builder.boundLR(boundLR[0],
                            boundLR[1]);
        }
        if (null != boundUL) {
            builder.boundUL(boundUL[0],
                            boundUL[1]);
        }
        return builder;
    }

    @Override
    protected T doBuild(final BuilderContext context) {
        return null;
    }

    @Override
    public T build(final BuilderContext context) {
        return null;
    }
}
