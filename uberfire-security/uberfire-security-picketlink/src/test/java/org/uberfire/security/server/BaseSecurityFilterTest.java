package org.uberfire.security.server;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.picketlink.authentication.web.AuthenticationFilter.*;
import static org.uberfire.security.server.FormAuthenticationScheme.*;

import javax.enterprise.inject.Instance;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.picketlink.Identity;
import org.picketlink.authentication.web.AuthenticationFilter;
import org.picketlink.authentication.web.HTTPAuthenticationScheme;
import org.picketlink.credential.DefaultLoginCredentials;
import org.uberfire.security.server.mock.MockFilterConfig;
import org.uberfire.security.server.mock.MockHttpSession;
import org.uberfire.security.server.mock.MockIdentity;
import org.uberfire.security.server.mock.MockServletContext;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseSecurityFilterTest {

    /**
     * Configuration that can be passed to {@link UberFireSecurityFilter#init(javax.servlet.FilterConfig)}.
     */
    protected MockFilterConfig filterConfig;

    /**
     * A mock HttpSession that mock requests can use. This value is returned as the session from the mock request.
     */
    protected MockHttpSession mockHttpSession;

    @Mock
    protected HttpServletRequest request;

    @Mock
    protected HttpServletResponse response;

    @Mock
    protected FilterChain filterChain;

    @Mock
    protected Instance<DefaultLoginCredentials> credentialsInstance;

    @Spy
    protected DefaultLoginCredentials credentials = new DefaultLoginCredentials();

    @Mock
    protected Instance<Identity> identityInstance;

    @Spy
    protected MockIdentity identity;

    @Mock( name = "applicationPreferredAuthSchemeInstance" )
    protected Instance<HTTPAuthenticationScheme> preferredAuthFilterInstance;

    @InjectMocks
    protected AuthenticationFilter authFilter;

    @Before
    public void setup() {
        filterConfig = new MockFilterConfig( new MockServletContext() );

        // useful minimum configuration. tests may overwrite these values before calling filter.init().
        filterConfig.initParams.put( HOST_PAGE_INIT_PARAM, "/dont/care" );
        filterConfig.initParams.put( FORCE_REAUTHENTICATION_INIT_PARAM, "true" );

        mockHttpSession = new MockHttpSession();

        when( request.getMethod() ).thenReturn( "POST" );
        when( request.getSession() ).thenReturn( mockHttpSession );
        when( request.getSession( anyBoolean() ) ).thenReturn( mockHttpSession );

        identity.setCredentials( credentials );

        when( identityInstance.get() ).thenReturn( identity );

        when( credentialsInstance.get() ).thenReturn( credentials );

        when( preferredAuthFilterInstance.get() ).thenReturn( new FormAuthenticationScheme() );
    }

}
