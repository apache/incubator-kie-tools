package org.uberfire.security.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Test;
import org.uberfire.security.server.cdi.SecurityFactory;


public class SessionStorageAuthProviderTest extends BaseSecurityFilterTest {

    @Test
    public void existingUserInSessionShouldBeAllowedIn() throws Exception {
        final String contextPath = "/test-context";

        UserImpl alreadyLoggedInUser = new UserImpl("already_logged_in");
        mockHttpSession.setAttribute( SecurityConstants.SUBJECT_ON_SESSION_KEY, alreadyLoggedInUser );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "/any-app-url" );
        when( request.getRequestURI() ).thenReturn( contextPath + "/any-app-url" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );

        HttpServletResponse response = mock( HttpServletResponse.class );

        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        assertEquals( "already_logged_in", SecurityFactory.getIdentity().getIdentifier() );
        verify( response, never() ).sendRedirect( anyString() );
        verify( response, never() ).sendError( anyInt() );
        verify( filterChain ).doFilter( any( ServletRequest.class ), any( ServletResponse.class) );
    }

}
