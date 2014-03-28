package org.uberfire.security.server;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.uberfire.security.server.SecurityConstants.*;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.uberfire.security.impl.SubjectImpl;
import org.uberfire.security.server.auth.FormAuthenticationScheme;
import org.uberfire.security.server.cdi.SecurityFactory;
import org.uberfire.security.server.mock.MockAuthenticationManager;
import org.uberfire.security.server.mock.MockAuthenticationProvider;
import org.uberfire.security.server.mock.MockAuthenticationScheme;

public class FormBasedLoginTest extends BaseSecurityFilterTest {

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
    public void successfulFormBasedLoginShouldRedirectToForceUrl() throws Exception {

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
        when( request.getServletPath() ).thenReturn( HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getRequestURI() ).thenReturn( contextPath + HTTP_FORM_SECURITY_CHECK_URI );
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

    /**
     * This test is the complement of {@link #successfulFormBasedLoginShouldRedirectToForceUrl()} for cases when
     * AUTH_FORCE_URL is not set. In this case, the framework is supposed to remember the initially denied request and
     * redirect back to it.
     */
    @Test
    public void successfulLoginShouldRedirectToInitiallyDeniedUrl() throws Exception {

        // accept any credentials as valid :)
        filterConfig.initParams.put( SecurityConstants.AUTH_PROVIDER_KEY, MockAuthenticationProvider.class.getName() );

        // force server-side redirect upon successful login
        final String contextPath = "/test-context";
        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";

        final RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );

        // First request to /test-context/MyGwtModule/MyGwtHostPage.html (should be remembered)
        HttpServletRequest initialRequest = mock( HttpServletRequest.class );
        when( initialRequest.getContextPath() ).thenReturn( contextPath );
        when( initialRequest.getServletPath() ).thenReturn( "" );
        when( initialRequest.getRequestURI() ).thenReturn( contextPath + hostPageUri );
        when( initialRequest.getSession() ).thenReturn( mockHttpSession );
        when( initialRequest.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        when( initialRequest.getRequestDispatcher( anyString() )).thenReturn( requestDispatcher );

        HttpServletResponse initialResponse = mock( HttpServletResponse.class );
        FilterChain initialFilterChain = mock( FilterChain.class );

        uberFireFilter.doFilter( initialRequest, initialResponse, initialFilterChain );

        verify( initialResponse ).sendError( eq( 401 ), anyString() );
        verify( initialResponse, never() ).sendRedirect( anyString() );
        verify( initialFilterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );


        // Now an intervening request, for example, to the login page or a css file (should not be remembered)
        HttpServletRequest middleRequest = mock( HttpServletRequest.class );
        when( middleRequest.getContextPath() ).thenReturn( contextPath );
        when( middleRequest.getServletPath() ).thenReturn( "" );
        when( middleRequest.getRequestURI() ).thenReturn( contextPath + "/login.jsp" );
        when( middleRequest.getSession() ).thenReturn( mockHttpSession );
        when( middleRequest.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        when( middleRequest.getRequestDispatcher( anyString() )).thenReturn( requestDispatcher );

        HttpServletResponse middleResponse = mock( HttpServletResponse.class );
        FilterChain middleFilterChain = mock( FilterChain.class );

        uberFireFilter.doFilter( middleRequest, middleResponse, middleFilterChain );


        // Finally the login request (should redirect back to initial request)
        HttpServletRequest authRequest = mock( HttpServletRequest.class, withSettings().verboseLogging() );
        when( authRequest.getContextPath() ).thenReturn( contextPath );
        when( authRequest.getServletPath() ).thenReturn( HTTP_FORM_SECURITY_CHECK_URI );
        when( authRequest.getRequestURI() ).thenReturn( contextPath + HTTP_FORM_SECURITY_CHECK_URI );
        when( authRequest.getParameter( "uf_username" ) ).thenReturn( "i-am-a-user" );
        when( authRequest.getParameter( "uf_password" ) ).thenReturn( "let-me-in" );
        when( authRequest.getSession() ).thenReturn( mockHttpSession );
        when( authRequest.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        when( authRequest.getRequestDispatcher( anyString() )).thenReturn( requestDispatcher );

        HttpServletResponse authResponse = mock( HttpServletResponse.class );

        FilterChain authFilterChain = mock( FilterChain.class );

        uberFireFilter.doFilter( authRequest, authResponse, authFilterChain );

        verify( authResponse, never() ).sendError( anyInt(), anyString() );
        verify( authResponse ).sendRedirect( contextPath + hostPageUri );
        verify( authFilterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    @Test
    public void newLoginAttemptShouldTakePrecedenceOverExistingSessionData() throws Exception {

        filterConfig.initParams.put( SecurityConstants.AUTH_SCHEME_KEY, FormAuthenticationScheme.class.getName() );

        // will accept any credentials discovered by FormAuthenticationScheme as valid
        filterConfig.initParams.put( AUTH_PROVIDER_KEY, MockAuthenticationProvider.class.getName() );

        // force server-side redirect upon successful login
        final String contextPath = "/test-context";
        final String hostPageUri = "/MyGwtModule/MyGwtHostPage.html";
        filterConfig.initParams.put( SecurityConstants.AUTH_FORCE_URL, hostPageUri );

        RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );

        // we assume this works because it's tested in SessionStorageAuthProviderTest
        SubjectImpl previouslyLoggedInUser = new SubjectImpl("previously_logged_in");
        mockHttpSession.setAttribute( SUBJECT_ON_SESSION_KEY, previouslyLoggedInUser );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getContextPath() ).thenReturn( contextPath );
        when( request.getServletPath() ).thenReturn( HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getRequestURI() ).thenReturn( contextPath + HTTP_FORM_SECURITY_CHECK_URI );
        when( request.getParameter( HTTP_FORM_USERNAME_PARAM )).thenReturn( "logged_in_via_form" );
        when( request.getParameter( HTTP_FORM_PASSWORD_PARAM )).thenReturn( "logged_in_via_form" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );
        when( request.getRequestDispatcher( anyString() )).thenReturn( requestDispatcher );

        HttpServletResponse response = mock( HttpServletResponse.class );

        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the new form-based login attempt must take precedence over the existing session info
        assertEquals( "logged_in_via_form", SecurityFactory.getIdentity().getName() );

        // and the AUTH_FORCE_URL redirect should have happened too
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response ).sendRedirect( contextPath + hostPageUri );
        verify( filterChain, never() ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );
    }

    /**
     * This tests for a request which is recognized (by some auth provider) as a login request and is also a request to
     * the AUTH_FORCE_URL. This isn't a form-based login, because that always has a target URL of /uf_security_check.
     */
    @Test
    public void loginRequestToForceUrlShouldNotRedirectBackToItself() throws Exception {
        final String contextPath = "/test-context";
        final String forceUri = "/force-uri.html";

        // treat every request as a login attempt
        filterConfig.initParams.put( SecurityConstants.AUTH_SCHEME_KEY, MockAuthenticationScheme.class.getName() );

        // accept any credentials as valid :)
        filterConfig.initParams.put( SecurityConstants.AUTH_PROVIDER_KEY, MockAuthenticationProvider.class.getName() );

        filterConfig.initParams.put( SecurityConstants.AUTH_FORCE_URL, forceUri );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "" );
        when( request.getContextPath() ).thenReturn( contextPath );
        when( request.getRequestURI() ).thenReturn( contextPath + forceUri );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );

        HttpServletResponse response = mock( HttpServletResponse.class, withSettings().verboseLogging() );
        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the AUTH_FORCE_URL redirect must not have happened (it would be a loop)
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendRedirect( contextPath + forceUri ); // redundant but has a better failure message
        verify( response, never() ).sendRedirect( anyString() );
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

    }

