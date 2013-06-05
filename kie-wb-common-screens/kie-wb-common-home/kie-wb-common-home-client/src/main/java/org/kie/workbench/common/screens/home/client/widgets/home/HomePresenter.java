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
package org.kie.workbench.common.screens.home.client.widgets.home;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.bus.client.api.base.DefaultErrorCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.home.client.model.HomeModel;
import org.kie.workbench.common.screens.home.client.resources.i18n.HomeConstants;
import org.kie.workbench.common.screens.home.service.HomeService;
import org.uberfire.backend.group.Group;
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

        void setModel( final HomeModel model );

        void setGroups( final Collection<Group> groups );

    }

    @Inject
    private HomeView view;

    @Inject
    private HomeModel model;

    @Inject
    private Caller<HomeService> homeService;

    @PostConstruct
    public void init() {
        view.setModel( model );

        homeService.call( new RemoteCallback<Collection<Group>>() {
            @Override
            public void callback( final Collection<Group> groups ) {
                view.setGroups( groups );
            }
        }, new DefaultErrorCallback() ).getGroups();
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
