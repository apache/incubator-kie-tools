/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.editor.page;

import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.views.pfly.multipage.PageImpl;

public class ColumnsPage extends PageImpl {

    private final ColumnsPagePresenter pagePresenter;

    @Inject
    public ColumnsPage(final ColumnsPagePresenter pagePresenter,
                       final TranslationService translationService) {

        super(asWidget(pagePresenter), tabTitle(translationService));

        this.pagePresenter = pagePresenter;
    }

    private static String tabTitle(final TranslationService translationService) {
        return translationService.format(GuidedDecisionTableErraiConstants.ColumnsPage_Columns);
    }

    private static ElementWrapperWidget<?> asWidget(final ColumnsPagePresenter pagePresenter) {

        final ColumnsPagePresenter.View view = pagePresenter.getView();

        return ElementWrapperWidget.getWidget(view.getElement());
    }

    public void init(final GuidedDecisionTableModellerView.Presenter modeller) {
        pagePresenter.init(modeller);
    }

    @Override
    public void onFocus() {
        pagePresenter.refresh();
    }
}
