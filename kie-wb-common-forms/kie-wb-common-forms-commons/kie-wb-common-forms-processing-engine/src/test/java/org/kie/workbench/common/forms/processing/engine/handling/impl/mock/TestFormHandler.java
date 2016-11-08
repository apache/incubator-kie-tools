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

package org.kie.workbench.common.forms.processing.engine.handling.impl.mock;

import org.jboss.errai.databinding.client.api.DataBinder;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandlerManager;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FormHandlerImpl;

public class TestFormHandler extends FormHandlerImpl {

    protected DataBinder dataBinder;

    public TestFormHandler( FormValidator validator,
                            FieldChangeHandlerManager fieldChangeManager, DataBinder binder ) {
        super( validator, fieldChangeManager );
        this.dataBinder = binder;
    }


    @Override
    protected DataBinder getBinderForModel( Object model ) {
        return dataBinder;
    }
}
