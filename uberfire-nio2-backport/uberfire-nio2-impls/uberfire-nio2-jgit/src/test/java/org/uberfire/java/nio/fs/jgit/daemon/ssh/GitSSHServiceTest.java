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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.cipher.BuiltinCiphers;
import org.apache.sshd.common.cipher.Cipher;
import org.apache.sshd.common.mac.BuiltinMacs;
import org.apache.sshd.common.mac.Mac;
import org.apache.sshd.server.SshServer;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.UploadPackFactory;
import org.eclipse.jgit.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.commons.concurrent.ExecutorServiceProducer;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GitSSHServiceTest {

    private static final List<File> tempFiles = new ArrayList<>();

    private final ExecutorService executorService = new ExecutorServiceProducer().produceUnmanagedExecutorService();

    protected static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile("temp",
                                              Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        tempFiles.add(temp);

        return temp;
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            try {
                FileUtils.delete(tempFile,
                                 FileUtils.RECURSIVE);
            } catch (IOException ignore) {
            }
        }
    }

    @Test
    public void testStartStop() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        sshService.setup(certDir,
                         null,
                         "10000",
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService);

        sshService.start();
        assertTrue(sshService.isRunning());

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testStartStopAlgo2() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        sshService.setup(certDir,
                         null,
                         "10000",
                         "DSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService);

        sshService.start();
        assertTrue(sshService.isRunning());

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testCheckTimeout() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        String idleTimeout = "10000";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService);

        sshService.start();
        assertTrue(sshService.isRunning());

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testCheckAlgo() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        try {
            sshService.setup(certDir,
                             null,
                             "10000",
                             "xxxx",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (final Exception ex) {
            assertThat(ex.getMessage()).contains("'xxxx'");
        }
    }

    @Test
    public void testCheckSetupParameters() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        try {
            sshService.setup(null,
                             null,
                             "10000",
                             "RSA",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'certDir'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             null,
                             "RSA",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'sshIdleTimeout'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             "",
                             "RSA",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'sshIdleTimeout'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             "1000",
                             null,
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'algorithm'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             "1000",
                             "",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'algorithm'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             "100",
                             "RSA",
                             null,
                             null,
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'receivePackFactory'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             "100",
                             "RSA",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             null,
                             executorService);
            fail("has to fail");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("'repositoryResolver'");
        }

        try {
            sshService.setup(certDir,
                             null,
                             "10000",
                             "RSA",
                             mock(ReceivePackFactory.class),
                             mock(UploadPackFactory.class),
                             mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                             executorService);
        } catch (IllegalArgumentException ex) {
            fail("should not fail");
        }
    }

    @Test
    public void testCheckCiphersAndMacs() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        String idleTimeout = "10000";
        String ciphers = "aes128-cbc,aes128-ctr,aes192-cbc,aes192-ctr,aes256-cbc,aes256-ctr,arcfour128,arcfour256,blowfish-cbc,3des-cbc";
        String macs = "hmac-md5, hmac-md5-96, hmac-sha1, hmac-sha1-96, hmac-sha2-256, hmac-sha2-512";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService,
                         ciphers,
                         macs);

        sshService.start();
        assertTrue(sshService.isRunning());

        List<String> ciphersReaded = sshService.getSshServer().getCipherFactoriesNames();
        List<String> macsReaded = sshService.getSshServer().getMacFactoriesNames();

        assertThat(ciphersReaded).hasSize(7);
        checkCiphersName(ciphersReaded);

        assertThat(macsReaded).hasSize(6);
        checkMacsName(macsReaded);

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testCheckEmptyCiphers() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();


        String idleTimeout = "10000";
        String macs = "hmac-md5, hmac-md5-96, hmac-sha1, hmac-sha1-96, hmac-sha2-256, hmac-sha2-512";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService,
                         "",
                         macs);

        sshService.start();
        assertTrue(sshService.isRunning());

        List<String> ciphersReaded = sshService.getSshServer().getCipherFactoriesNames();
        List<String> macsReaded = sshService.getSshServer().getMacFactoriesNames();

        assertThat(ciphersReaded).hasSize(7);
        checkCiphersName(ciphersReaded);

        assertThat(macsReaded).hasSize(6);
        checkMacsName(macsReaded);

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testCheckEmptyMacs() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        String idleTimeout = "10000";

        String ciphers = "aes128-cbc,aes128-ctr,aes192-cbc,aes192-ctr,aes256-cbc,aes256-ctr,arcfour128,arcfour256,blowfish-cbc,3des-cbc";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService,
                         ciphers,
                         "");

        sshService.start();
        assertTrue(sshService.isRunning());

        List<String> ciphersReaded = sshService.getSshServer().getCipherFactoriesNames();
        List<String> macsReaded = sshService.getSshServer().getMacFactoriesNames();

        assertThat(ciphersReaded).hasSize(7);
        checkCiphersName(ciphersReaded);

        assertThat(macsReaded).hasSize(6);
        checkMacsName(macsReaded);

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testCheckEmptyCiphersAndMacs() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        String idleTimeout = "10000";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService,
                         "",
                         "");

        sshService.start();
        assertTrue(sshService.isRunning());

        List<String> ciphersReaded = sshService.getSshServer().getCipherFactoriesNames();
        List<String> macsReaded = sshService.getSshServer().getMacFactoriesNames();

        assertThat(ciphersReaded).hasSize(7);
        checkCiphersName(ciphersReaded);

        assertThat(macsReaded).hasSize(6);
        checkMacsName(macsReaded);

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testCheckNullCiphersAndMacs() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        String idleTimeout = "10000";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService,
                         null,
                         null);

        sshService.start();
        assertTrue(sshService.isRunning());

        List<String> ciphersReaded = sshService.getSshServer().getCipherFactoriesNames();
        List<String> macsReaded = sshService.getSshServer().getMacFactoriesNames();

        assertThat(ciphersReaded).hasSize(7);
        checkCiphersName(ciphersReaded);

        assertThat(macsReaded).hasSize(6);
        checkMacsName(macsReaded);

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    @Test
    public void testWithWrongCiphersAndMacs() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        String idleTimeout = "10000";
        String ciphers = "aes126-cbc,aes124-ctr,aes192-cbc,aes192-ctr,aes255-cbc,aes256-ctr,arcfour128,arcfour256,blowfish-cbc,3des-cbc";
        sshService.setup(certDir,
                         null,
                         idleTimeout,
                         "RSA",
                         mock(ReceivePackFactory.class),
                         mock(UploadPackFactory.class),
                         mock(JGitFileSystemProvider.RepositoryResolverImpl.class),
                         executorService,
                         ciphers,
                         "");

        sshService.start();
        assertTrue(sshService.isRunning());

        List<String> ciphersReaded = sshService.getSshServer().getCipherFactoriesNames();
        List<String> macsReaded = sshService.getSshServer().getMacFactoriesNames();

        assertThat(ciphersReaded).hasSize(5);
        checkCiphersName(ciphersReaded);

        assertThat(macsReaded).hasSize(6);
        checkMacsName(macsReaded);

        assertThat(sshService.getSshServer().getProperties().get(SshServer.IDLE_TIMEOUT)).isEqualTo(idleTimeout);

        sshService.stop();

        assertFalse(sshService.isRunning());
    }

    private void checkCiphersName(List<String> ciphersReaded){
        for(String cipher : ciphersReaded){
            assertThat(BuiltinCiphers.fromFactoryName(cipher)).isNotNull();
        }
    }

    private void checkMacsName(List<String> macsReaded){
        for(String mac : macsReaded){
            assertThat(BuiltinMacs.fromFactoryName(mac)).isNotNull();
        }
    }

}
