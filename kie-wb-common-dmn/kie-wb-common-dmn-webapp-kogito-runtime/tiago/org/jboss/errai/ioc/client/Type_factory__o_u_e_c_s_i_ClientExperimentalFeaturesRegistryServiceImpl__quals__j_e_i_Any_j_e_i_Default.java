package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl;
import org.uberfire.experimental.service.ExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.backend.BackendExperimentalFeaturesRegistryService;
import org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.ExperimentalFeaturesRegistry;

public class Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientExperimentalFeaturesRegistryServiceImpl> { private class Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientExperimentalFeaturesRegistryServiceImpl implements Proxy<ClientExperimentalFeaturesRegistryServiceImpl> {
    private final ProxyHelper<ClientExperimentalFeaturesRegistryServiceImpl> proxyHelper = new ProxyHelperImpl<ClientExperimentalFeaturesRegistryServiceImpl>("Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final ClientExperimentalFeaturesRegistryServiceImpl instance) {

    }

    public ClientExperimentalFeaturesRegistryServiceImpl asBeanType() {
      return this;
    }

    public void setInstance(final ClientExperimentalFeaturesRegistryServiceImpl instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void loadRegistry() {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadRegistry();
      } else {
        super.loadRegistry();
      }
    }

    @Override public ExperimentalFeaturesRegistry getFeaturesRegistry() {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final ExperimentalFeaturesRegistry retVal = proxiedInstance.getFeaturesRegistry();
        return retVal;
      } else {
        return super.getFeaturesRegistry();
      }
    }

    @Override public boolean isFeatureEnabled(String featureId) {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isFeatureEnabled(featureId);
        return retVal;
      } else {
        return super.isFeatureEnabled(featureId);
      }
    }

    @Override public void updateExperimentalFeature(String featureId, boolean enabled) {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.updateExperimentalFeature(featureId, enabled);
      } else {
        super.updateExperimentalFeature(featureId, enabled);
      }
    }

    @Override public Boolean isExperimentalEnabled() {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final Boolean retVal = proxiedInstance.isExperimentalEnabled();
        return retVal;
      } else {
        return super.isExperimentalEnabled();
      }
    }

    @Override public void onGlobalFeatureModified(PortableExperimentalFeatureModifiedEvent event) {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onGlobalFeatureModified(event);
      } else {
        super.onGlobalFeatureModified(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientExperimentalFeaturesRegistryServiceImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientExperimentalFeaturesRegistryServiceImpl.class, "Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientExperimentalFeaturesRegistryServiceImpl.class, Object.class, ClientExperimentalFeaturesRegistryService.class, ExperimentalFeaturesRegistryService.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent", new AbstractCDIEventCallback<PortableExperimentalFeatureModifiedEvent>() {
      public void fireEvent(final PortableExperimentalFeatureModifiedEvent event) {
        final ClientExperimentalFeaturesRegistryServiceImpl instance = Factory.maybeUnwrapProxy((ClientExperimentalFeaturesRegistryServiceImpl) context.getInstance("Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_Default"));
        instance.onGlobalFeatureModified(event);
      }
      public String toString() {
        return "Observer: org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent []";
      }
    });
  }

  public ClientExperimentalFeaturesRegistryServiceImpl createInstance(final ContextManager contextManager) {
    final Event<NonPortableExperimentalFeatureModifiedEvent> _event_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NonPortableExperimentalFeatureModifiedEvent.class }, new Annotation[] { });
    final Caller<BackendExperimentalFeaturesRegistryService> _backendService_0 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { BackendExperimentalFeaturesRegistryService.class }, new Annotation[] { });
    final ClientExperimentalFeaturesRegistryServiceImpl instance = new ClientExperimentalFeaturesRegistryServiceImpl(_backendService_0, _event_1);
    registerDependentScopedReference(instance, _event_1);
    registerDependentScopedReference(instance, _backendService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_c_s_i_ClientExperimentalFeaturesRegistryServiceImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.experimental.client.service.impl.ClientExperimentalFeaturesRegistryServiceImpl ([org.jboss.errai.common.client.api.Caller, javax.enterprise.event.Event])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientExperimentalFeaturesRegistryServiceImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}