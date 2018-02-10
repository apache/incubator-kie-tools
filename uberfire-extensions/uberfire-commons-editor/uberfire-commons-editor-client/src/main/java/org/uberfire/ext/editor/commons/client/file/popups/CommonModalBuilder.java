/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups;

import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class CommonModalBuilder {

    private final BaseModal modal;

    public CommonModalBuilder() {
        modal = new BaseModal();
    }

    public CommonModalBuilder addHeader(String title) {
        modal.setTitle(title);
        return this;
    }

    public CommonModalBuilder addBody(HTMLElement element) {
        modal.add(buildPanel(element,
                             new ModalBody()));
        return this;
    }

    public CommonModalBuilder addBody(final elemental2.dom.HTMLElement htmlElement) {

        final FlowPanel flowPanel = buildPanel(htmlElement, makeModalBody());
        getModal().add(flowPanel);
        return this;
    }

    public CommonModalBuilder addFooter(ModalFooter footer) {
        modal.add(footer);
        return this;
    }

    public CommonModalBuilder addFooter(HTMLElement element) {
        modal.add(buildPanel(element,
                             new ModalFooter()));
        return this;
    }

    public CommonModalBuilder addFooter(final elemental2.dom.HTMLElement htmlElement) {

        final FlowPanel flowPanel = buildPanel(htmlElement, makeModalFooter());
        getModal().add(flowPanel);
        return this;
    }

    public BaseModal build() {
        return getModal();
    }

    BaseModal getModal() {
        return modal;
    }

    ModalBody makeModalBody() {
        return new ModalBody();
    }

    ModalFooter makeModalFooter() {
        return new ModalFooter();
    }

    FlowPanel buildPanel(final elemental2.dom.HTMLElement element,
                         final FlowPanel panel) {

        final HTMLElement htmlElement = TemplateUtil.asErraiElement(element);
        panel.add(build(htmlElement));
        return panel;
    }

    private FlowPanel buildPanel(final HTMLElement element,
                                 final FlowPanel panel) {

        final HTMLElement htmlElement = TemplateUtil.asErraiElement(element);
        panel.add(build(htmlElement));
        return panel;
    }

    private Widget build(HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }
}
