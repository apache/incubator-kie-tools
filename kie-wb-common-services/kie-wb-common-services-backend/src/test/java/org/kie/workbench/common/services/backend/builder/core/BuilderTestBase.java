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

package org.kie.workbench.common.services.backend.builder.core;

import java.io.InputStream;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.backend.server.ExtendedM2RepoService;
import org.jboss.weld.environment.se.Weld;
import org.junit.Assert;

public abstract class BuilderTestBase {

    private static final int DEFAULT_TIMEOUT_MILLIS = 5000;
    private static final int WAIT_SLICE_MILLIS = 50;

    protected BeanManager beanManager;
    protected Weld weld;

    protected void startWeld() {
        weld = new Weld(getClass().getCanonicalName());
        beanManager = weld.initialize().getBeanManager();
    }

    protected void stopWeld() {
        if (weld != null) {
            weld.shutdown();
        }
    }

    protected void setUpGuvnorM2Repo() {
        Bean m2RepoServiceBean = (Bean) beanManager.getBeans(ExtendedM2RepoService.class).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext(m2RepoServiceBean);
        ExtendedM2RepoService m2RepoService = (ExtendedM2RepoService) beanManager.getReference(m2RepoServiceBean,
                                                                                               ExtendedM2RepoService.class,
                                                                                               cc);

        String m2RepoURL = m2RepoService.getRepositoryURL();

        //Deploy a 1.0 version of guvnor-m2repo-dependency-example1-snapshot kjar
        GAV gav = new GAV("org.kie.workbench.common.services.builder.tests",
                          "dependency-test1",
                          "1.0");

        InputStream is = this.getClass().getResourceAsStream("/dependency-test1-1.0.jar");
        m2RepoService.deployJarInternal(is,
                                        gav);

        //Deploy a SNAPSHOT version of guvnor-m2repo-dependency-example1-snapshot kjar
        GAV gav2 = new GAV("org.kie.workbench.common.services.builder.tests",
                           "dependency-test1-snapshot",
                           "1.0-SNAPSHOT");

        InputStream is2 = this.getClass().getResourceAsStream("/dependency-test1-snapshot-1.0-SNAPSHOT.jar");
        m2RepoService.deployJarInternal(is2,
                                        gav2);
    }

    protected  <T> T getReference( Class<T> clazz ) {
        Bean bean = (Bean) beanManager.getBeans( clazz ).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext( bean );
        return (T) beanManager.getReference( bean,
                                             clazz,
                                             cc );
    }

    protected void waitForBuildResults(BuildResultsObserver buildResultsObserver) throws InterruptedException {
        waitForBuildResults(buildResultsObserver, DEFAULT_TIMEOUT_MILLIS);
    }

    protected void waitForBuildResults(BuildResultsObserver buildResultsObserver, int timeoutMillis) throws InterruptedException {
        int alreadyWaitedMillis = 0;

        while (alreadyWaitedMillis < timeoutMillis) {
            if (buildResultsObserver.getBuildResults() != null) {
                return;
            } else {
                Thread.sleep(WAIT_SLICE_MILLIS);
                alreadyWaitedMillis += WAIT_SLICE_MILLIS;
            }
        }
        Assert.fail("Build results not available after " + timeoutMillis + "ms!");

    }

    protected void waitForIncrementalBuildResults(BuildResultsObserver buildResultsObserver) throws InterruptedException {
        waitForIncrementalBuildResults(buildResultsObserver, DEFAULT_TIMEOUT_MILLIS);
    }

    protected void waitForIncrementalBuildResults(BuildResultsObserver buildResultsObserver, int timeoutMillis) throws InterruptedException {
        int alreadyWaitedMillis = 0;
        while (alreadyWaitedMillis < timeoutMillis) {
            if (buildResultsObserver.getIncrementalBuildResults() != null) {
                return;
            } else {
                Thread.sleep(WAIT_SLICE_MILLIS);
                alreadyWaitedMillis += WAIT_SLICE_MILLIS;
            }
        }
        Assert.fail("Incremental build results not available after " + timeoutMillis + "ms");
    }

}
