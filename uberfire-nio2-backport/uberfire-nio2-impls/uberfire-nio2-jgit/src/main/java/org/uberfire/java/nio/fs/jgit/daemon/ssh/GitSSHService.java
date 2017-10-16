/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.sshd.common.cipher.BuiltinCiphers;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.CachingPublicKeyAuthenticator;
import org.apache.sshd.server.keyprovider.AbstractGeneratorHostKeyProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.UnknownCommand;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;

import static org.apache.sshd.common.NamedFactory.setUpBuiltinFactories;
import static org.apache.sshd.server.ServerBuilder.builder;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class GitSSHService {

    private final SshServer sshd = buildSshServer();

    private FileSystemAuthenticator fileSystemAuthenticator;
    private FileSystemAuthorizer fileSystemAuthorizer;

    private static SshServer buildSshServer() {
        final List<BuiltinCiphers> ciphers =
                Collections.unmodifiableList(Arrays.asList(
                        BuiltinCiphers.aes128ctr,
                        BuiltinCiphers.aes192ctr,
                        BuiltinCiphers.aes256ctr,
                        BuiltinCiphers.arcfour256,
                        BuiltinCiphers.arcfour128,
                        BuiltinCiphers.aes192cbc,
                        BuiltinCiphers.aes256cbc
                ));

        return builder().cipherFactories(setUpBuiltinFactories(false,
                                                               ciphers)).build();
    }

    public void setup(final File certDir,
                      final InetSocketAddress inetSocketAddress,
                      final String sshIdleTimeout,
                      final String algorithm,
                      final ReceivePackFactory receivePackFactory,
                      final JGitFileSystemProvider.RepositoryResolverImpl<BaseGitCommand> repositoryResolver,
                      final ExecutorService executorService) {
        checkNotNull("certDir",
                     certDir);
        checkNotEmpty("sshIdleTimeout",
                      sshIdleTimeout);
        checkNotEmpty("algorithm",
                      algorithm);
        checkNotNull("receivePackFactory",
                     receivePackFactory);
        checkNotNull("repositoryResolver",
                     repositoryResolver);

        sshd.getProperties().put(SshServer.IDLE_TIMEOUT,
                                 sshIdleTimeout);

        if (inetSocketAddress != null) {
            sshd.setHost(inetSocketAddress.getHostName());
            sshd.setPort(inetSocketAddress.getPort());
        }

        if (!certDir.exists()) {
            certDir.mkdirs();
        }

        final AbstractGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider(new File(certDir,
                                                                                                             "hostkey.ser"));

        try {
            SecurityUtils.getKeyPairGenerator(algorithm);
            keyPairProvider.setAlgorithm(algorithm);
        } catch (final Exception ignore) {
            throw new RuntimeException(String.format("Can't use '%s' algorithm for ssh key pair generator.",
                                                     algorithm),
                                       ignore);
        }

        sshd.setKeyPairProvider(keyPairProvider);
        sshd.setCommandFactory(command -> {
            if (command.startsWith("git-upload-pack")) {
                return new GitUploadCommand(command,
                                            repositoryResolver,
                                            getAuthorizationManager(),
                                            executorService);
            } else if (command.startsWith("git-receive-pack")) {
                return new GitReceiveCommand(command,
                                             repositoryResolver,
                                             getAuthorizationManager(),
                                             receivePackFactory,
                                             executorService);
            } else {
                return new UnknownCommand(command);
            }
        });
        sshd.setPublickeyAuthenticator(new CachingPublicKeyAuthenticator((username, key, session) -> false));
        sshd.setPasswordAuthenticator((username, password, session) -> {
            final FileSystemUser user = getUserPassAuthenticator().authenticate(username,
                                                                                password);
            if (user == null) {
                return false;
            }
            session.setAttribute(BaseGitCommand.SUBJECT_KEY,
                                 user);
            return true;
        });
    }

    public void stop() {
        try {
            sshd.stop(true);
        } catch (IOException ignored) {
        }
    }

    public void start() {
        try {
            sshd.start();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't start SSH daemon at " + sshd.getHost() + ":" + sshd.getPort(),
                                       e);
        }
    }

    public boolean isRunning() {
        return !(sshd.isClosed() || sshd.isClosing());
    }

    SshServer getSshServer() {
        return sshd;
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(sshd.getProperties());
    }

    public FileSystemAuthenticator getUserPassAuthenticator() {
        return fileSystemAuthenticator;
    }

    public void setUserPassAuthenticator(FileSystemAuthenticator fileSystemAuthenticator) {
        this.fileSystemAuthenticator = fileSystemAuthenticator;
    }

    public FileSystemAuthorizer getAuthorizationManager() {
        return fileSystemAuthorizer;
    }

    public void setAuthorizationManager(FileSystemAuthorizer fileSystemAuthorizer) {
        this.fileSystemAuthorizer = fileSystemAuthorizer;
    }
}
