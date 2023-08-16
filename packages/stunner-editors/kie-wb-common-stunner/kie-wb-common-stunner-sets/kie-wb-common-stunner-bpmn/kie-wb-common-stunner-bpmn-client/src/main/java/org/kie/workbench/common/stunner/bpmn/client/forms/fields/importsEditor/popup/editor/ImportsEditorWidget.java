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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;

import static com.google.gwt.dom.client.Style.Display.NONE;
import static com.google.gwt.dom.client.Style.Display.TABLE;

public abstract class ImportsEditorWidget<T> implements ImportsEditorWidgetView.Presenter<T> {

    @Inject
    protected ImportsEditorWidgetView<T> view;

    @PostConstruct
    public void init() {
        view.init(this);
        view.setDisplayStyle(NONE);
    }

    @Override
    public abstract T createImport();

    @Override
    public void addImport() {
        List<T> imports = view.getImports();
        if (imports.isEmpty()) {
            view.setDisplayStyle(TABLE);
        }

        imports.add(createImport());

        ImportListItemWidgetView widget = view.getImportWidget(view.getImportsCount() - 1);
        widget.setParentWidget(this);
    }

    @Override
    public void removeImport(final T imp) {
        List<T> imports = view.getImports();
        imports.remove(imp);
        if (view.getImports().isEmpty()) {
            view.setDisplayStyle(NONE);
        }
    }

    @Override
    public boolean isDuplicateImport(final T imp) {
        List<T> imports = view.getImports();
        if (imports != null && !imports.isEmpty()) {
            for (T compareImp : imports) {
                if (compareImp.equals(imp)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Widget getWidget() {
        return (Widget) view;
    }

    @Override
    public List<T> getData() {
        List<T> imports = new ArrayList<>();
        if (!view.getImports().isEmpty()) {
            imports.addAll(view.getImports());
        }
        return imports;
    }

    @Override
    public void setData(final List<T> imports) {
        if (imports == null || imports.isEmpty()) {
            view.setDisplayStyle(NONE);
        } else {
            view.setDisplayStyle(TABLE);
        }

        if (imports != null) {
            view.setImports(imports);
            for (int i = 0; i < imports.size(); i++) {
                view.getImportWidget(i).setParentWidget(this);
            }
        }
    }
}
