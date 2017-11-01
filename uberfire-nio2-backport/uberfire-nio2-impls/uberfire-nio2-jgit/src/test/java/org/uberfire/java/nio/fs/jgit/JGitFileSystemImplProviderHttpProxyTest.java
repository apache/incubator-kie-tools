/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.java.nio.fs.jgit;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.junit.Test;

import static java.net.Authenticator.requestPasswordAuthentication;
import static org.junit.Assert.*;

public class JGitFileSystemImplProviderHttpProxyTest {

    @Test
    public void testHttpProxy() throws MalformedURLException, UnknownHostException {
        final String userName = "user";
        final String passw = "passwd";

        final JGitFileSystemProvider provider = new JGitFileSystemProvider(new HashMap<String, String>() {{
            put("http.proxyUser",
                "user");
            put("http.proxyPassword",
                "passwd");
            put("org.uberfire.nio.git.daemon.enabled",
                "false");
            put("org.uberfire.nio.git.ssh.enabled",
                "false");
        }});

        final PasswordAuthentication passwdAuth = requestPasswordAuthentication("localhost",
                                                                                InetAddress.getLocalHost(),
                                                                                8080,
                                                                                "http",
                                                                                "xxx",
                                                                                "http",
                                                                                new URL("http://localhost"),
                                                                                Authenticator.RequestorType.PROXY);

        assertEquals(userName,
                     passwdAuth.getUserName());
        assertEquals(passw,
                     new String(passwdAuth.getPassword()));

        provider.dispose();
    }

    @Test
    public void testHttpsProxy() throws MalformedURLException, UnknownHostException {
        final String userName = "user";
        final String passw = "passwd";

        final JGitFileSystemProvider provider = new JGitFileSystemProvider(new HashMap<String, String>() {{
            put("https.proxyUser",
                "user");
            put("https.proxyPassword",
                "passwd");
            put("org.uberfire.nio.git.daemon.enabled",
                "false");
            put("org.uberfire.nio.git.ssh.enabled",
                "false");
        }});

        final PasswordAuthentication passwdAuth = requestPasswordAuthentication("localhost",
                                                                                InetAddress.getLocalHost(),
                                                                                8080,
                                                                                "https",
                                                                                "xxx",
                                                                                "https",
                                                                                new URL("https://localhost"),
                                                                                Authenticator.RequestorType.PROXY);

        assertEquals(userName,
                     passwdAuth.getUserName());
        assertEquals(passw,
                     new String(passwdAuth.getPassword()));

        provider.dispose();
    }

    @Test
    public void testNoProxyInfo() throws MalformedURLException, UnknownHostException {
        final JGitFileSystemProvider provider = new JGitFileSystemProvider(new HashMap<String, String>() {{
            put("org.uberfire.nio.git.daemon.enabled",
                "false");
            put("org.uberfire.nio.git.ssh.enabled",
                "false");
        }});

        {
            final PasswordAuthentication passwdAuth = requestPasswordAuthentication("localhost",
                                                                                    InetAddress.getLocalHost(),
                                                                                    8080,
                                                                                    "https",
                                                                                    "xxx",
                                                                                    "https",
                                                                                    new URL("https://localhost"),
                                                                                    Authenticator.RequestorType.PROXY);

            assertNull(passwdAuth);
        }

        {
            final PasswordAuthentication passwdAuth = requestPasswordAuthentication("localhost",
                                                                                    InetAddress.getLocalHost(),
                                                                                    8080,
                                                                                    "http",
                                                                                    "xxx",
                                                                                    "http",
                                                                                    new URL("http://localhost"),
                                                                                    Authenticator.RequestorType.PROXY);

            assertNull(passwdAuth);
        }

        provider.dispose();
    }
}
