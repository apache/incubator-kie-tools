/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.common.screens.home.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.screens.home.client.resources.i18n.HomeConstants;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;

@Dependent
@WorkbenchScreen(identifier = "org.kie.workbench.common.screens.home.client.HomePresenter")
public class HomePresenter {

    public interface HomeView
            extends
            UberView<HomePresenter> {

    }

    @Inject
    private HomeView view;

    @PostConstruct
    public void init() {
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return HomeConstants.INSTANCE.homeName();
    }

    @WorkbenchPartView
    public UberView<HomePresenter> getView() {
        return view;
    }

}
