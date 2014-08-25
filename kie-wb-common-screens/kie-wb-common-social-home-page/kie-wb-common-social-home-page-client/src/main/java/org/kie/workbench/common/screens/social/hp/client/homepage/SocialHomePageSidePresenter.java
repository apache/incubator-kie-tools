package org.kie.workbench.common.screens.social.hp.client.homepage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.social.activities.client.widgets.timeline.simple.model.SimpleSocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.workbench.common.screens.social.hp.predicate.UserTimeLineFileChangesPredicate;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.mvp.Command;
import org.uberfire.security.Identity;

@ApplicationScoped
@WorkbenchScreen(identifier = "SocialHomePageSidePresenter")
public class SocialHomePageSidePresenter {

    public interface View extends UberView<SocialHomePageSidePresenter> {

        void setupWidget( SimpleSocialTimelineWidgetModel model,
                          Command linkCommand );
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    Caller<SocialUserRepositoryAPI> socialUserRepositoryAPI;

    @Inject
    private Identity loggedUser;

    @PostConstruct
    public void init() {
    }

    @AfterInitialization
    public void loadContent() {

    }

    @OnOpen
    public void onOpen(){
        socialUserRepositoryAPI.call( new RemoteCallback<SocialUser>() {
            public void callback( SocialUser socialUser ) {
                SimpleSocialTimelineWidgetModel model = new SimpleSocialTimelineWidgetModel( socialUser, "Recent Assets", new UserTimeLineFileChangesPredicate(), placeManager );
                view.setupWidget( model, new Command() {
                    @Override
                    public void execute() {
                        Window.alert("TODO");
                    }
                } );

            }
        } ).findSocialUser(loggedUser.getName() );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Social HomePage Side Presenter";
    }

    @WorkbenchPartView
    public UberView<SocialHomePageSidePresenter> getView() {
        return view;
    }

}
