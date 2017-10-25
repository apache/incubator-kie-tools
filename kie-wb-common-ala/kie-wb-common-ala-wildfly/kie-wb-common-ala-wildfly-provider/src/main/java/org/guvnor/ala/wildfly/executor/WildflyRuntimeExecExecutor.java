package org.guvnor.ala.wildfly.executor;

import java.io.File;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.WildflyAppState;
import org.guvnor.ala.wildfly.config.WildflyRuntimeConfiguration;
import org.guvnor.ala.wildfly.config.WildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.model.WildflyRuntimeEndpoint;
import org.guvnor.ala.wildfly.model.WildflyRuntimeInfo;
import org.guvnor.ala.wildfly.model.WildflyRuntimeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.guvnor.ala.runtime.RuntimeState.RUNNING;
import static org.guvnor.ala.runtime.RuntimeState.STOPPED;
import static org.guvnor.ala.runtime.RuntimeState.UNKNOWN;
import static org.guvnor.ala.util.RuntimeConfigHelper.buildRuntimeName;

public class WildflyRuntimeExecExecutor<T extends WildflyRuntimeConfiguration>
        implements RuntimeBuilder<T, WildflyRuntime>,
                   RuntimeDestroyer,
                   FunctionConfigExecutor<T, WildflyRuntime> {

    private final RuntimeRegistry runtimeRegistry;
    private final WildflyAccessInterface wildfly;
    protected static final Logger LOG = LoggerFactory.getLogger(WildflyRuntimeExecExecutor.class);

    @Inject
    public WildflyRuntimeExecExecutor(final RuntimeRegistry runtimeRegistry,
                                      final WildflyAccessInterface wildfly) {
        this.runtimeRegistry = runtimeRegistry;
        this.wildfly = wildfly;
    }

    @Override
    public Optional<WildflyRuntime> apply(final WildflyRuntimeConfiguration config) {
        final Optional<WildflyRuntime> runtime = create(config);
        if (runtime.isPresent()) {
            runtimeRegistry.registerRuntime(runtime.get());
        }
        return runtime;
    }

    private Optional<WildflyRuntime> create(final WildflyRuntimeConfiguration runtimeConfig) throws ProvisioningException {

        String warPath = runtimeConfig.getWarPath();
        final Optional<WildflyProvider> _wildflyProvider = runtimeRegistry.getProvider(runtimeConfig.getProviderId(),
                                                                                       WildflyProvider.class);
        if (!_wildflyProvider.isPresent()) {
            throw new ProvisioningException("No Wildfly provider was found for providerId: " + runtimeConfig.getProviderId());
        }

        WildflyProvider wildflyProvider = _wildflyProvider.get();
        File file = new File(warPath);
        final String id = file.getName();

        WildflyAppState appState = wildfly.getWildflyClient(wildflyProvider).getAppState(id);
        if (UNKNOWN.equals(appState.getState())) {
            int result = wildfly.getWildflyClient(wildflyProvider).deploy(file);
            if (result != 200) {
                throw new ProvisioningException("Deployment to Wildfly Failed with error code: " + result);
            }
        } else if ((RUNNING.equals(appState.getState()) || STOPPED.equals(appState.getState())) &&
                (isNullOrEmpty(runtimeConfig.getRedeployStrategy()) || "auto".equals(runtimeConfig.getRedeployStrategy()))) {
            wildfly.getWildflyClient(wildflyProvider).undeploy(id);
            int result = wildfly.getWildflyClient(wildflyProvider).deploy(file);
            if (result != 200) {
                throw new ProvisioningException("Deployment to Wildfly Failed with error code: " + result);
            }
        } else {
            throw new ProvisioningException("A runtime with the given identifier: " + id + " is already deployed");
        }

        String appContext = id.substring(0,
                                         id.lastIndexOf(".war"));
        WildflyRuntimeEndpoint endpoint = new WildflyRuntimeEndpoint();
        endpoint.setHost(wildfly.getWildflyClient(wildflyProvider).getHost());
        endpoint.setPort(wildfly.getWildflyClient(wildflyProvider).getPort());
        endpoint.setContext(appContext);
        return Optional.of(new WildflyRuntime(id,
                                              buildRuntimeName(runtimeConfig,
                                                               id),
                                              runtimeConfig,
                                              wildflyProvider,
                                              endpoint,
                                              new WildflyRuntimeInfo(),
                                              new WildflyRuntimeState(RUNNING,
                                                                      new Date().toString())));
    }

    @Override
    public Class<? extends Config> executeFor() {
        return WildflyRuntimeExecConfig.class;
    }

    @Override
    public String outputId() {
        return "wildfly-runtime";
    }

    @Override
    public boolean supports(final RuntimeConfig config) {
        return config instanceof WildflyRuntimeConfiguration;
    }

    @Override
    public boolean supports(final RuntimeId runtimeId) {
        return runtimeId instanceof WildflyRuntime
                || runtimeRegistry.getRuntimeById(runtimeId.getId()) instanceof WildflyRuntime;
    }

    @Override
    public void destroy(final RuntimeId runtimeId) {
        final Optional<WildflyProvider> _wildflyProvider = runtimeRegistry.getProvider(runtimeId.getProviderId(),
                                                                                       WildflyProvider.class);
        WildflyProvider wildflyProvider = _wildflyProvider.get();
        int result = wildfly.getWildflyClient(wildflyProvider).undeploy(runtimeId.getId());
        if (result != 200) {
            throw new ProvisioningException("UnDeployment to Wildfly Failed with error code: " + result);
        }
        runtimeRegistry.deregisterRuntime(runtimeId);
    }
}
