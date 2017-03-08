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

package org.uberfire.ext.preferences.client.admin.category;

import java.util.List;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;

public class AdminPageCategoryPresenter {

    private final View view;
    private final ManagedInstance<AdminPageItemPresenter> adminPageItemPresenterProvider;

    @Inject
    public AdminPageCategoryPresenter(final View view,
                                      final ManagedInstance<AdminPageItemPresenter> adminPageItemPresenterProvider) {
        this.view = view;
        this.adminPageItemPresenterProvider = adminPageItemPresenterProvider;
    }

    public void setup(final List<AdminTool> adminTools,
                      final String screen,
                      final String perspectiveIdentifierToGoBackTo) {
        adminTools.forEach(adminTool -> {
            final AdminPageItemPresenter itemPresenter = adminPageItemPresenterProvider.get();
            itemPresenter.setup(adminTool,
                                screen,
                                perspectiveIdentifierToGoBackTo);
            view.add(itemPresenter.getView());
        });
    }

    public View getView() {
        return view;
    }

    public interface View extends UberElement<AdminPageCategoryPresenter> {

        void add(AdminPageItemPresenter.View rootItemView);
    }
}
