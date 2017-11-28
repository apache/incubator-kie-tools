/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

@Dependent
public class SelectorDropDownItem extends AbstractSelectorItemPresenter {

    public interface View extends SelectorItemView<SelectorDropDownItem> {

        void setSelectionIconVisible(boolean visible);
    }

    protected View view;

    @Inject
    public SelectorDropDownItem(View view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setSelectionIconVisible(boolean selectionIconVisible) {
        view.setSelectionIconVisible(selectionIconVisible);
    }
}