    /**
     * The request in this test is not a login request; it's for an ongoing session. It targets AUTH_FORCE_URL, which is
     * actually a common scenario. This request must get through the filter without a redirect.
     */
    @Test
    public void authenticatedRequestToForceUrlShouldNotRedirectBackToItself() throws Exception {
        final String contextPath = "/test-context";
        final String forceUri = "/force-uri.html";

        // we assume this works because it's tested in SessionStorageAuthProviderTest
        SubjectImpl previouslyLoggedInUser = new SubjectImpl("previously_logged_in");
        mockHttpSession.setAttribute( SUBJECT_ON_SESSION_KEY, previouslyLoggedInUser );

        filterConfig.initParams.put( SecurityConstants.AUTH_FORCE_URL, forceUri );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "" );
        when( request.getRequestURI() ).thenReturn( contextPath + forceUri );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );

        HttpServletResponse response = mock( HttpServletResponse.class );
        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the AUTH_FORCE_URL redirect must not have happened (it would be a loop)
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendRedirect( contextPath + forceUri ); // redundant but has a better failure message
        verify( response, never() ).sendRedirect( anyString() );
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

    }

    /**
     * The request in this test is not a login request; it's for an ongoing session. It should not get redirected to the
     * AUTH_FORCE_URL.
     */
    @Test
    public void authenticatedRequestToAnyUrlShouldNotRedirectToForceUrl() throws Exception {
        final String contextPath = "/test-context";
        final String forceUri = "/force-uri.html";

        // we assume this works because it's tested in SessionStorageAuthProviderTest
        SubjectImpl previouslyLoggedInUser = new SubjectImpl("previously_logged_in");
        mockHttpSession.setAttribute( SUBJECT_ON_SESSION_KEY, previouslyLoggedInUser );

        filterConfig.initParams.put( SecurityConstants.AUTH_FORCE_URL, forceUri );

        HttpServletRequest request = mock( HttpServletRequest.class );
        when( request.getServletPath() ).thenReturn( "" );
        when( request.getRequestURI() ).thenReturn( contextPath + "/foo.css" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );

        HttpServletResponse response = mock( HttpServletResponse.class );
        FilterChain filterChain = mock( FilterChain.class );

        UberFireSecurityFilter uberFireFilter = new UberFireSecurityFilter();
        uberFireFilter.init( filterConfig );
        uberFireFilter.doFilter( request, response, filterChain );

        // the AUTH_FORCE_URL redirect must not have happened (it would be a loop)
        verify( response, never() ).sendError( anyInt(), anyString() );
        verify( response, never() ).sendRedirect( contextPath + forceUri ); // redundant but has a better failure message
        verify( response, never() ).sendRedirect( anyString() );
        verify( filterChain ).doFilter( any(HttpServletRequest.class), any(HttpServletResponse.class) );

    }

}
