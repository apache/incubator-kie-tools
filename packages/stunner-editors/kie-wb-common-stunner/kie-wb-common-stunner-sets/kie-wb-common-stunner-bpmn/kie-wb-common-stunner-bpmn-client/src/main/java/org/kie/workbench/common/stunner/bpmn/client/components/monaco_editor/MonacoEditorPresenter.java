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


package org.kie.workbench.common.stunner.bpmn.client.components.monaco_editor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MonacoEditorPresenter {

    private final MonacoEditorView view;
    private final Set<MonacoEditorLanguage> languages;
    private boolean readyOnly;
    private int widthPx;
    private int heightPx;
    private boolean requestRefresh = false;

    OnChangeCallback onChangeCallback;
    String current;

    public interface OnChangeCallback {

        void onChange();
    }

    @Inject
    public MonacoEditorPresenter(MonacoEditorView view) {
        this.readyOnly = false;
        this.view = view;
        this.languages = new HashSet<>();
        this.widthPx = 500;
        this.heightPx = 300;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public MonacoEditorPresenter setOnChangeCallback(OnChangeCallback onChangeCallback) {
        this.onChangeCallback = onChangeCallback;
        return this;
    }

    public MonacoEditorPresenter setWidthPx(int widthPx) {
        this.widthPx = widthPx;
        return this;
    }

    public MonacoEditorPresenter setHeightPx(int heightPx) {
        this.heightPx = heightPx;
        return this;
    }

    public MonacoEditorPresenter setReadOnly(boolean readOnly) {
        this.readyOnly = readOnly;
        return this;
    }

    public MonacoEditorPresenter addLanguage(MonacoEditorLanguage module) {
        languages.add(module);
        view.addLanguage(module.getTitle(), module.getId());
        return this;
    }

    // Reloads the editor if language has changed
    public void setValue(String languageId, String value) {
        if (requestRefresh || (null != current && !current.equals(languageId))) {
            view.dispose();
            current = null;
        }
        requestRefresh = false;

        if (null == current) {
            getLanguageById(languageId).ifPresent(module -> load(module, value));
        } else {
            view.setValue(value);
        }
    }

    private void load(MonacoEditorLanguage language, String value) {
        current = language.getId();
        view.loadingStarts();
        view.setLanguage(language.getId());
        view.load(language.buildOptions()
                          .setLanguage(language.getLanguageCode())
                          .setWidthPx(widthPx)
                          .setHeightPx(heightPx)
                          .setAutomaticLayout(false) // Otherwise running into issues with nested forms & bootstrap accordion
                          .setValue(value)
                          .setReadOnly(readyOnly),
                  () -> {
                      view.loadingEnds();
                      view.setLanguageReadOnly(readyOnly);
                      view.attachListenerToPanelTitle();
                  });
    }
    
    public String getValue() {
        return view.getValue();
    }

    public String getLanguageId() {
        return view.getLanguage();
    }

    public MonacoEditorView getView() {
        return view;
    }

    @PreDestroy
    public void destroy() {
        current = null;
        onChangeCallback = null;
        languages.clear();
    }

    void onValueChanged() {
        onChangeCallback.onChange();
    }

    void requestRefresh() {
        requestRefresh = true;
    }

    void onLanguageChanged(String languageId) {
        onChangeCallback.onChange();
        setValue(languageId, null != getValue() ? getValue() : "");
    }

    private Optional<MonacoEditorLanguage> getLanguageById(String id) {
        return languages.stream()
                .filter(m -> m.getId().equals(id))
                .findAny();
    }
}
