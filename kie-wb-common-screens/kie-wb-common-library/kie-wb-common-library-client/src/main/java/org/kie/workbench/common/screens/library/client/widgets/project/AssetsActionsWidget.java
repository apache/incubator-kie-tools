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

package org.kie.workbench.common.screens.library.client.widgets.project;

import javax.inject.Inject;

import org.kie.workbench.common.screens.library.client.util.ResourceUtils;
import org.kie.workbench.common.widgets.client.handlers.NewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.uberfire.client.mvp.UberElement;

import static org.kie.workbench.common.screens.library.client.util.ResourceUtils.isProjectHandler;

public class AssetsActionsWidget {

    public interface View extends UberElement<AssetsActionsWidget> {

        void addResourceHandler(final NewResourceHandler newResourceHandler);
    }

    private View view;

    private NewResourcePresenter newResourcePresenter;

    private ResourceUtils resourceUtils;

    @Inject
    public AssetsActionsWidget(final View view,
                               final NewResourcePresenter newResourcePresenter,
                               final ResourceUtils resourceUtils) {
        this.view = view;
        this.newResourcePresenter = newResourcePresenter;
        this.resourceUtils = resourceUtils;
    }

    public void init() {
        view.init(this);
        resourceUtils.getAlphabeticallyOrderedNewResourceHandlers().stream().filter(newResourceHandler -> newResourceHandler.canCreate()
                && !isProjectHandler(newResourceHandler)).forEach(newResourceHandler -> view.addResourceHandler(newResourceHandler));
    }

    public NewResourcePresenter getNewResourcePresenter() {
        return newResourcePresenter;
    }

    public View getView() {
        return view;
    }
}
