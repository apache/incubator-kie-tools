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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.Context;

@Dependent
public class ContextEditor implements ContextEditorView.Editor {

    private ContextEditorView view;

    @Inject
    public ContextEditor(final ContextEditorView view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public IsElement getView() {
        return view;
    }

    @Override
    public void setHasName(final Optional<HasName> hasName) {
        view.setHasName(hasName);
    }

    @Override
    public void setExpression(final Context expression) {
        view.setExpression(expression);
    }
}
