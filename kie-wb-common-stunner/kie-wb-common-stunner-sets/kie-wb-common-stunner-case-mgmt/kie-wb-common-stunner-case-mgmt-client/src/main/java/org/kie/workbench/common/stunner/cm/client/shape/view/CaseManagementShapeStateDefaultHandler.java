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

package org.kie.workbench.common.stunner.cm.client.shape.view;

import java.util.function.Supplier;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.LienzoShapeView;

/**
 * TODO: this class was introduced as part of https://issues.jboss.org/browse/RHPAM-1706 as a quick fix.
 * Final solution must be provided as part of https://issues.jboss.org/browse/JBPM-7681
 */
public class CaseManagementShapeStateDefaultHandler
        extends ShapeStateDefaultHandler {

    /**
     * TODO: this implementation should be reviewd as part of JBPM-7681
     * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
     */
    public ShapeStateDefaultHandler setRenderType(final RenderType renderType) {
        handler.getAttributesHandler().useAttributes(renderType.stateAttributesProvider());
        return this;
    }

    /**
     * TODO: this implementation should be reviewd as part of JBPM-7681
     * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
     */
    public ShapeStateDefaultHandler setBorderShape(final Supplier<LienzoShapeView<?>> shapeSupplier) {
        handler.getAttributesHandler().setView(shapeSupplier);
        borderShapeSupplier = shapeSupplier;
        return this;
    }

    /**
     * TODO: this implementation should be reviewd as part of JBPM-7681
     * @see <a href="https://issues.jboss.org/browse/JBPM-7681">JBPM-7681</a>
     */
    public ShapeStateDefaultHandler setBackgroundShape(final Supplier<LienzoShapeView<?>> shapeSupplier) {
        backgroundShapeSupplier = shapeSupplier;
        return this;
    }
}
