/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

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
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeConstraintView_ConstraintsTooltip;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Templated
@Dependent
public class DataTypeConstraintView implements DataTypeConstraint.View {

    @DataField("constraints-anchor")
    private final HTMLAnchorElement constraintsAnchor;

    @DataField("constraints-tooltip")
    private final HTMLElement constraintsTooltip;

    @DataField("constraints-label")
    private final HTMLElement constraintsLabel;

    @DataField("constraints-text")
    private final HTMLDivElement constraintsText;

    private final TranslationService translationService;

    private DataTypeConstraint presenter;

    @Inject
    public DataTypeConstraintView(final HTMLAnchorElement constraintsAnchor,
                                  final @Named("span") HTMLElement constraintsTooltip,
                                  final @Named("span") HTMLElement constraintsLabel,
                                  final HTMLDivElement constraintsText,
                                  final TranslationService translationService) {
        this.constraintsAnchor = constraintsAnchor;
        this.constraintsTooltip = constraintsTooltip;
        this.constraintsLabel = constraintsLabel;
        this.constraintsText = constraintsText;
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

    @EventHandler("constraints-anchor")
    public void onConstraintsClick(final ClickEvent e) {
        presenter.openModal();
    }

    @Override
    public void showAnchor() {
        show(constraintsAnchor);
        show(constraintsTooltip);
    }

    @Override
    public void showTextLabel() {
        show(constraintsLabel);
    }

    @Override
    public void hideAnchor() {
        hide(constraintsAnchor);
        hide(constraintsTooltip);
    }

    @Override
    public void hideTextLabel() {
        hide(constraintsLabel);
    }

    @Override
    public void showText() {
        show(constraintsText);
    }

    @Override
    public void hideText() {
        hide(constraintsText);
    }

    @Override
    public void setText(final String text) {

        final boolean isValueBlank = isEmpty(text);
        final String noneCSSClass = "none";

        if (isValueBlank) {
            constraintsText.textContent = "NONE";
            constraintsText.classList.add(noneCSSClass);
        } else {
            constraintsText.classList.remove(noneCSSClass);
            constraintsText.textContent = text;
        }
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
