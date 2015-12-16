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
package org.drools.workbench.screens.guided.dtree.client.widget.factories;

import org.drools.workbench.models.guided.dtree.shared.model.nodes.ActionUpdateNode;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;

public class ActionUpdateFactoryHelper implements FactoryHelper<ActionUpdateNode> {

    private ActionUpdateNode context;
    private boolean isReadOnly;

    public ActionUpdateFactoryHelper( final ActionUpdateNode context,
                                      final boolean isReadOnly ) {
        this.context = context;
        this.isReadOnly = isReadOnly;
    }

    @Override
    public ActionUpdateNode getContext() {
        return this.context;
    }

    @Override
    public void setContext( final ActionUpdateNode context ) {
        this.context = context;
    }

    public boolean isReadOnly() {
        return this.isReadOnly;
    }

}
