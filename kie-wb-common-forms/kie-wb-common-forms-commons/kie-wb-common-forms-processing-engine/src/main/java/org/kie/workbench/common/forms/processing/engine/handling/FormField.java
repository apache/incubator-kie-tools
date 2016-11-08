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

package org.kie.workbench.common.forms.processing.engine.handling;

import com.google.gwt.user.client.ui.IsWidget;

public interface FormField {
    public static final String FORM_GROUP_SUFFIX = "_form_group";
    public static final String HELP_BLOCK_SUFFIX = "_help_block";

    String getFieldName();

    String getFieldBinding();

    boolean isValidateOnChange();

    boolean isBindable();

    void setVisible( boolean visible );

    void setReadOnly( boolean readOnly );

    void clearError();

    void setError( String error );

    IsWidget getWidget();
}
