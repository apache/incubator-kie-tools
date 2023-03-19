/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.breadcrumbs.widget;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

@Dependent
public class DefaultBreadcrumbsPresenter implements BreadcrumbPresenter {

    private final View view;
    private Command selectCommand;
    private boolean removeDeepLevelBreadcrumbsAfterActivation = true;

    @Inject
    public DefaultBreadcrumbsPresenter(final View view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final String label,
                      final boolean removeDeepLevelBreadcrumbsAfterActivation,
                      final Command selectCommand) {
        this.selectCommand = selectCommand;
        this.removeDeepLevelBreadcrumbsAfterActivation = removeDeepLevelBreadcrumbsAfterActivation;
        view.setup(label);

        if (selectCommand == null) {
            view.setNoAction();
        }
    }

    public boolean hasToRemoveDeepLevelBreadcrumbsAfterActivation() {
        return removeDeepLevelBreadcrumbsAfterActivation;
    }

    @Override
    public void activate() {
        view.activate();
    }

    @Override
    public void deactivate() {
        view.deactivate();
    }

    @Override
    public View getView() {
        return view;
    }

    public void onClick() {
        if (selectCommand != null) {
            selectCommand.execute();
        }
    }

    public interface View extends UberElemental<DefaultBreadcrumbsPresenter> {

        void setup(String label);

        void activate();

        void deactivate();

        void setNoAction();
    }
}