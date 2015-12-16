/*
* Copyright 2014 Red Hat, Inc. and/or its affiliates.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.drools.workbench.screens.guided.dtree.client.widget;

import org.drools.workbench.screens.guided.dtree.client.widget.shapes.BaseGuidedDecisionTreeShape;
import org.uberfire.ext.wires.core.api.factories.ShapeDropContext;

public class GuidedDecisionTreeDropContext implements ShapeDropContext<BaseGuidedDecisionTreeShape> {

    private BaseGuidedDecisionTreeShape context;

    @Override
    public BaseGuidedDecisionTreeShape getContext() {
        return context;
    }

    @Override
    public void setContext( final BaseGuidedDecisionTreeShape context ) {
        this.context = context;
    }

}
