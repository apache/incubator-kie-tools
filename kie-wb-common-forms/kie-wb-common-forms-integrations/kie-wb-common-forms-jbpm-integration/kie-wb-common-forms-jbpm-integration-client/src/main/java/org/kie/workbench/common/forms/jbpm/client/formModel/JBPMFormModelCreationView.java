/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.jbpm.client.formModel;

import java.util.List;

import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.uberfire.client.mvp.UberElement;

public interface JBPMFormModelCreationView extends UberElement<JBPMFormModelCreationView.Presenter> {

    interface Presenter {

        void setModel(JBPMFormModel model);
    }

    void setProcessModels(List<JBPMProcessModel> processModels);

    void reset();

    void clearValidationErrors();

    void setErrorMessage(String errorMessage);
}
