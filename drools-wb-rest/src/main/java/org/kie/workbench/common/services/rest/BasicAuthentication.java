package org.kie.workbench.common.services.rest;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.security.Subject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.StringTokenizer;

@Provider
@ServerInterceptor
@ApplicationScoped
public class BasicAuthentication implements PreProcessInterceptor {

    private static final Logger log = LoggerFactory.getLogger(BasicAuthentication.class);

/*    @Inject
    protected SecurityManager securityManager;*/

    @Override
    public ServerResponse preProcess(HttpRequest request, ResourceMethod method) throws Failure, WebApplicationException {
        if (request.getHttpHeaders().getRequestHeaders().containsKey("Authorization")) {
            String auth = request.getHttpHeaders().getRequestHeader("Authorization").get(0);
            auth = substringAfter(auth, "Basic");
            StringTokenizer tokenizer = null;
            try {
                auth = new String(Base64.decode(auth));
                tokenizer = new StringTokenizer(auth, ":");
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to parse authorization string" , e);
            }
            
            //final Subject subject = securityManager.authenticate( context );
/*            SecurityFactory.setSubject( subject );
            
            if (securityManager.login(tokenizer.nextToken(), tokenizer.nextToken())) {
                SecurityFactory.setSubject( subject );
                return null;
            }*/
        }

        return null;
/*        ServerResponse response = new ServerResponse();
        response.setStatus(HttpResponseCodes.SC_UNAUTHORIZED);
        Headers headers = new Headers();
        headers.add("Content-Type", "text/plain");
        headers.add("WWW-Authenticate", "BASIC realm=\"users\"");
        response.setMetadata(headers);
        response.setEntity("Error 401 Unauthorized: "
                + request.getPreprocessedPath());
        return response;*/
    }
    
    public static String substringAfter(String str, String separator) {
        if (str == null || str.length() == 0) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }
}
