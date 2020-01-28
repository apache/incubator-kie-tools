/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.workitems.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Work Items Editor I18N constants
 */
public interface WorkItemsEditorConstants
        extends
        Messages {

    public static final WorkItemsEditorConstants INSTANCE = GWT.create( WorkItemsEditorConstants.class );

    String NewWorkItemDescription();

    String WorkItemDescription();

    String Title();

    String ChooseImportClass();

    String ChooseIcon();

    String BrowserTitle();

    String workItemResourceTypeDescription();

    String Definition();

    String Parameter();

    String Result();

    String DisplayName();

    String CustomEditor();

    String ParameterValues();

    String DefaultHandler();
}
