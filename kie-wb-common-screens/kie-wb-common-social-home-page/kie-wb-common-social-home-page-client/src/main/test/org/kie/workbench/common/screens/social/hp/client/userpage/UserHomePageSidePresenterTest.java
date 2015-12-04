package org.kie.workbench.common.screens.social.hp.client.userpage;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.service.SocialUserRepositoryAPI;
import org.kie.uberfire.social.activities.service.SocialUserServiceAPI;
import org.kie.workbench.common.screens.social.hp.client.homepage.events.UserHomepageSelectedEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class UserHomePageSidePresenterTest {

    UserHomePageSidePresenter presenter;

    @Mock
    private SocialUserServiceAPI socialUserServiceAPI;
    private CallerMock<SocialUserServiceAPI> socialUserServiceAPICaller;

    @Mock
    private SocialUserRepositoryAPI socialUserRepositoryAPI;
    private Caller<SocialUserRepositoryAPI> socialUserRepositoryAPICaller;

    @Mock
    private EventSourceMock<UserHomepageSelectedEvent> selectEvent;

    private SocialUser dora;

    private SocialUser bento;

    @Before
    public void setup() {
        presenter = new UserHomePageSidePresenter();

        socialUserServiceAPICaller = new CallerMock<SocialUserServiceAPI>( socialUserServiceAPI );

        socialUserRepositoryAPICaller = new CallerMock<SocialUserRepositoryAPI>( socialUserRepositoryAPI );

        presenter.socialUserService = socialUserServiceAPICaller;
        presenter.socialUserRepositoryAPI = socialUserRepositoryAPICaller;
        presenter.selectedEvent = selectEvent;

        presenter.users = new HashMap<String, SocialUser>();
        presenter.loggedUser = new UserImpl( "dora" );

        dora = new SocialUser( "dora" );
        bento = new SocialUser( "bento" );

        presenter.users.put( "dora", dora );
        presenter.users.put( "bento", bento );

    }

    @Test
    public void followUserShouldUpdateUserCache() {

        //clean cache
        presenter.users = new HashMap<String, SocialUser>(  );

        when( socialUserRepositoryAPI.findSocialUser( bento.getUserName() ) )
                .thenReturn( bento );

        presenter.followUser( bento );

        verify( selectEvent).fire( any( UserHomepageSelectedEvent.class ) );
        assertEquals( bento, presenter.users.get( "bento" ) );
    }


    @Test
    public void unfollowUserShouldUpdateUserCache() {

        presenter.users = new HashMap<String, SocialUser>(  );

        when( socialUserRepositoryAPI.findSocialUser( bento.getUserName() ) )
                .thenReturn( bento );

        presenter.unfollowUser( bento );

        verify( selectEvent).fire( any( UserHomepageSelectedEvent.class ) );
        assertEquals( bento, presenter.users.get( "bento" ) );
    }

}