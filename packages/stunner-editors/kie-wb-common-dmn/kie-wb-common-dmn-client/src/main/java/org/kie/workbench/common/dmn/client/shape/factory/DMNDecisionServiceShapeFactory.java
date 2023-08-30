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
package org.kie.workbench.common.dmn.client.shape.factory;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactory;
import org.kie.workbench.common.dmn.client.shape.def.DMNDecisionServiceSVGShapeDef;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;

@Dependent
public class DMNDecisionServiceShapeFactory implements ShapeDefFactory<DecisionService, DMNDecisionServiceSVGShapeDef, DMNDecisionServiceSVGMutableShapeImpl> {

    private final DMNDecisionServiceSVGViewFactory dmnViewFactory;

    protected DMNDecisionServiceShapeFactory() {
        this(null);
    }

    @Inject
    public DMNDecisionServiceShapeFactory(final DMNDecisionServiceSVGViewFactory dmnViewFactory) {
        this.dmnViewFactory = dmnViewFactory;
    }

    @Override
    public DMNDecisionServiceSVGMutableShapeImpl newShape(final DecisionService instance,
                                                          final DMNDecisionServiceSVGShapeDef shapeDef) {
        final SVGShapeView view = shapeDef.newViewInstance(dmnViewFactory, instance);
        return new DMNDecisionServiceSVGMutableShapeImpl(shapeDef, (SVGShapeViewImpl) view);
    }
}
