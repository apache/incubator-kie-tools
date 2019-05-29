package org.uberfire.java.nio.fs.jgit.daemon.http;

import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.uberfire.java.nio.fs.jgit.AbstractTestInfra;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HTTPSupportDisableTest extends AbstractTestInfra {

    /*
     * Default Git preferences suitable for most of the tests. If specific test needs some custom configuration, it needs to
     * override this method and provide own map of preferences.
     */
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put(JGitFileSystemProviderConfiguration.GIT_HTTP_ENABLED, "false");
        return gitPrefs;
    }

    @Test
    public void test() {
        final HTTPSupport httpSupport = new HTTPSupport() {
            @Override
            protected JGitFileSystemProvider resolveProvider() {
                return provider;
            }
        };

        final ServletContextEvent sce = mock(ServletContextEvent.class);
        final ServletContext sc = mock(ServletContext.class);
        when(sce.getServletContext()).thenReturn(sc);
        httpSupport.contextInitialized(sce);
        assertFalse(provider.getFullHostNames().containsKey("http"));

        verify(sc, times(0)).addServlet(anyString(), any(Servlet.class));
    }
}
