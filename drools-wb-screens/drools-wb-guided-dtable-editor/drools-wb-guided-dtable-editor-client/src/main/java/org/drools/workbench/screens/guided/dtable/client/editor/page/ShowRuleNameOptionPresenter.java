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
package org.drools.workbench.screens.guided.dtable.client.editor.page;

import javax.inject.Inject;

import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElemental;

public class ShowRuleNameOptionPresenter {

    private View view;
    private Callback<Boolean> callback;

    public ShowRuleNameOptionPresenter() {
        // CDI
    }

    @Inject
    public ShowRuleNameOptionPresenter(final View view) {
        this.view = view;
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void addOptionChangeCallback(Callback<Boolean> callback) {
        this.callback = callback;
    }

    public void setShowRuleName(boolean show) {
        view.setShowRuleName(show);
    }

    public void onRuleNameCheckboxChanged(boolean checked) {
        if (callback != null) {
            callback.callback(checked);
        }
    }

    public interface View extends UberElemental<ShowRuleNameOptionPresenter> {

        void setShowRuleName(final boolean show);
    }
}
