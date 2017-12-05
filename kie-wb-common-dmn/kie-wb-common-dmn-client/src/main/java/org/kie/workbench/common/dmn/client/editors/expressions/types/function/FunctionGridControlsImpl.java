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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;
import org.uberfire.client.views.pfly.widgets.Select;
import org.uberfire.mvp.Command;

@Templated
@Dependent
public class FunctionGridControlsImpl implements FunctionGridControls {

    @DataField("addFormalParameter")
    private Div addFormalParameter;

    @DataField("lstKinds")
    private Select lstKinds;

    @DataField("lstExpressionTypes")
    private Select lstExpressionTypes;

    private Presenter presenter;

    public FunctionGridControlsImpl() {
        //CDI proxy
    }

    @Inject
    public FunctionGridControlsImpl(final Div addFormalParameter,
                                    final Select lstKinds,
                                    final Select lstExpressionTypes) {
        this.addFormalParameter = addFormalParameter;
        this.lstKinds = lstKinds;
        this.lstExpressionTypes = lstExpressionTypes;

        setupKindsEventHandler();
        setupExpressionTypesHandler();
    }

    private void setupKindsEventHandler() {
        setupChangeEventHandler(lstKinds,
                                () -> {
                                    final FunctionDefinition.Kind kind = FunctionDefinition.Kind.determineFromString(lstKinds.getValue());
                                    presenter.setKind(kind);
                                });
    }

    private void setupExpressionTypesHandler() {
        setupChangeEventHandler(lstExpressionTypes,
                                () -> {
                                    final ExpressionType type = ExpressionType.valueOf(lstExpressionTypes.getValue());
                                    presenter.setExpressionType(type);
                                });
    }

    private void setupChangeEventHandler(final Select select,
                                         final Command command) {
        // org.uberfire.client.views.pfly.widgets.Select does not work with @EventHandler
        select.getElement().addEventListener("change",
                                             (event) -> command.execute(),
                                             false);
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void initKinds(final List<FunctionDefinition.Kind> kinds) {
        kinds.forEach(k -> lstKinds.addOption(k.code()));
    }

    @Override
    public void initExpressionTypes(final List<ExpressionType> types) {
        types.forEach(t -> lstExpressionTypes.addOption(t.name()));
    }

    @Override
    public void initSelectedKind(final FunctionDefinition.Kind kind) {
        // Setting value directly throws a JavaScript error, probably because the Element is
        // not attached to the DOM at this point. Deferring setting the value works around
        Scheduler.get().scheduleDeferred(() -> lstKinds.refresh(s -> s.setValue(kind.code())));
    }

    @Override
    public void initSelectedExpressionType(final ExpressionType type) {
        // Setting value directly throws a JavaScript error, probably because the Element is
        // not attached to the DOM at this point. Deferring setting the value works around
        Scheduler.get().scheduleDeferred(() -> lstExpressionTypes.refresh(s -> s.setValue(type.name())));
    }

    @Override
    public void enableKind(final boolean enabled) {
        if (enabled) {
            lstKinds.enable();
        } else {
            lstKinds.disable();
        }
    }

    @Override
    public void enableExpressionType(final boolean enabled) {
        if (enabled) {
            lstExpressionTypes.enable();
        } else {
            lstExpressionTypes.disable();
        }
    }

    @EventHandler("addFormalParameter")
    @SinkNative(Event.ONCLICK)
    @SuppressWarnings("unused")
    public void onClickAddFormalParameter(final Event event) {
        presenter.addFormalParameter();
    }
}
