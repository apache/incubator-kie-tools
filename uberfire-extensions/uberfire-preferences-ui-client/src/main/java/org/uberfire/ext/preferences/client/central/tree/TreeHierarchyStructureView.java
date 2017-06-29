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

package org.uberfire.ext.preferences.client.central.tree;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.preferences.client.resources.i18n.Constants;

@Dependent
@Templated
public class TreeHierarchyStructureView implements IsElement,
                                                   TreeHierarchyStructurePresenter.View {

    private final TranslationService translationService;

    @Inject
    @DataField("preference-tree")
    Div tree;

    @Inject
    public TreeHierarchyStructureView(final TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void init(final TreeHierarchyStructurePresenter presenter) {
        tree.setInnerHTML("");
        tree.appendChild(((IsElement) presenter.getHierarchyItem().getView()).getElement());
    }

    @Override
    public String getTranslation(final String key) {
        return translationService.format(key);
    }

    @Override
    public String getSaveSuccessMessage() {
        return translationService.format(Constants.TreeHierarchyStructureView_SaveSuccess);
    }

    @Override
    public String getSaveErrorMessage(String message) {
        return translationService.format(Constants.UnexpectedErrorWhileSaving,
                                         message);
    }
}
