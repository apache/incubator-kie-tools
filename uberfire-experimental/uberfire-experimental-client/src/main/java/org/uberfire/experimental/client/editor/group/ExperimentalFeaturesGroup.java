/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.experimental.client.editor.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.experimental.client.editor.group.feature.ExperimentalFeatureEditor;
import org.uberfire.experimental.client.resources.i18n.UberfireExperimentalConstants;
import org.uberfire.experimental.service.editor.EditableExperimentalFeature;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class ExperimentalFeaturesGroup implements IsElement,
                                                  ExperimentalFeaturesGroupView.Presenter,
                                                  Comparable<ExperimentalFeaturesGroup> {

    private final ExperimentalFeaturesGroupView view;
    private final TranslationService translationService;
    private final ManagedInstance<ExperimentalFeatureEditor> editorInstance;

    protected String labelKey;
    protected List<ExperimentalFeatureEditor> editors = new ArrayList<>();
    protected ParameterizedCommand<EditableExperimentalFeature> callback;

    private boolean expanded = false;

    private boolean enableAllRunning = false;

    @Inject
    public ExperimentalFeaturesGroup(final ExperimentalFeaturesGroupView view, final TranslationService translationService, final ManagedInstance<ExperimentalFeatureEditor> editorInstance) {
        this.view = view;
        this.translationService = translationService;
        this.editorInstance = editorInstance;

        view.init(this);
    }

    public void init(final String labelKey, final Collection<ExperimentalFeature> features, final ParameterizedCommand<EditableExperimentalFeature> callback) {
        PortablePreconditions.checkNotNull("labelKey", labelKey);
        PortablePreconditions.checkNotNull("features", features);
        PortablePreconditions.checkNotNull("callback", callback);

        clear();

        this.labelKey = labelKey;

        this.callback = feature -> {
            callback.execute(feature);
            setEnableAllLabel();
        };

        view.setLabel(getLabel());

        features.stream()
                .map(this::getEditor)
                .collect(Collectors.toCollection(TreeSet::new))
                .forEach(view::render);

        setEnableAllLabel();
    }

    private void setEnableAllLabel() {
        if (!enableAllRunning) {
            if (isSelectAll()) {
                view.setEnableAllLabel(translationService.getTranslation(UberfireExperimentalConstants.ExperimentalFeaturesGroupEnableAll));
            } else {
                view.setEnableAllLabel(translationService.getTranslation(UberfireExperimentalConstants.ExperimentalFeaturesGroupDisableAll));
            }
        }
    }

    private boolean isSelectAll() {
        long enabled = editors.stream().filter(ExperimentalFeatureEditor::isEnabled).count();

        return enabled != editors.size();
    }

    private ExperimentalFeatureEditor getEditor(ExperimentalFeature experimentalFeature) {
        ExperimentalFeatureEditor editor = editorInstance.get();

        editor.render(new EditableExperimentalFeature(experimentalFeature), callback);

        editors.add(editor);

        return editor;
    }

    @Override
    public void doEnableAll() {
        final boolean select = isSelectAll();

        enableAllRunning = true;

        editors.stream()
                .filter(editor -> editor.isEnabled() != select)
                .forEach(editor -> editor.enable());

        enableAllRunning = false;

        setEnableAllLabel();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public int compareTo(ExperimentalFeaturesGroup other) {
        if (UberfireExperimentalConstants.experimentalFeaturesGeneralGroupKey.equals(labelKey) ||
                UberfireExperimentalConstants.experimentalFeaturesGlobalGroupKey.equals(other.labelKey)) {
            return Integer.MIN_VALUE;
        }

        if (UberfireExperimentalConstants.experimentalFeaturesGlobalGroupKey.equals(labelKey) ||
                UberfireExperimentalConstants.experimentalFeaturesGeneralGroupKey.equals(other.labelKey)) {
            return Integer.MAX_VALUE;
        }

        return getLabel().compareTo(other.getLabel());
    }

    public void expand() {
        if (!expanded) {
            expanded = true;
            view.expand();
        }
    }

    public void collapse() {
        if (expanded) {
            expanded = false;
            view.collapse();
        }
    }

    @Override
    public boolean isExpanded() {
        return expanded;
    }

    @Override
    public void notifyExpand() {
        expanded = !expanded;
        view.arrangeCaret();
    }

    @Override
    public String getLabel() {
        String translation = translationService.getTranslation(labelKey);

        if (translation == null) {
            translation = labelKey;
        }

        return translation;
    }

    @PreDestroy
    public void clear() {
        editors.clear();
        view.clear();
        editorInstance.destroyAll();
        enableAllRunning = false;
    }
}
