/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups.elemental2;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public abstract class Elemental2Modal<V extends Elemental2Modal.View> {

    public interface View<P extends Elemental2Modal<? extends View>> extends UberElemental<P>,
                                                                             IsElement {

        String getHeader();

        HTMLElement getBody();

        HTMLElement getFooter();
    }

    private final V view;

    private BaseModal modal;

    private boolean isShowing;

    public Elemental2Modal(final V view) {
        this.view = view;
    }

    public void setup() {
        superSetup();
    }

    public void superSetup() {

        view.init(this);

        modal = new CommonModalBuilder()
                .addHeader(getHeader())
                .addBody(getBody())
                .addFooter(getFooter())
                .build();

        modal.addHideHandler(i -> isShowing = false);
    }

    private String getHeader() {
        return view.getHeader();
    }

    private org.jboss.errai.common.client.dom.HTMLElement getFooter() {
        return TemplateUtil.asErraiElement(view.getFooter());
    }

    private org.jboss.errai.common.client.dom.HTMLElement getBody() {
        return TemplateUtil.asErraiElement(view.getBody());
    }

    public void show() {
        if (!isShowing) {
            modal.show();
            isShowing = true;
        }
    }

    protected void setWidth(final String width) {
        modal.setWidth(width);
    }

    public void hide() {
        modal.hide();
        isShowing = false;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public V getView() {
        return view;
    }

    public BaseModal getModal() {
        return modal;
    }
}
