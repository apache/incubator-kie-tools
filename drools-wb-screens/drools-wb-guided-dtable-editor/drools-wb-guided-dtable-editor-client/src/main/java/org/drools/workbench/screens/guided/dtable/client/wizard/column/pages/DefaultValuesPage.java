/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.guided.dtable.shared.model.BRLColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasDefaultValuesPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasRuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class DefaultValuesPage<T extends HasDefaultValuesPage & HasRuleModellerPage>
        extends BaseDecisionTableColumnPage<T> {

    private View view;

    @Inject
    public DefaultValuesPage(final View view,
                             final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.ValueOptionsPage_ValueOptions);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(true);
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public void prepareView() {
        view.init(this);

        view.clear();

        plugin.setupDefinedVariables(plugin().getRuleModel());

        for (final BRLVariableColumn childColumn : getChildColumns()) {
            final IsWidget widget = factory().getWidget(childColumn,
                                                        getDefaultValue(childColumn));
            view.addVariable(childColumn.getVarName(),
                             widget);
        }
    }

    private List<BRLVariableColumn> getChildColumns() {
        if (plugin().editingCol() instanceof BRLColumn) {
            return ((BRLColumn) plugin().editingCol()).getChildColumns();
        } else {
            return Collections.emptyList();
        }
    }

    private DTCellValue52 getDefaultValue(final BRLVariableColumn childColumn) {
        final DTColumnConfig52 column = (DTColumnConfig52) childColumn;
        if (column.getDefaultValue() == null) {
            column.setDefaultValue(factory().makeNewValue(column));
        }
        return column.getDefaultValue();
    }

    protected DTCellValueWidgetFactory factory() {
        return DTCellValueWidgetFactory.getInstance(plugin().getPresenter().getModel(),
                                                    plugin().getPresenter().getDataModelOracle(),
                                                    false,
                                                    plugin().getPresenter().getModel().getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY);
    }

    public interface View extends UberElement<DefaultValuesPage> {

        void clear();

        void addVariable(final String varName, final IsWidget widget);
    }
}
