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

package org.kie.workbench.common.forms.crud.client.component;

import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer.FormDisplayerCallback;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

import static org.kie.workbench.common.forms.crud.client.resources.i18n.CrudComponentConstants.CrudComponentViewImplNewInstanceTitle;

@Dependent
public class CrudComponent<MODEL, FORM_MODEL> implements IsWidget {

    public interface CrudComponentView<MODEL, FORM_MODEL> extends IsWidget {

        void setPresenter(CrudComponent<MODEL, FORM_MODEL> presenter);

        int getCurrentPage();

        void addDisplayer(FormDisplayer displayer);

        void removeDisplayer(FormDisplayer displayer);

        void initTableView(List<ColumnMeta<MODEL>> dataColumns,
                           int pageSize);

        void showCreateButton();

        void setDataProvider(final AsyncDataProvider<MODEL> dataProvider);

        void showDeleteButtons();

        void showEditButtons();
    }

    private final CrudComponentView<MODEL, FORM_MODEL> view;

    private final EmbeddedFormDisplayer embeddedFormDisplayer;

    private final ModalFormDisplayer modalFormDisplayer;

    protected boolean embedded = true;

    protected CrudActionsHelper<MODEL> helper;

    private final TranslationService translationService;

    @Inject
    public CrudComponent(final CrudComponentView<MODEL, FORM_MODEL> view,
                         final EmbeddedFormDisplayer embeddedFormDisplayer,
                         final ModalFormDisplayer modalFormDisplayer,
                         final TranslationService translationService) {
        this.view = view;
        this.embeddedFormDisplayer = embeddedFormDisplayer;
        this.modalFormDisplayer = modalFormDisplayer;
        this.translationService = translationService;
        view.setPresenter(this);
    }

    public void init(final CrudActionsHelper<MODEL> helper) {
        this.helper = helper;
        view.initTableView(helper.getGridColumns(),
                           helper.getPageSize());
        if (helper.isAllowCreate()) {
            view.showCreateButton();
        }
        if (helper.isAllowEdit()) {
            view.showEditButtons();
        }
        if (helper.isAllowDelete()) {
            view.showDeleteButtons();
        }

        view.setDataProvider(helper.getDataProvider());
        refresh();
    }

    public FormDisplayer getFormDisplayer() {
        if (helper.showEmbeddedForms()) {
            return embeddedFormDisplayer;
        }
        return modalFormDisplayer;
    }

    public void createInstance() {
        helper.createInstance();
    }

    public void editInstance(int index) {
        helper.editInstance(index);
    }

    public void deleteInstance(final int index) {
        helper.deleteInstance(index);
    }

    public int getCurrentPage() {
        return view.getCurrentPage();
    }

    public void refresh() {
        final HasData<MODEL> next = helper.getDataProvider().getDataDisplays().iterator().next();
        next.setVisibleRangeAndClearData(next.getVisibleRange(),
                                         true);
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(final boolean embedded) {
        this.embedded = embedded;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void displayForm(final String title,
                            final IsFormView<FORM_MODEL> formView,
                            final FormDisplayer.FormDisplayerCallback callback) {
        final FormDisplayer displayer = getFormDisplayer();

        if (displayer.isEmbeddable()) {
            view.addDisplayer(displayer);
        }

        displayer.display(title,
                          formView,
                          new FormDisplayerCallback() {

                              @Override
                              public void onCancel() {
                                  restoreTable();
                                  callback.onCancel();
                              }

                              @Override
                              public void onAccept() {
                                  restoreTable();
                                  callback.onAccept();
                              }
                          });
    }

    public void displayForm(IsFormView<FORM_MODEL> formView,
                            FormDisplayerCallback callback) {
        displayForm(translationService.getTranslation(CrudComponentViewImplNewInstanceTitle),
                    formView,
                    callback);
    }

    public void restoreTable() {
        final FormDisplayer displayer = getFormDisplayer();
        if (displayer.isEmbeddable()) {
            view.removeDisplayer(displayer);
        }
    }
}
