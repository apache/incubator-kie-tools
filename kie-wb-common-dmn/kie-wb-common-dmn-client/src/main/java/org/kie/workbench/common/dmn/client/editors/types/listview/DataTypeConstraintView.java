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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQueryTooltip.$;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_ConstraintsTooltip;

@Dependent
@Templated
public class DataTypeConstraintView implements DataTypeConstraint.View {

    @DataField("constraint-toggle")
    private final HTMLInputElement constraintToggle;

    @DataField("constraint-value")
    private final HTMLInputElement constraintValue;

    @DataField("constraints-tooltip")
    private final HTMLElement constraintsTooltip;

    private final TranslationService translationService;

    private DataTypeSelect presenter;

    @Inject
    public DataTypeConstraintView(final TranslationService translationService,
                                  final HTMLInputElement constraintToggle,
                                  final HTMLInputElement constraintValue,
                                  final @Named("span") HTMLElement constraintsTooltip) {
        this.translationService = translationService;
        this.constraintToggle = constraintToggle;
        this.constraintValue = constraintValue;
        this.constraintsTooltip = constraintsTooltip;
    }

    @Override
    public void init(final DataTypeSelect presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setup() {
        constraintsTooltip.setAttribute("title", translationService.format(DataTypeConstraintView_ConstraintsTooltip));
        setupTooltip(properties().getJavaScriptObject());
    }

    @EventHandler("constraint-toggle")
    public void onConstraintToggleChange(final ChangeEvent e) {
        if (constraintToggle.checked) {
            enableConstraint();
            constraintValue.select();
        } else {
            disableConstraint();
            constraintValue.value = "";
        }
    }

    @Override
    public void enableConstraint() {
        constraintToggle.checked = true;
        show(constraintValue);
    }

    @Override
    public void disableConstraint() {
        constraintToggle.checked = false;
        hide(constraintValue);
    }

    @Override
    public String getConstraintValue() {
        return constraintValue.value;
    }

    @Override
    public void setConstraintValue(final String value) {
        constraintValue.value = value;
    }

    void setupTooltip(final JavaScriptObject javaScriptObject) {
        $(constraintsTooltip).tooltip(javaScriptObject);
    }

    JSONObject properties() {
        final JSONObject jsonObject = makeJsonObject();
        jsonObject.put("container", new JSONString("body"));
        return jsonObject;
    }

    JSONObject makeJsonObject() {
        return new JSONObject();
    }
}
