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
        this.getModal().setTitle(title);
        return this;
    }

    public CommonModalBuilder addBody(HTMLElement element) {
        this.getModal().add(buildPanel(element,
                             makeModalBody()));
        return this;
    }

    public CommonModalBuilder addBody(elemental2.dom.HTMLElement element) {
        this.getModal().add(buildPanel(element,
                             makeModalBody()));
        return this;
    }

    public CommonModalBuilder addFooter(ModalFooter footer) {
        this.getModal().add(footer);
        return this;
    }

    public CommonModalBuilder addFooter(HTMLElement element) {
        this.getModal().add(buildPanel(element,
                             makeModalFooter()));
        return this;
    }

    public CommonModalBuilder addFooter(final elemental2.dom.HTMLElement htmlElement) {

        final FlowPanel flowPanel = buildPanel(htmlElement,
                                               makeModalFooter());
        getModal().add(flowPanel);
        return this;
    }

    public BaseModal build() {
        return getModal();
    }

    protected BaseModal getModal() {
        return modal;
    }

    ModalBody makeModalBody() {
        return new ModalBody();
    }

    ModalFooter makeModalFooter() {
        return new ModalFooter();
    }

    protected FlowPanel buildPanel(final HTMLElement element,
                                 final FlowPanel panel) {

        final HTMLElement htmlElement = TemplateUtil.asErraiElement(element);
        panel.add(build(htmlElement));
        return panel;
    }

    protected FlowPanel buildPanel(elemental2.dom.HTMLElement element,
                                 FlowPanel panel) {
        panel.add(build(element));
        return panel;
    }

    private Widget build(HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }

    private Widget build(elemental2.dom.HTMLElement element) {
        return ElementWrapperWidget.getWidget(element);
    }
}
