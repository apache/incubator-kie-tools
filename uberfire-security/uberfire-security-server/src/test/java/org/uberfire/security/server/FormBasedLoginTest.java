package org.uberfire.security.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.security.server.mock.MockAuthenticationManager;
import org.uberfire.security.server.mock.MockAuthenticationProvider;
import org.uberfire.security.server.mock.MockAuthenticationScheme;
import org.uberfire.security.server.mock.MockFilterConfig;
import org.uberfire.security.server.mock.MockHttpSession;
import org.uberfire.security.server.mock.MockServletContext;
import org.uberfire.security.server.mock.TestingRoleProvider;

public class FormBasedLoginTest {

    /**
     * Configuration that can be passed to {@link UberFireSecurityFilter#init(javax.servlet.FilterConfig)}.
     */
    private MockFilterConfig filterConfig;
    
    /**
     * A mock HttpSession that mock requests can use.
     */
    private MockHttpSession mockHttpSession;

    @Before
    public void setup() {
        ServletContext context = new MockServletContext();
        
        filterConfig = new MockFilterConfig( context );
        filterConfig.initParams.put( SecurityConstants.COOKIE_NAME_KEY, "test-remember-me-cookie" );
        filterConfig.initParams.put( SecurityConstants.ROLE_PROVIDER_KEY, TestingRoleProvider.class.getName() );
        
        mockHttpSession = new MockHttpSession();
    }
    
    /**
     * The client-side of the security framework watches for 401 errors on ErraiBus communication attempts, and it
     * does a redirect to the login page when that happens. This test ensures unauthenticated ErraiBus requests
     * result in a 401 error.
     */
    @Test
    public void test401WhenNotAuthenticated() throws Exception {
        // we want to displace FormAuthenticationScheme, which is installed by default if no auth scheme is specified
        filterConfig.initParams.put( SecurityConstants.AUTH_SCHEME_KEY, MockAuthenticationScheme.class.getName() );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "/in.erraiBus" );
        when( request.getRequestURI() ).thenReturn( "/test-context/in.erraiBus" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        
        HttpServletResponse response = mock( HttpServletResponse.class );

        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        verify( response ).sendError( eq( 401 ), anyString() );
        
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    @Test
    public void shouldPassAuthenticatedRequestsThrough() throws Exception {
        // always claim the user is logged in
        filterConfig.initParams.put( SecurityConstants.AUTH_MANAGER_KEY, MockAuthenticationManager.class.getName() );
        
        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "/in.erraiBus" );
        when( request.getRequestURI() ).thenReturn( "/test-context/in.erraiBus" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        
        HttpServletResponse response = mock( HttpServletResponse.class );

        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // make sure the filter didn't commit the response, with specific checks for the most likely reasons it might have
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendError( anyInt() );
        verify( response, never() ).sendRedirect( anyString() );
        assertFalse( response.isCommitted() );
        
        // and most importantly, it passed the request up the filter chain!
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    /**
     * This test protects the redirect-after-login behaviour that the uberfire-tutorial project relies on.
     */
    @Test
    public void shouldRedirectToHostPageUponSuccessfulLogin() throws Exception {
        
        // treat every request as a login attempt
        filterConfig.initParams.put( SecurityConstants.AUTH_SCHEME_KEY, MockAuthenticationScheme.class.getName() );
        
        // accept any credentials as valid :)
        filterConfig.initParams.put( SecurityConstants.AUTH_PROVIDER_KEY, MockAuthenticationProvider.class.getName() );
        
        // force server-side redirect upon successful login
        final String contextPath = "/test-context";
        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";
        filterConfig.initParams.put( SecurityConstants.AUTH_FORCE_URL, hostPageUri );
        
        RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getContextPath() ).thenReturn( contextPath );
        when( request.getServletPath() ).thenReturn( "/uf_security_check" );
        when( request.getRequestURI() ).thenReturn( contextPath + "/uf_security_check" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        when( request.getRequestDispatcher( anyString() )).thenReturn( requestDispatcher );
        
        HttpServletResponse response = mock( HttpServletResponse.class );

        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response ).sendRedirect( contextPath + hostPageUri );
        
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

}
