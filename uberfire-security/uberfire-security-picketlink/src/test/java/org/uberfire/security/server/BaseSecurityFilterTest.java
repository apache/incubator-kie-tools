package org.uberfire.security.server;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.uberfire.security.server.mock.MockFilterConfig;
import org.uberfire.security.server.mock.MockHttpSession;
import org.uberfire.security.server.mock.MockServletContext;
import org.uberfire.security.server.mock.NullAuthProvider;
import org.uberfire.security.server.mock.TestingRoleProvider;


public abstract class BaseSecurityFilterTest {

    /**
     * Configuration that can be passed to {@link UberFireSecurityFilter#init(javax.servlet.FilterConfig)}.
     */
    protected MockFilterConfig filterConfig;

    /**
     * A mock HttpSession that mock requests can use.
     */
    protected MockHttpSession mockHttpSession;

    @Before
    public void setup() {
        ServletContext context = new MockServletContext();

        filterConfig = new MockFilterConfig( context );

        // useful minimum configuration. tests may overwrite these values before calling filter.init().
        filterConfig.initParams.put( SecurityConstants.ROLE_PROVIDER_KEY, TestingRoleProvider.class.getName() );
        filterConfig.initParams.put( SecurityConstants.AUTH_PROVIDER_KEY, NullAuthProvider.class.getName() );

        mockHttpSession = new MockHttpSession();
    }

}
