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

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CrudComponentTest<MODEL, FORM_MODEL> extends AbstractCrudComponentTest<MODEL, FORM_MODEL> {

    @Test
    public void usesEmbeddedDisplayerWhenShowEmbeddedFormsTrue() {
        initTest();

        when(helper.showEmbeddedForms()).thenReturn(true);

        final FormDisplayer displayer = crudComponent.getFormDisplayer();

        assertTrue(displayer.equals(embeddedFormDisplayer));

        runFormTest();
    }

    @Test
    public void useModalDisplayerWhenShowEmbeddedFormsFalse() {
        initTest();

        when(helper.showEmbeddedForms()).thenReturn(false);

        final FormDisplayer displayer = crudComponent.getFormDisplayer();

        assertTrue(displayer.equals(modalFormDisplayer));

        runFormTest();
    }

    @Test
    public void createInstanceCallsHelperCreateInstance() {
        initTest();
        crudComponent.createInstance();
        verify(helper).createInstance();
    }

    @Test
    public void editInstanceCallsHelperEditInstance() {
        initTest();
        crudComponent.editInstance(0);
        verify(helper).editInstance(0);
    }

    @Test
    public void deleteInstanceCallsHelperDeleteInstance() {
        initTest();
        crudComponent.deleteInstance(0);
        verify(helper).deleteInstance(0);
    }
}
