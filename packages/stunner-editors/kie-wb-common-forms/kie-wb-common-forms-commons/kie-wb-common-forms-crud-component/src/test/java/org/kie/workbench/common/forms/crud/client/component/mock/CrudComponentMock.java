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


package org.kie.workbench.common.forms.crud.client.component.mock;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;

public class CrudComponentMock<MODEL, FORM_MODEL> extends CrudComponent<MODEL, FORM_MODEL> {

    public CrudComponentMock(final CrudComponentView<MODEL, FORM_MODEL> view,
                             final EmbeddedFormDisplayer embeddedFormDisplayer,
                             final ModalFormDisplayer modalFormDisplayer,
                             final TranslationService translationService) {
        super(view,
              embeddedFormDisplayer,
              modalFormDisplayer,
              translationService);
    }

    @Override
    public void refresh() {
    }
}
