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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQueryTooltip.$;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_AddConstraints;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_ConstraintsTooltip;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Templated
@Dependent
public class DataTypeConstraintView implements DataTypeConstraint.View {

    @DataField("constraints-anchor-container")
    private final HTMLAnchorElement constraintsAnchorContainer;

    @DataField("constraints-label-container")
    private final HTMLDivElement constraintsLabelContainer;

    @DataField("constraints-anchor-text")
    private final HTMLElement constraintsAnchorText;

    @DataField("constraints-label-text")
    private final HTMLElement constraintsLabelText;

    @DataField("constraints-tooltip")
    private final HTMLElement constraintsTooltip;

    private final TranslationService translationService;

    private DataTypeConstraint presenter;

    @Inject
    public DataTypeConstraintView(final HTMLAnchorElement constraintsAnchorContainer,
                                  final HTMLDivElement constraintsLabelContainer,
                                  final @Named("span") HTMLElement constraintsAnchorText,
                                  final @Named("span") HTMLElement constraintsLabelText,
                                  final @Named("span") HTMLElement constraintsTooltip,
                                  final TranslationService translationService) {
        this.constraintsAnchorContainer = constraintsAnchorContainer;
        this.constraintsLabelContainer = constraintsLabelContainer;
        this.constraintsAnchorText = constraintsAnchorText;
        this.constraintsLabelText = constraintsLabelText;
        this.constraintsTooltip = constraintsTooltip;
        this.translationService = translationService;
    }

    @Override
    public void init(final DataTypeConstraint presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setup() {
        constraintsTooltip.setAttribute("title", translationService.format(DataTypeConstraintView_ConstraintsTooltip));
        setupTooltip(properties().getJavaScriptObject());
    }

    @EventHandler("constraints-anchor-container")
    public void onConstraintsClick(final ClickEvent e) {
        presenter.openModal();
    }

    @Override
    public void showAnchor() {
        hide(constraintsLabelContainer);
        show(constraintsAnchorContainer);
        show(constraintsTooltip);
    }

    @Override
    public void hideAnchor() {

        if (Objects.equals(constraintsLabelText.textContent, addConstraints())) {
            hide(constraintsLabelContainer);
        } else {
            show(constraintsLabelContainer);
        }

        hide(constraintsAnchorContainer);
        hide(constraintsTooltip);
    }

    @Override
    public void setText(final String text) {

        final String constraints = addConstraints();
        final String constraintText = isEmpty(text) ? constraints : text;

        constraintsAnchorText.textContent = constraintText;
        constraintsLabelText.textContent = constraintText;
    }

    @Override
    public void enable() {
        show(getElement());
    }

    @Override
    public void disable() {
        hide(getElement());
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

    private String addConstraints() {
        return translationService.format(DataTypeConstraintView_AddConstraints);
    }
}
