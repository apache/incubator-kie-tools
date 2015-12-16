/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.workitems.client.editor;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import org.kie.workbench.common.widgets.metadata.client.KieEditorView;

public interface WorkItemsEditorView extends KieEditorView,
                                             RequiresResize,
                                             IsWidget {

    void setContent( final String content,
                     final List<String> workItemImages );

    String getContent();

    void setReadOnly( final boolean readOnly );

}
