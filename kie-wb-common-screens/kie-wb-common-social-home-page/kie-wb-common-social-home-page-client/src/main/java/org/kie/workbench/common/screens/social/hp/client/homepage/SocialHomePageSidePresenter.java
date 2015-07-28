/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.uberfire.social.activities.client.widgets.item.model.LinkCommandParams;
import org.kie.uberfire.social.activities.client.widgets.pagination.Next;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialPaged;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.screens.social.hp.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.social.hp.client.util.IconLocator;
import org.kie.workbench.common.screens.social.hp.predicate.UserTimeLineFileChangesPredicate;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.ParameterizedCommand;

@ApplicationScoped
@WorkbenchScreen( identifier = "SocialHomePageSidePresenter" )
public class SocialHomePageSidePresenter {

    public interface View extends UberView<SocialHomePageSidePresenter> {

        void setupWidget( SimpleSocialTimelineWidgetModel model );
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private User loggedUser;

    @Inject
    private IconLocator iconLocator;

    @Inject
    private DefaultSocialLinkCommandGenerator linkCommandGenerator;

    @OnOpen
    public void onOpen() {
        final SocialPaged socialPaged = new SocialPaged( 5 );
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                SimpleSocialTimelineWidgetModel model = new SimpleSocialTimelineWidgetModel( socialUser, new UserTimeLineFileChangesPredicate(), placeManager, socialPaged )
                        .withOnlyMorePagination( new Next() {{
                            setText( "(more...)" );
                        }} )
                        .withIcons( iconLocator.getResourceTypes() )
                        .withLinkCommand( generateLinkCommand() );
                view.setupWidget( model );
            }
        } ).findSocialUser( loggedUser.getIdentifier() );
    }

    private ParameterizedCommand<LinkCommandParams> generateLinkCommand() {
        return linkCommandGenerator.generateLinkCommand();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.RecentAssets();
    }

    @WorkbenchPartView
    public UberView<SocialHomePageSidePresenter> getView() {
        return view;
    }

}
