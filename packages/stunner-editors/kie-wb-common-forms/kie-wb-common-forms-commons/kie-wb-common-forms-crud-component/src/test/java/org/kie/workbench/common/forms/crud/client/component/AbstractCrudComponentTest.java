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

import com.google.gwtmockito.GwtMock;
import junit.framework.TestCase;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer.FormDisplayerCallback;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.mock.CrudComponentMock;
import org.kie.workbench.common.forms.crud.client.resources.i18n.CrudComponentConstants;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractCrudComponentTest<MODEL, FORM_MODEL> extends TestCase {

    @Mock
    protected CrudComponent.CrudComponentView<MODEL, FORM_MODEL> view;

    @Mock
    protected TranslationService translationService;

    @GwtMock
    protected EmbeddedFormDisplayer embeddedFormDisplayer;

    @GwtMock
    protected ModalFormDisplayer modalFormDisplayer;

    @Mock
    private IsFormView<FORM_MODEL> formView;

    @Mock
    private FormDisplayerCallback callback;

    @SuppressWarnings("unchecked")
    protected CrudActionsHelper<MODEL> helper = Mockito.mock(CrudActionsHelper.class);

    protected CrudComponentMock<MODEL, FORM_MODEL> crudComponent;

    protected static final String NEW_INSTANCE_TITLE = "New Instance Title";
    protected static final String EDIT_INSTANCE_TITLE = "Edit Instance Title";

    @Before
    public void init() {
        when(translationService.getTranslation(CrudComponentConstants.CrudComponentViewImplNewInstanceTitle)).thenReturn(NEW_INSTANCE_TITLE);
        when(translationService.getTranslation(CrudComponentConstants.CrudComponentViewImplEditInstanceTitle)).thenReturn(EDIT_INSTANCE_TITLE);
        when(embeddedFormDisplayer.isEmbeddable()).thenReturn(true);
        crudComponent = new CrudComponentMock<>(view,
                                                embeddedFormDisplayer,
                                                modalFormDisplayer,
                                                translationService);
    }

    protected void initTest() {
        verify(view).setPresenter(crudComponent);

        crudComponent.init(getActionsHelper());
        verify(view).initTableView(helper.getGridColumns(),
                                   helper.getPageSize());

        crudComponent.getCurrentPage();
        verify(view).getCurrentPage();
    }

    protected void runFormTest() {
        crudComponent.displayForm(formView,
                                  callback);

        if (getActionsHelper().showEmbeddedForms()) {
            verify(view).addDisplayer(embeddedFormDisplayer);
            verify(embeddedFormDisplayer).display(eq(NEW_INSTANCE_TITLE),
                                                  eq(getFormView()),
                                                  any(FormDisplayer.FormDisplayerCallback.class));
        } else {
            verify(modalFormDisplayer).display(eq(NEW_INSTANCE_TITLE),
                                               eq(getFormView()),
                                               any(FormDisplayer.FormDisplayerCallback.class));
        }

        crudComponent.restoreTable();

        if (getActionsHelper().showEmbeddedForms()) {
            verify(view).removeDisplayer(embeddedFormDisplayer);
        }
    }

    protected CrudActionsHelper<MODEL> getActionsHelper() {
        return helper;
    }

    protected IsFormView<FORM_MODEL> getFormView() {
        return formView;
    }
}
