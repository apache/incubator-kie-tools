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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup.editor;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

public interface ImportsEditorWidgetView<T> {

    void init(final Presenter presenter);

    List<T> getImports();

    void setImports(final List<T> imports);

    int getImportsCount();

    ImportListItemWidgetView<T> getImportWidget(final int index);

    void setDisplayStyle(final Style.Display displayStyle);

    void setTitle(final String title);

    interface Presenter<T> {

        List<T> getData();

        void setData(final List<T> imports);

        Widget getWidget();

        T createImport();

        void addImport();

        void removeImport(final T imp);

        boolean isDuplicateImport(final T imp);
    }
}
