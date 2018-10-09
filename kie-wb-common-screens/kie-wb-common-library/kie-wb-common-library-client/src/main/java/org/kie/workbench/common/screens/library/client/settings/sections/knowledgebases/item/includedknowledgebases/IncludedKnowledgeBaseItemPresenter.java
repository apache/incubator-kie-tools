/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.includedknowledgebases;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.KnowledgeBaseItemPresenter;
import org.kie.workbench.common.services.shared.kmodule.SingleValueItemObjectModel;
import org.kie.workbench.common.widgets.client.widget.ListItemPresenter;
import org.kie.workbench.common.widgets.client.widget.ListItemView;

@Dependent
public class IncludedKnowledgeBaseItemPresenter extends ListItemPresenter<SingleValueItemObjectModel, KnowledgeBaseItemPresenter, IncludedKnowledgeBaseItemPresenter.View> {

    private SingleValueItemObjectModel knowledgeBaseNameObject;

    KnowledgeBaseItemPresenter parentPresenter;

    @Inject
    public IncludedKnowledgeBaseItemPresenter(final View view) {
        super(view);
    }

    @Override
    public IncludedKnowledgeBaseItemPresenter setup(final SingleValueItemObjectModel knowledgeBaseName,
                                                    final KnowledgeBaseItemPresenter parentPresenter) {
        this.parentPresenter = parentPresenter;
        this.knowledgeBaseNameObject = knowledgeBaseName;

        view.init(this);
        view.setName(knowledgeBaseNameObject.getValue());

        return this;
    }

    @Override
    public SingleValueItemObjectModel getObject() {
        return knowledgeBaseNameObject;
    }

    @Override
    public void remove() {
        super.remove();
        parentPresenter.fireChangeEvent();
    }

    public void onKnowledgeBaseNamChange(final String name){
        knowledgeBaseNameObject.setValue(name);
        parentPresenter.fireChangeEvent();
    }

    public interface View extends ListItemView<IncludedKnowledgeBaseItemPresenter>,
                                  IsElement {

        void setName(final String name);
    }
}
