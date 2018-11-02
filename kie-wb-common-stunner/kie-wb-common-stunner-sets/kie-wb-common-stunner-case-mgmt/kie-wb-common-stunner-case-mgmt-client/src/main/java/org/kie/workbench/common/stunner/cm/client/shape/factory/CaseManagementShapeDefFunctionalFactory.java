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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgShapeDef;
import org.kie.workbench.common.stunner.cm.qualifiers.CaseManagementEditor;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

@Dependent
@CaseManagementEditor
public class CaseManagementShapeDefFunctionalFactory<W, D extends ShapeDef, S extends Shape>
        extends ShapeDefFunctionalFactory<W, CaseManagementSvgShapeDef, S> {

    @Override
    @SuppressWarnings("unchecked")
    public S newShape(final W instance, final CaseManagementSvgShapeDef shapeDef) {

        // using getClass in lieu of getType to avoid cast to SVGShapeViewDef.class for shapeDefFactory callback
        return newShape(instance, shapeDef, shapeDef.getClass());
    }
}
