/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class KieSelectOptionElement extends ListItemPresenter<KieSelectOption, KieSelectElementBase, KieSelectOptionView> {

    private KieSelectOption option;

    @Inject
    public KieSelectOptionElement(final KieSelectOptionView view) {
        super(view);
    }

    @Override
    public KieSelectOptionElement setup(final KieSelectOption option,
                                        final KieSelectElementBase parentPresenter) {
        this.option = option;

        view.init(this);
        view.setLabel(option.label);
        view.setValue(option.value);

        return this;
    }

    @Override
    public KieSelectOption getObject() {
        return option;
    }
}
