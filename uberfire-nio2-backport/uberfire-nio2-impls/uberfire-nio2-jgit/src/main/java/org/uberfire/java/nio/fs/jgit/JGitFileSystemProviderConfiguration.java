/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.config.ConfigProperties;

import static java.util.stream.Collectors.joining;
import static org.eclipse.jgit.lib.Constants.DEFAULT_REMOTE_NAME;

public class JGitFileSystemProviderConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(JGitFileSystemProviderConfiguration.class);

    public static final String GIT_ENV_KEY_DEFAULT_REMOTE_NAME = DEFAULT_REMOTE_NAME;
    public static final String GIT_ENV_KEY_BRANCH_LIST = "branches";
    public static final String GIT_ENV_KEY_SUBDIRECTORY = "subdirectory";

    public static final String GIT_DAEMON_ENABLED = "org.uberfire.nio.git.daemon.enabled";
    public static final String GIT_SSH_ENABLED = "org.uberfire.nio.git.ssh.enabled";
    public static final String GIT_NIO_DIR = "org.uberfire.nio.git.dir";
    public static final String GIT_NIO_DIR_NAME = "org.uberfire.nio.git.dirname";
    public static final String ENABLE_GIT_KETCH = "org.uberfire.nio.git.ketch";
    public static final String HOOK_DIR = "org.uberfire.nio.git.hooks";
    public static final String GIT_DAEMON_HOST = "org.uberfire.nio.git.daemon.host";
    public static final String GIT_DAEMON_HOSTNAME = "org.uberfire.nio.git.daemon.hostname";
    public static final String GIT_DAEMON_PORT = "org.uberfire.nio.git.daemon.port";
    public static final String GIT_SSH_HOST = "org.uberfire.nio.git.ssh.host";
    public static final String GIT_SSH_HOSTNAME = "org.uberfire.nio.git.ssh.hostname";
    public static final String GIT_SSH_PORT = "org.uberfire.nio.git.ssh.port";
    public static final String GIT_SSH_CERT_DIR = "org.uberfire.nio.git.ssh.cert.dir";
    public static final String GIT_SSH_IDLE_TIMEOUT = "org.uberfire.nio.git.ssh.idle.timeout";
    public static final String GIT_SSH_ALGORITHM = "org.uberfire.nio.git.ssh.algorithm";
    public static final String GIT_SSH_PASSPHRASE = "org.uberfire.nio.git.ssh.passphrase";
    public static final String GIT_GC_LIMIT = "org.uberfire.nio.git.gc.limit";
    public static final String GIT_HTTP_SSL_VERIFY ="org.uberfire.nio.git.http.sslVerify";
    public static final String SSH_OVER_HTTP = "org.uberfire.nio.git.proxy.ssh.over.http";
    public static final String HTTP_PROXY_HOST = "http.proxyHost";
    public static final String HTTP_PROXY_PORT = "http.proxyPort";
    public static final String HTTP_PROXY_USER = "http.proxyUser";
    public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
    public static final String SSH_OVER_HTTPS = "org.uberfire.nio.git.proxy.ssh.over.https";
    public static final String HTTPS_PROXY_HOST = "https.proxyHost";
    public static final String HTTPS_PROXY_PORT = "https.proxyPort";
    public static final String HTTPS_PROXY_USER = "https.proxyUser";
    public static final String HTTPS_PROXY_PASSWORD = "https.proxyPassword";
    public static final String USER_DIR = "user.dir";
    public static final String JGIT_CACHE_INSTANCES = "org.uberfire.nio.jgit.cache.instances";
    public static final String JGIT_CACHE_OVERFLOW_CLEANUP_SIZE = "org.uberfire.nio.jgit.cache.overflow.cleanup.size";
    public static final String JGIT_REMOVE_ELDEST_ENTRY_ITERATIONS = "org.uberfire.nio.jgit.remove.eldest.iterations";
    public static final String JGIT_CACHE_EVICT_THRESHOLD_DURATION = "org.uberfire.nio.jgit.cache.evict.threshold.duration";
    public static final String JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT = "org.uberfire.nio.jgit.cache.evict.threshold.time.unit";

    public static final String GIT_ENV_KEY_DEST_PATH = "out-dir";
    public static final String GIT_ENV_KEY_USER_NAME = "username";
    public static final String GIT_ENV_KEY_PASSWORD = "password";
    public static final String GIT_ENV_KEY_INIT = "init";
    public static final String GIT_ENV_KEY_MIRROR = "mirror";
    public static final String SCHEME = "git";
    public static final int SCHEME_SIZE = (SCHEME + "://").length();
    public static final int DEFAULT_SCHEME_SIZE = ("default://").length();
    public static final String DEFAULT_HOST_NAME = "localhost";
    public static final String REPOSITORIES_CONTAINER_DIR = ".niogit";
    public static final String SSH_FILE_CERT_CONTAINER_DIR = ".security";
    public static final String DEFAULT_SSH_OVER_HTTP = "false";
    public static final String DEFAULT_SSH_OVER_HTTPS = "false";
    public static final String DEFAULT_HOST_ADDR = "127.0.0.1";
    public static final String DEFAULT_DAEMON_DEFAULT_ENABLED = "true";
    public static final String DEFAULT_DAEMON_DEFAULT_PORT = "9418";
    public static final String DEFAULT_SSH_ENABLED = "true";
    public static final String DEFAULT_SSH_PORT = "8001";
    public static final String DEFAULT_SSH_IDLE_TIMEOUT = "10000";
    public static final String DEFAULT_SSH_ALGORITHM = "RSA";
    public static final String DEFAULT_SSH_CERT_PASSPHRASE = "";
    public static final String DEFAULT_COMMIT_LIMIT_TO_GC = "20";
    public static final Boolean DEFAULT_GIT_HTTP_SSL_VERIFY = Boolean.TRUE;
    public static final String DEFAULT_GIT_ENV_KEY_MIGRATE_FROM = "migrate-from";
    public static final String DEFAULT_ENABLE_GIT_KETCH = "false";
    public static final String DEFAULT_JGIT_FILE_SYSTEM_INSTANCES_CACHE = "10000";
    public static final String DEFAULT_JGIT_REMOVE_ELDEST_ENTRY_ITERATIONS = "10";
    public static final String DEFAULT_JGIT_CACHE_OVERFLOW_CLEANUP_SIZE = "10";
    public static final String DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_DURATION = "5";
    public static final TimeUnit DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT = TimeUnit.MINUTES;

    private int commitLimit;
    private boolean sslVerify;
    private boolean daemonEnabled;
    private int daemonPort;
    private String daemonHostAddr;
    private String daemonHostName;

    private boolean sshEnabled;
    private int sshPort;
    private String sshHostAddr;
    private String sshHostName;
    private File sshFileCertDir;
    private String sshAlgorithm;
    private String sshPassphrase;
    private String sshIdleTimeout;

    private File gitReposParentDir;

    private File hookDir;

    boolean enableKetch = false;
    private boolean sshOverHttpProxy;
    private String httpProxyHost;
    private int httpProxyPort;
    private String httpProxyUser;
    private String httpProxyPassword;
    private boolean sshOverHttpsProxy;
    private String httpsProxyHost;
    private int httpsProxyPort;
    private String httpsProxyUser;
    private String httpsProxyPassword;

    //Number of instances of filesystem in cache
    private int jgitFileSystemsInstancesCache;
    //Number of instances that was removed by iteration in case of cache overflow
    private int jgitCacheOverflowCleanupSize;
    //Number of attempts to remove FS instances on cache
    private int jgitRemoveEldestEntryIterations;
    //Duration of Threshold of jgit file system instances evict
    private long jgitCacheEvictThresholdDuration;
    //TimeUnit of Threshold of jgit file system instances evict
    private TimeUnit jgitCacheEvictThresholdTimeUnit;

    public void load(ConfigProperties systemConfig) {
        LOG.debug("Configuring from properties:");

        final String currentDirectory = System.getProperty(USER_DIR);
        final ConfigProperties.ConfigProperty enableKetchProp = systemConfig.get(ENABLE_GIT_KETCH,
                                                                                 DEFAULT_ENABLE_GIT_KETCH);
        final ConfigProperties.ConfigProperty hookDirProp = systemConfig.get(HOOK_DIR,
                                                                             null);
        final ConfigProperties.ConfigProperty bareReposDirProp = systemConfig.get(GIT_NIO_DIR,
                                                                                  currentDirectory);
        final ConfigProperties.ConfigProperty reposDirNameProp = systemConfig.get(GIT_NIO_DIR_NAME,
                                                                                  REPOSITORIES_CONTAINER_DIR);
        final ConfigProperties.ConfigProperty enabledProp = systemConfig.get(GIT_DAEMON_ENABLED,
                                                                             DEFAULT_DAEMON_DEFAULT_ENABLED);
        final ConfigProperties.ConfigProperty hostProp = systemConfig.get(GIT_DAEMON_HOST,
                                                                          DEFAULT_HOST_ADDR);
        final ConfigProperties.ConfigProperty hostNameProp = systemConfig.get(GIT_DAEMON_HOSTNAME,
                                                                              hostProp.isDefault() ? DEFAULT_HOST_NAME : hostProp.getValue());
        final ConfigProperties.ConfigProperty portProp = systemConfig.get(GIT_DAEMON_PORT,
                                                                          DEFAULT_DAEMON_DEFAULT_PORT);
        final ConfigProperties.ConfigProperty sshEnabledProp = systemConfig.get(GIT_SSH_ENABLED,
                                                                                DEFAULT_SSH_ENABLED);
        final ConfigProperties.ConfigProperty sshHostProp = systemConfig.get(GIT_SSH_HOST,
                                                                             DEFAULT_HOST_ADDR);
        final ConfigProperties.ConfigProperty sshHostNameProp = systemConfig.get(GIT_SSH_HOSTNAME,
                                                                                 sshHostProp.isDefault() ? DEFAULT_HOST_NAME : sshHostProp.getValue());
        final ConfigProperties.ConfigProperty sshPortProp = systemConfig.get(GIT_SSH_PORT,
                                                                             DEFAULT_SSH_PORT);
        final ConfigProperties.ConfigProperty sshCertDirProp = systemConfig.get(GIT_SSH_CERT_DIR,
                                                                                currentDirectory);
        final ConfigProperties.ConfigProperty sshIdleTimeoutProp = systemConfig.get(GIT_SSH_IDLE_TIMEOUT,
                                                                                    DEFAULT_SSH_IDLE_TIMEOUT);
        final ConfigProperties.ConfigProperty sshAlgorithmProp = systemConfig.get(GIT_SSH_ALGORITHM,
                                                                                  DEFAULT_SSH_ALGORITHM);
        final ConfigProperties.ConfigProperty sshPassphraseProp = systemConfig.get(GIT_SSH_PASSPHRASE,
                                                                                   DEFAULT_SSH_CERT_PASSPHRASE);
        final ConfigProperties.ConfigProperty commitLimitProp = systemConfig.get(GIT_GC_LIMIT,
                                                                                 DEFAULT_COMMIT_LIMIT_TO_GC);
        final ConfigProperties.ConfigProperty sslVerifyProp = systemConfig.get(GIT_HTTP_SSL_VERIFY,
                                                                               DEFAULT_GIT_HTTP_SSL_VERIFY.toString());
        final ConfigProperties.ConfigProperty sshOverHttpProxyProp = systemConfig.get(SSH_OVER_HTTP,
                                                                                      DEFAULT_SSH_OVER_HTTP);
        final ConfigProperties.ConfigProperty httpProxyHostProp = systemConfig.get(HTTP_PROXY_HOST,
                                                                                   null);
        final ConfigProperties.ConfigProperty httpProxyPortProp = systemConfig.get(HTTP_PROXY_PORT,
                                                                                   null);
        final ConfigProperties.ConfigProperty httpProxyUserProp = systemConfig.get(HTTP_PROXY_USER,
                                                                                   null);
        final ConfigProperties.ConfigProperty httpProxyPasswordProp = systemConfig.get(HTTP_PROXY_PASSWORD,
                                                                                       null);
        final ConfigProperties.ConfigProperty sshOverHttpsProxyProp = systemConfig.get(SSH_OVER_HTTPS,
                                                                                       DEFAULT_SSH_OVER_HTTPS);
        final ConfigProperties.ConfigProperty httpsProxyHostProp = systemConfig.get(HTTPS_PROXY_HOST,
                                                                                    null);
        final ConfigProperties.ConfigProperty httpsProxyPortProp = systemConfig.get(HTTPS_PROXY_PORT,
                                                                                    null);
        final ConfigProperties.ConfigProperty httpsProxyUserProp = systemConfig.get(HTTPS_PROXY_USER,
                                                                                    null);
        final ConfigProperties.ConfigProperty httpsProxyPasswordProp = systemConfig.get(HTTPS_PROXY_PASSWORD,
                                                                                        null);

        final ConfigProperties.ConfigProperty jgitFileSystemsInstancesCacheProp = systemConfig.get(JGIT_CACHE_INSTANCES,
                                                                                                   DEFAULT_JGIT_FILE_SYSTEM_INSTANCES_CACHE);

        final ConfigProperties.ConfigProperty jgitFileSystemsCacheOverflowSizePropCacheProp = systemConfig.get(JGIT_CACHE_OVERFLOW_CLEANUP_SIZE,
                                                                                                               DEFAULT_JGIT_CACHE_OVERFLOW_CLEANUP_SIZE);

        final ConfigProperties.ConfigProperty jgitRemoveEldestEntryIterationsProp = systemConfig.get(JGIT_REMOVE_ELDEST_ENTRY_ITERATIONS,
                                                                                                     DEFAULT_JGIT_REMOVE_ELDEST_ENTRY_ITERATIONS);

        final ConfigProperties.ConfigProperty jgitCacheEvictThresoldDurationProp = systemConfig.get(JGIT_CACHE_EVICT_THRESHOLD_DURATION,
                                                                                                    DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_DURATION);

        final ConfigProperties.ConfigProperty jgitCacheEvictThresoldTimeUnitProp = systemConfig.get(JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT,
                                                                                                    DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT.name());

        sshOverHttpProxy = sshOverHttpProxyProp.getBooleanValue();
        if (sshOverHttpProxy) {
            httpProxyHost = httpProxyHostProp.getValue();
            httpProxyPort = httpProxyPortProp.getIntValue();
        }
        httpProxyUser = httpProxyUserProp.getValue();
        httpProxyPassword = httpProxyPasswordProp.getValue();
        sshOverHttpsProxy = sshOverHttpsProxyProp.getBooleanValue();
        if (sshOverHttpsProxy) {
            httpsProxyHost = httpsProxyHostProp.getValue();
            httpsProxyPort = httpsProxyPortProp.getIntValue();
        }
        httpsProxyUser = httpsProxyUserProp.getValue();
        httpsProxyPassword = httpsProxyPasswordProp.getValue();

        if (LOG.isDebugEnabled()) {
            LOG.debug(systemConfig.getConfigurationSummary("Summary of JGit configuration:"));
        }

        if (enableKetchProp != null && enableKetchProp.getValue() != null) {
            enableKetch = enableKetchProp.getBooleanValue();
        }

        if (hookDirProp != null && hookDirProp.getValue() != null) {
            hookDir = new File(hookDirProp.getValue());
            if (!hookDir.exists()) {
                hookDir = null;
            }
        }

        gitReposParentDir = new File(bareReposDirProp.getValue(),
                                     reposDirNameProp.getValue());
        commitLimit = commitLimitProp.getIntValue();
        sslVerify = sslVerifyProp.getBooleanValue();

        jgitFileSystemsInstancesCache = jgitFileSystemsInstancesCacheProp.getIntValue();

        if (jgitFileSystemsInstancesCache < 1) {
            jgitFileSystemsInstancesCache = Integer.valueOf(DEFAULT_JGIT_FILE_SYSTEM_INSTANCES_CACHE);
        }

        jgitCacheOverflowCleanupSize = jgitFileSystemsCacheOverflowSizePropCacheProp.getIntValue();

        if (jgitCacheOverflowCleanupSize < 1) {
            jgitCacheOverflowCleanupSize = Integer.valueOf(DEFAULT_JGIT_CACHE_OVERFLOW_CLEANUP_SIZE);
        }

        jgitRemoveEldestEntryIterations = jgitRemoveEldestEntryIterationsProp.getIntValue();
        if (jgitRemoveEldestEntryIterations < 1) {
            jgitRemoveEldestEntryIterations = Integer.valueOf(DEFAULT_JGIT_REMOVE_ELDEST_ENTRY_ITERATIONS);
        }

        jgitCacheEvictThresholdDuration = Long.valueOf(jgitCacheEvictThresoldDurationProp.getValue());
        if (jgitCacheEvictThresholdDuration < 1) {
            jgitCacheEvictThresholdDuration = Integer.valueOf(DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_DURATION);
        }

        try {
            jgitCacheEvictThresholdTimeUnit = TimeUnit.valueOf(jgitCacheEvictThresoldTimeUnitProp.getValue());
        } catch (IllegalArgumentException e) {
            String validValues = Stream.of(TimeUnit.values()).map(Enum::toString).collect(joining(","));
            LOG.warn("Failed to parse TimeUnit from {}={}. Valid values are {}. Using default instead: {}",
                     JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT,
                     jgitCacheEvictThresholdTimeUnit,
                     validValues,
                     DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT);
            jgitCacheEvictThresholdTimeUnit = DEFAULT_JGIT_CACHE_EVICT_THRESHOLD_TIME_UNIT;
        }

        daemonEnabled = enabledProp.getBooleanValue();
        if (daemonEnabled) {
            daemonPort = portProp.getIntValue();
            daemonHostAddr = hostProp.getValue();
            daemonHostName = hostNameProp.getValue();
        }

        sshEnabled = sshEnabledProp.getBooleanValue();
        if (sshEnabled) {
            sshPort = sshPortProp.getIntValue();
            sshHostAddr = sshHostProp.getValue();
            sshHostName = sshHostNameProp.getValue();
            sshFileCertDir = new File(sshCertDirProp.getValue(),
                                      SSH_FILE_CERT_CONTAINER_DIR);
            sshAlgorithm = sshAlgorithmProp.getValue();
            sshIdleTimeout = sshIdleTimeoutProp.getValue();
            try {
                Integer.valueOf(sshIdleTimeout);
            } catch (final NumberFormatException exception) {
                LOG.error("SSH Idle Timeout value is not a valid integer - Parameter is ignored, now using default value.");
                sshIdleTimeout = DEFAULT_SSH_IDLE_TIMEOUT;
            }
        }
        sshPassphrase = sshPassphraseProp.getValue();
    }

    public boolean httpProxyIsDefined() {
        return (httpProxyUser != null &&
                httpProxyPassword != null) ||
                (httpsProxyUser != null &&
                        httpsProxyPassword != null);
    }

    public int getCommitLimit() {
        return commitLimit;
    }

    public boolean isSslVerify() {
        return sslVerify;
    }

    public boolean isDaemonEnabled() {
        return daemonEnabled;
    }

    public int getDaemonPort() {
        return daemonPort;
    }

    public String getDaemonHostAddr() {
        return daemonHostAddr;
    }

    public String getDaemonHostName() {
        return daemonHostName;
    }

    public boolean isSshEnabled() {
        return sshEnabled;
    }

    public int getSshPort() {
        return sshPort;
    }

    public String getSshHostAddr() {
        return sshHostAddr;
    }

    public String getSshHostName() {
        return sshHostName;
    }

    public File getSshFileCertDir() {
        return sshFileCertDir;
    }

    public String getSshAlgorithm() {
        return sshAlgorithm;
    }

    public String getSshPassphrase() {
        return sshPassphrase;
    }

    public String getSshIdleTimeout() {
        return sshIdleTimeout;
    }

    public File getGitReposParentDir() {
        return gitReposParentDir;
    }

    public File getHookDir() {
        return hookDir;
    }

    public boolean isEnableKetch() {
        return enableKetch;
    }

    public boolean isSshOverHttpProxy() {
        return sshOverHttpProxy;
    }

    public String getHttpProxyHost() {
        return httpProxyHost;
    }

    public int getHttpProxyPort() {
        return httpProxyPort;
    }

    public String getHttpProxyUser() {
        return httpProxyUser;
    }

    public String getHttpProxyPassword() {
        return httpProxyPassword;
    }

    public boolean isSshOverHttpsProxy() {
        return sshOverHttpsProxy;
    }

    public String getHttpsProxyHost() {
        return httpsProxyHost;
    }

    public int getHttpsProxyPort() {
        return httpsProxyPort;
    }

    public String getHttpsProxyUser() {
        return httpsProxyUser;
    }

    public String getHttpsProxyPassword() {
        return httpsProxyPassword;
    }

    public int getJgitFileSystemsInstancesCache() {
        return jgitFileSystemsInstancesCache;
    }

    public int getJgitCacheOverflowCleanupSize() {
        return jgitCacheOverflowCleanupSize;
    }

    public int getJgitRemoveEldestEntryIterations() {
        return jgitRemoveEldestEntryIterations;
    }

    public TimeUnit getDefaultJgitCacheEvictThresholdTimeUnit() {
        return jgitCacheEvictThresholdTimeUnit;
    }

    public long getJgitCacheEvictThresholdDuration() {
        return jgitCacheEvictThresholdDuration;
    }

}
