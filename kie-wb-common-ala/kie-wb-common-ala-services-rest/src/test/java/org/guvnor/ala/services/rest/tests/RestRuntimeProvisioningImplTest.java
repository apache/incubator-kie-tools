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

package org.guvnor.ala.services.rest.tests;

import javax.inject.Inject;

import com.spotify.docker.client.DockerException;
import org.apache.commons.lang.SystemUtils;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.executor.DockerProviderConfigExecutor;
import org.guvnor.ala.docker.executor.DockerRuntimeExecExecutor;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerProviderType;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.service.DockerRuntimeManager;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.pipeline.execution.impl.PipelineExecutorTaskManagerImpl;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineExecutorRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryPipelineRegistry;
import org.guvnor.ala.registry.inmemory.InMemoryRuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeManager;
import org.guvnor.ala.runtime.RuntimeState;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.RuntimeQuery;
import org.guvnor.ala.services.api.RuntimeQueryBuilder;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.services.api.itemlist.RuntimeQueryResultItemList;
import org.guvnor.ala.services.rest.RestRuntimeProvisioningServiceImpl;
import org.guvnor.ala.services.rest.factories.ProviderFactory;
import org.guvnor.ala.services.rest.factories.RuntimeFactory;
import org.guvnor.ala.services.rest.factories.RuntimeManagerFactory;
import org.guvnor.ala.wildfly.executor.WildflyProviderConfigExecutor;
import org.guvnor.ala.wildfly.model.WildflyProviderType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.lifecycle.Disposable;

import static org.junit.Assert.*;

/**
 * Test that shows how to work with the Runtime Provisioning Service
 */
@RunWith(Arquillian.class)
public class RestRuntimeProvisioningImplTest {

    @Inject
    private RuntimeProvisioningService runtimeService;

    @Deployment()
    public static Archive createDeployment() throws Exception {
        JavaArchive deployment = ShrinkWrap.create(JavaArchive.class);
        deployment.addClass(DockerProviderConfigExecutor.class);
        deployment.addClass(WildflyProviderConfigExecutor.class);
        deployment.addClass(RestRuntimeProvisioningServiceImpl.class);
        deployment.addClass(RuntimeRegistry.class);
        deployment.addClass(InMemoryRuntimeRegistry.class);
        deployment.addClass(RuntimeRegistry.class);
        deployment.addClass(ProviderFactory.class);
        deployment.addClass(RuntimeFactory.class);
        deployment.addClass(RuntimeManagerFactory.class);
        deployment.addClass(DockerProviderType.class);
        deployment.addClass(DockerProviderConfig.class);
        deployment.addClass(DockerProvider.class);
        deployment.addClass(WildflyProviderType.class);
        deployment.addClass(ProviderBuilder.class);
        deployment.addClass(ProviderType.class);
        deployment.addClass(FunctionConfigExecutor.class);
        deployment.addClass(ConfigExecutor.class);
        deployment.addClass(ProviderConfig.class);
        deployment.addClass(Provider.class);
        deployment.addClass(DockerRuntimeConfig.class);
        deployment.addClass(DockerRuntime.class);
        deployment.addClass(RuntimeBuilder.class);
        deployment.addClass(DockerRuntimeExecExecutor.class);
        deployment.addClass(RuntimeDestroyer.class);
        deployment.addClass(DockerAccessInterface.class);
        deployment.addClass(DockerAccessInterfaceImpl.class);
        deployment.addClass(Disposable.class);
        deployment.addClass(DockerException.class);
        deployment.addClass(DockerRuntimeManager.class);
        deployment.addClass(RuntimeManager.class);

        deployment.addClass(org.guvnor.ala.config.Config.class);
        deployment.addClass(org.guvnor.ala.config.ProviderConfig.class);
        deployment.addClass(org.guvnor.ala.docker.config.DockerProviderConfig.class);
        deployment.addClass(org.guvnor.ala.docker.model.DockerProvider.class);
        deployment.addClass(org.guvnor.ala.pipeline.FunctionConfigExecutor.class);
        deployment.addClass(org.guvnor.ala.registry.RuntimeRegistry.class);
        deployment.addClass(org.guvnor.ala.runtime.providers.ProviderBuilder.class);
        deployment.addClass(org.guvnor.ala.runtime.providers.ProviderDestroyer.class);
        deployment.addClass(org.guvnor.ala.runtime.providers.ProviderId.class);

        deployment.addClass(PipelineExecutorTaskManagerImpl.class);
        deployment.addClass(InMemoryPipelineExecutorRegistry.class);
        deployment.addClass(InMemoryPipelineRegistry.class);

        deployment.addAsManifestResource(EmptyAsset.INSTANCE,
                                         "beans.xml");
        return deployment;
    }

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
    }

    @Ignore("refer https://issues.redhat.com/browse/AF-2469")
    @Test
    public void testAPI() {

        ProviderTypeList allProviderTypes = runtimeService.getProviderTypes(0,
                                                                            10,
                                                                            "",
                                                                            true);

        assertEquals(2,
                     allProviderTypes.getItems().size());
        DockerProviderConfig dockerProviderConfig = new DockerProviderConfig() {
        };
        runtimeService.registerProvider(dockerProviderConfig);

        ProviderList allProviders = runtimeService.getProviders(0,
                                                                10,
                                                                "",
                                                                true);

        assertEquals(1,
                     allProviders.getItems().size());

        Provider p = allProviders.getItems().get(0);
        assertTrue(p instanceof DockerProvider);

        assertNotNull(p.getId());
        assertNotNull(p.getProviderType());
        assertNotNull(p.getConfig());

        DockerRuntimeConfig dockerRuntimeConfiguration = new DockerRuntimeConfig() {
            @Override
            public String getImage() {
                return "kitematic/hello-world-nginx";
            }

            @Override
            public String getPort() {
                return "8080";
            }

            @Override
            public boolean isPull() {
                return true;
            }

            @Override
            public ProviderId getProviderId() {
                return p;
            }
        };

        String newRuntime = runtimeService.newRuntime(dockerRuntimeConfiguration);
        assertNotNull(newRuntime);

        RuntimeList allRuntimes = runtimeService.getRuntimes(0,
                                                             10,
                                                             "",
                                                             true);
        assertEquals(1,
                     allRuntimes.getItems().size());

        RuntimeQuery query = RuntimeQueryBuilder.newInstance().withProviderId(p.getId()).build();
        RuntimeQueryResultItemList queryResult = runtimeService.executeQuery(query);
        assertEquals(1,
                     queryResult.getItems().size());
        assertEquals(RuntimeState.RUNNING,
                     queryResult.getItems().get(0).getRuntimeStatus());

        runtimeService.destroyRuntime(newRuntime,
                                      true);

        allRuntimes = runtimeService.getRuntimes(0,
                                                 10,
                                                 "",
                                                 true);
        assertEquals(0,
                     allRuntimes.getItems().size());
    }
}
