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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;

/**
 * Defines a view to create FormModels on the FormEditor
 */
public interface FormModelCreationView<F extends FormModel> extends IsWidget {

    String getLabel();

    int getPriority();

    /**
     * Initializes the view
     */
    void init( Path projectPath );

    /**
     * Retrieves a FormModel instance
     */
    F getFormModel();

    boolean isValid();

    void reset();
}
