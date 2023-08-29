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


package org.kie.workbench.common.forms.processing.engine.handling;

import java.util.Collection;

import com.google.gwt.user.client.ui.IsWidget;

public interface FormField {

    String getFieldName();

    String getFieldBinding();

    boolean isValidateOnChange();

    boolean isBindable();

    void setVisible(boolean visible);

    void setReadOnly(boolean readOnly);

    boolean isRequired();

    void clearError();

    void showError(String error);

    void showWarning(String warning);

    FieldContainer getContainer();

    IsWidget getWidget();

    default boolean isContentValid() {
        return true;
    }

    Collection<FieldChangeListener> getChangeListeners();

    Collection<CustomFieldValidator> getCustomValidators();
}
