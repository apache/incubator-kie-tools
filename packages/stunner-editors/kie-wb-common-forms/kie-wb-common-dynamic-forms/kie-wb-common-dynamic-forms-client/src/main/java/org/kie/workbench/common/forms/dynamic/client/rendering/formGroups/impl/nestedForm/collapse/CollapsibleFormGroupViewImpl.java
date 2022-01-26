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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Event;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import jsinterop.base.Js;
import org.gwtproject.dom.client.Document;
import org.gwtproject.user.client.ui.SimplePanel;
import org.gwtproject.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
@Dependent
public class CollapsibleFormGroupViewImpl implements IsElement,
                                                     CollapsibleFormGroupView {

    private static final String PART_ANCHOR_TEXT = "Anchor Text";

    @Inject
    private FieldRequired fieldRequired;

    @Inject
    private FieldHelp fieldHelp;

    @Inject
    @DataField
    private HTMLAnchorElement anchor;

    @Inject
    @DataField
    @Named("span")
    private HTMLElement anchorText;

    @Inject
    @DataField
    private HTMLDivElement panel;

    @DataField
    private SimplePanel container = new SimplePanel();

    @Inject
    @DataField
    private HTMLDivElement formGroup;

    @Inject
    @DataField
    private HTMLDivElement helpBlock;

    @Inject
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private Presenter presenter;

    private Map<String, Widget> partsWidget = new HashMap<>();

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(Widget widget,
                       FieldDefinition field) {
        String id = Document.get().createUniqueId();
        anchor.setAttribute("data-target", "#" + id);
        panel.id = (id);

        formGroup.hidden = (true);
        anchorText.textContent = (field.getLabel());

        if (field.isRequired()) {
            anchor.appendChild(fieldRequired.getElement());
        }

        if (field.getHelpMessage() != null && !field.getHelpMessage().trim().isEmpty()) {
            fieldHelp.showHelpMessage(field.getHelpMessage());
            anchor.appendChild(fieldHelp.getElement());
        }

        container.clear();

        container.add(widget);

        partsWidget.put(PART_ANCHOR_TEXT, wrapperWidgetUtil.getWidget(this, anchorText));
    }

    @Override
    public void click() {
        Js.<HTMLInputElement>uncheckedCast(anchor).click();
        //anchor.click();
    }

    @Override
    public void expand() {
        DOMUtil.removeCSSClass(anchor, "collapsed");
        anchor.setAttribute("aria-expanded", "true");
        DOMUtil.addCSSClass(panel, "in");
        panel.setAttribute("aria-expanded", "true");
    }

    @Override
    public void collapse() {
        DOMUtil.addCSSClass(anchor, "collapsed");
        anchor.setAttribute("aria-expanded", "false");
        DOMUtil.removeCSSClass(panel, "in");
        panel.setAttribute("aria-expanded", "false");
    }

    @EventHandler("anchor")
    public void onClick(@ForEvent("click") Event clickEvent) {
        presenter.notifyClick();
    }

    @Override
    public Map<String, Widget> getViewPartsWidgets() {
        return partsWidget;
    }

    @PreDestroy
    public void destroy() {
        container.clear();
        wrapperWidgetUtil.clear(this);
    }
}
