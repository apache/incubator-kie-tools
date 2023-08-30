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


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Templated
public class CollapsibleFormGroupViewImpl implements IsElement,
                                                     CollapsibleFormGroupView {

    private static final String PART_ANCHOR_TEXT = "Anchor Text";

    @Inject
    private FieldRequired fieldRequired;

    @Inject
    private FieldHelp fieldHelp;

    @Inject
    @DataField
    private Anchor anchor;

    @Inject
    @DataField
    private Span anchorText;

    @Inject
    @DataField
    private Div panel;

    @DataField
    private SimplePanel container = new SimplePanel();

    @Inject
    @DataField
    private Div formGroup;

    @Inject
    @DataField
    private Div helpBlock;

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
        panel.setId(id);

        formGroup.setHidden(true);
        anchorText.setTextContent(field.getLabel());

        if (field.getRequired()) {
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
        anchor.click();
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
    public void onClick(ClickEvent clickEvent) {
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
