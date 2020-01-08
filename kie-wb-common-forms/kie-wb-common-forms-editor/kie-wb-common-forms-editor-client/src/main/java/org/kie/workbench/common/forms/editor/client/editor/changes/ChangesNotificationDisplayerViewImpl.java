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

package org.kie.workbench.common.forms.editor.client.editor.changes;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

@Templated
public class ChangesNotificationDisplayerViewImpl implements ChangesNotificationDisplayerView,
                                                             IsElement {

    private Presenter presenter;

    private BaseModal modal;

    private ModalFooterOKButton footer = new ModalFooterOKButton(() -> close());

    private TranslationService translationService;

    @Inject
    private Document document;

    @Inject
    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    @Inject
    public ChangesNotificationDisplayerViewImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    public void init() {
        modal = new BaseModal();
        modal.setTitle(translationService.getTranslation(FormEditorConstants.ChangesNotificationDisplayerTitle));
        modal.setBody(wrapperWidgetUtil.getWidget(this, this.getElement()));
        modal.add(footer);
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    public void close() {
        DOMUtil.removeAllChildren(this.getElement());

        modal.hide();
        presenter.close();
    }

    @Override
    public void show() {
        modal.show();
    }

    @PreDestroy
    public void destroy() {
        modal.clear();
        wrapperWidgetUtil.clear(this.getElement());
    }
}
