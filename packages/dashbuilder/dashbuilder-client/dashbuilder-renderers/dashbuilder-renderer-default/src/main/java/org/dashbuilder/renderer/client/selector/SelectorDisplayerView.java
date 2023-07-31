/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.renderer.client.selector;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import jsinterop.base.Js;
import org.dashbuilder.displayer.client.AbstractErraiDisplayerView;
import org.dashbuilder.patternfly.select.Select;
import org.dashbuilder.renderer.client.resources.i18n.SelectorConstants;

@Dependent
public class SelectorDisplayerView extends AbstractErraiDisplayerView<SelectorDisplayer> implements
                                   SelectorDisplayer.View {

    @Inject
    protected Select select;

    @Override
    public void init(SelectorDisplayer presenter) {
        super.setPresenter(presenter);
        super.setVisualization(Js.cast(select.getElement()));
    }

    @Override
    public void showSelectHint(String column) {
        showHint("- " + SelectorConstants.INSTANCE.selectorDisplayer_select() + " " + column + " -");
    }

    @Override
    public void showResetHint(String column) {
        showHint("- " + SelectorConstants.INSTANCE.selectorDisplayer_reset() + " " + column + " -");
    }

    protected void showHint(String hint) {
        select.setPromptText(hint);
    }

    @Override
    public void clearItems() {
        select.clear();
    }

    @Override
    public void addItem(String id, String value, boolean selected) {
        select.addItem(value, id);
        if (selected) {
            select.setSelectedIndex(select.getItemCount() - 1);
        }
    }

    @Override
    public String getSelectedId() {
        return select.getSelectedId();
    }

    @Override
    public int getItemCount() {
        return select.getItemCount();
    }

    @Override
    public void setItemTitle(int index, String title) {
        select.setItemTitle(index, title);
    }

    @Override
    public void setFilterEnabled(boolean enabled) {
        if (enabled) {
            select.addChangeAction(getPresenter()::onItemSelected);
        }
    }

    @Override
    public String getGroupsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_groupsTitle();
    }

    @Override
    public String getColumnsTitle() {
        return SelectorConstants.INSTANCE.selectorDisplayer_columnsTitle();
    }
}
