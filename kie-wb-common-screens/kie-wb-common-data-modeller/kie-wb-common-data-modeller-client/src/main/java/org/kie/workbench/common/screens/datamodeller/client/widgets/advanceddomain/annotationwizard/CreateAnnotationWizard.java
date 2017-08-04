/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.annotationwizard;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.AnnotationValuePairDefinition;
import org.kie.workbench.common.services.datamodeller.core.ElementType;
import org.kie.workbench.common.services.datamodeller.core.impl.AnnotationImpl;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

@Dependent
public class CreateAnnotationWizard extends AbstractWizard {

    private List<WizardPage> pages = new ArrayList<>();

    private SearchAnnotationPage searchAnnotationPage;

    private SyncBeanManager iocManager;

    private Callback<Annotation> onCloseCallback;

    private KieProject project;

    private AnnotationDefinition annotationDefinition = null;

    private ElementType target = ElementType.FIELD;

    @Inject
    public CreateAnnotationWizard(SearchAnnotationPage searchAnnotationPage,
                                  SyncBeanManager iocManager) {
        this.searchAnnotationPage = searchAnnotationPage;
        this.iocManager = iocManager;
    }

    @PostConstruct
    protected void init() {
        pages.add(searchAnnotationPage);
        searchAnnotationPage.addSearchAnnotationHandler(new SearchAnnotationPageView.SearchAnnotationHandler() {
            @Override
            public void onSearchClassChanged() {
                doOnSearchClassChanged();
            }

            @Override
            public void onAnnotationDefinitionChange(AnnotationDefinition annotationDefinition) {
                updateValuePairPages(annotationDefinition);
            }
        });
    }

    public void init(KieProject project,
                     ElementType target) {
        this.project = project;
        this.target = target;
        searchAnnotationPage.init(project,
                                  target);
        clearCurrentValuePairEditorPages();
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget(int pageNumber) {
        return pages.get(pageNumber).asWidget();
    }

    @Override
    public String getTitle() {
        return Constants.INSTANCE.advanced_domain_wizard_title();
    }

    @Override
    public int getPreferredHeight() {
        return 350;
    }

    @Override
    public int getPreferredWidth() {
        return 700;
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        final int[] unCompletedPages = {this.pages.size()};

        for (WizardPage page : this.pages) {
            page.isComplete(result -> {
                if (Boolean.TRUE.equals(result)) {
                    unCompletedPages[0]--;
                    if (unCompletedPages[0] == 0) {
                        callback.callback(true);
                    }
                } else {
                    callback.callback(false);
                }
            });
        }
    }

    public void onCloseCallback(final Callback<Annotation> callback) {
        this.onCloseCallback = callback;
    }

    @Override
    public void complete() {
        doComplete();
        super.complete();
    }

    @Override
    public void close() {
        clearCurrentValuePairEditorPages();
        invokeOnCloseCallback(null);
        super.close();
    }

    private void doComplete() {
        Annotation annotation = null;
        if (annotationDefinition != null) {
            annotation = new AnnotationImpl(annotationDefinition);
            if (!annotationDefinition.isMarker()) {
                for (ValuePairEditorPage valuePairEditor : filterValuePairEditorPages()) {
                    if (valuePairEditor.getCurrentValue() != null) {
                        annotation.setValue(valuePairEditor.getValuePairDefinition().getName(),
                                            valuePairEditor.getCurrentValue());
                    }
                }
            }
        }
        clearCurrentValuePairEditorPages();
        invokeOnCloseCallback(annotation);
    }

    private void doOnSearchClassChanged() {
        if (annotationDefinition != null || pages.size() > 1) {
            annotationDefinition = null;
            clearCurrentValuePairEditorPages();
            super.start();
            Scheduler.get().scheduleDeferred((Command) () -> searchAnnotationPage.requestFocus());
        }
    }

    private void updateValuePairPages(AnnotationDefinition annotationDefinition) {
        clearCurrentValuePairEditorPages();
        pages.clear();
        pages.add(searchAnnotationPage);
        this.annotationDefinition = annotationDefinition;

        if (annotationDefinition != null) {
            for (AnnotationValuePairDefinition valuePairDefinition : annotationDefinition.getValuePairs()) {
                pages.add(createValuePairEditorPage(valuePairDefinition));
            }
        }
        super.start();
    }

    private ValuePairEditorPage createValuePairEditorPage(AnnotationValuePairDefinition valuePairDefinition) {
        final ValuePairEditorPage valuePairEditorPage = iocManager.lookupBean(ValuePairEditorPage.class).getInstance();
        valuePairEditorPage.init(annotationDefinition,
                                 valuePairDefinition,
                                 target,
                                 project);
        return valuePairEditorPage;
    }

    private void clearCurrentValuePairEditorPages() {
        List<ValuePairEditorPage> editorPages = filterValuePairEditorPages();
        int valuePairEditors = editorPages.size();

        for (WizardPage page : editorPages) {
            pages.remove(page);
        }

        for (int i = 0; i < valuePairEditors; i++) {
            ValuePairEditorPage valuePairEditorPage = editorPages.remove(0);
            iocManager.destroyBean(valuePairEditorPage);
        }
    }

    private void invokeOnCloseCallback(Annotation annotation) {
        if (onCloseCallback != null) {
            onCloseCallback.callback(annotation);
        }
    }

    private List<ValuePairEditorPage> filterValuePairEditorPages() {
        List<ValuePairEditorPage> result = new ArrayList<>(pages.size());
        for (WizardPage page : pages) {
            if (page instanceof ValuePairEditorPage) {
                result.add((ValuePairEditorPage) page);
            }
        }
        return result;
    }

    public static class CreateAnnotationWizardErrorCallback implements ErrorCallback<Message> {

        public CreateAnnotationWizardErrorCallback() {
        }

        @Override
        public boolean error(Message message,
                             Throwable throwable) {
            Window.alert("Unexpected error encountered : " + throwable.getMessage());
            return false;
        }
    }
}
