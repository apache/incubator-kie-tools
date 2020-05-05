package org.uberfire.java.nio.fs.jgit.daemon.http;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

import org.eclipse.jgit.http.server.GitServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

@WebListener
public class HTTPSupport implements ServletContextListener {

    private static final String GIT_PATH = "git";
    private static final Logger LOG = LoggerFactory.getLogger(HTTPSupport.class);

    private ServletContext servletContext = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        servletContext = sce.getServletContext();
        final JGitFileSystemProvider fsProvider = resolveProvider();
        if (fsProvider != null && (fsProvider.getConfig().isHttpEnabled() || fsProvider.getConfig().isHttpsEnabled())) {
            if (fsProvider.getConfig().isHttpEnabled()) {
                fsProvider.addHostName("http", fsProvider.getConfig().getHttpHostName() + ":" + fsProvider.getConfig().getHttpPort() + servletContext.getContextPath() + "/" + GIT_PATH);
            }
            if (fsProvider.getConfig().isHttpsEnabled()) {
                fsProvider.addHostName("https",
                                       fsProvider.getConfig().getHttpsHostName() + ":" + fsProvider.getConfig().getHttpsPort() + servletContext.getContextPath() + "/" + GIT_PATH);
            }
            fsProvider.updateCacheWithHostNames();
            final GitServlet gitServlet = new GitServlet();
            gitServlet.setRepositoryResolver(fsProvider.getRepositoryResolver());
            gitServlet.setAsIsFileService(null);
            gitServlet.setReceivePackFactory((req, db) -> fsProvider.getReceivePack("http", req, db));
            ServletRegistration.Dynamic sd = servletContext.addServlet("GitServlet", gitServlet);
            sd.addMapping("/" + GIT_PATH + "/*");
            sd.setLoadOnStartup(1);
            sd.setAsyncSupported(false);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        servletContext = null;
    }

    protected JGitFileSystemProvider resolveProvider() {
        try {
            return (JGitFileSystemProvider) FileSystemProviders.resolveProvider(URI.create(JGitFileSystemProviderConfiguration.SCHEME + "://whatever"));
        } catch (Exception ex) {
            LOG.error("Error trying to resolve JGitFileSystemProvider.", ex);
        }
        return null;
    }
}
