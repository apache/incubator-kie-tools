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


package org.kie.workbench.common.forms.crud.client.component.formDisplay.modal;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import org.gwtbootstrap3.client.shared.event.ModalHiddenEvent;
import org.gwtbootstrap3.client.shared.event.ModalHiddenHandler;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.crud.client.resources.i18n.CrudComponentConstants;

@Dependent
@Templated
public class ModalFormDisplayerViewImpl extends Composite implements ModalFormDisplayer.ModalFormDisplayerView {

    @DataField
    private SimplePanel content = new SimplePanel();

    private Button submit;

    private Button cancel;

    protected ModalFormDisplayer presenter;

    private Modal modal;

    private ModalBody modalBody;

    private TranslationService translationService;

    @Inject
    public ModalFormDisplayerViewImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    public void initialize() {

        modal = new Modal();

        modal.setHideOtherModals(false);
        modal.setClosable(true);
        modal.setFade(true);
        modal.setDataKeyboard(true);
        modal.setDataBackdrop(ModalBackdrop.FALSE);
        modal.setSize(ModalSize.LARGE);
        modal.setRemoveOnHide(true);

        modalBody = new ModalBody();

        modalBody.add(this);

        modal.add(modalBody);

        submit = new Button(translationService.getTranslation(CrudComponentConstants.ModalFormDisplayerViewImplAccept));

        submit.setType(ButtonType.PRIMARY);

        cancel = new Button(translationService.getTranslation(CrudComponentConstants.ModalFormDisplayerViewImplCancel));

        modal.add(new ModalFooter() {{
            add(submit);
            add(cancel);
        }});

        submit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.submitForm();
            }
        });

        cancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doCancel();
            }
        });

        modal.addHiddenHandler(new ModalHiddenHandler() {
            @Override
            public void onHidden(ModalHiddenEvent evt) {
                doCancel();
            }
        });
    }

    @Override
    public void setPresenter(ModalFormDisplayer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(String title,
                     IsFormView formView) {
        modal.setTitle(title);
        content.clear();
        content.add(formView);
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    protected void doCancel() {
        presenter.cancel();
    }
}
