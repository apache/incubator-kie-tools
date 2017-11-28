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
package org.dashbuilder.displayer.client.widgets;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.dashbuilder.displayer.DisplayerAttributeDef;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.widgets.sourcecode.HasHtmlTemplate;
import org.dashbuilder.displayer.client.widgets.sourcecode.HasJsTemplate;
import org.dashbuilder.displayer.client.widgets.sourcecode.SourceCodeEditor;
import org.dashbuilder.displayer.client.widgets.sourcecode.SourceCodeType;
import org.uberfire.client.mvp.UberView;

import static org.dashbuilder.displayer.client.widgets.sourcecode.SourceCodeType.*;

/**
 * UI component for the edition of a displayer component supporting HTML templates
 */
@Dependent
public class DisplayerHtmlEditor implements IsWidget {

    public interface View extends UberView<DisplayerHtmlEditor> {

        void showDisplayer(IsWidget displayer);

        void clearSourceCodeItems();

        void addSourceCodeItem(String name);

        void editSourceCodeItem(String name, IsWidget editor);
    }

    public class SourceCodeItem {
        String name;
        SourceCodeType type;
        DisplayerAttributeDef attributeDef;

        public SourceCodeItem(String name, SourceCodeType type, DisplayerAttributeDef attributeDef) {
            this.name = name;
            this.type = type;
            this.attributeDef = attributeDef;
        }
    }

    Displayer displayer;
    View view;
    SourceCodeEditor sourceCodeEditor;
    Set<SourceCodeItem> sourceCodeItems = new HashSet<>();
    SourceCodeItem selectedSourceCodeItem = null;
    boolean showingDisplayer = true;

    @Inject
    public DisplayerHtmlEditor(View view, SourceCodeEditor sourceCodeEditor) {
        this.view = view;
        this.sourceCodeEditor = sourceCodeEditor;
        view.init(this);

        sourceCodeItems.add(new SourceCodeItem("html", HTML, DisplayerAttributeDef.HTML_TEMPLATE));
        sourceCodeItems.add(new SourceCodeItem("javascript", JAVASCRIPT, DisplayerAttributeDef.JS_TEMPLATE));
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public Displayer getDisplayer() {
        return displayer;
    }

    public void setDisplayer(Displayer displayer) {
        this.selectedSourceCodeItem = null;
        this.displayer = displayer;

        initSourceCodeItems();
        showDisplayer();
    }

    public boolean showDisplayer() {
        if (sourceCodeEditor.hasErrors()) {
            sourceCodeEditor.focus();
            return false;
        }
        view.showDisplayer(displayer);
        showingDisplayer = true;
        displayer.redraw();
        return true;
    }

    public void initSourceCodeItems() {
        view.clearSourceCodeItems();

        Set<DisplayerAttributeDef> attrs = displayer.getDisplayerConstraints().getSupportedAttributes();
        for (SourceCodeItem sourceCodeItem : sourceCodeItems) {
            if (attrs.contains(sourceCodeItem.attributeDef)) {
                view.addSourceCodeItem(sourceCodeItem.name);
            }
        }
    }

    public SourceCodeItem getSourceCodeItem(String name) {
        for (SourceCodeItem sourceCodeItem : sourceCodeItems) {
            if (sourceCodeItem.name.equals(name)) {
                return sourceCodeItem;
            }
        }
        return null;
    }

    public boolean onSourceCodeItemSelected(String name) {
        selectedSourceCodeItem = getSourceCodeItem(name);
        String code = displayer.getDisplayerSettings().getDisplayerSetting(selectedSourceCodeItem.attributeDef);
        Map<String,String> varMap = null;
        if (selectedSourceCodeItem.type == SourceCodeType.HTML && displayer instanceof HasHtmlTemplate) {
            varMap = ((HasHtmlTemplate) displayer).getHtmlVariableMap();
        }
        if (selectedSourceCodeItem.type == SourceCodeType.JAVASCRIPT && displayer instanceof HasJsTemplate) {
            varMap = ((HasJsTemplate) displayer).getJsVariableMap();
        }

        sourceCodeEditor.init(selectedSourceCodeItem.type, code, varMap, this::onSourceCodeChanged);
        sourceCodeEditor.focus();

        showingDisplayer = false;
        view.editSourceCodeItem(name, sourceCodeEditor);
        return true;
    }

    public void onSourceCodeChanged() {
        if (!sourceCodeEditor.hasErrors()) {
            String newCode = sourceCodeEditor.getCode();
            displayer.getDisplayerSettings().setDisplayerSetting(selectedSourceCodeItem.attributeDef, newCode);
        }
    }
}
