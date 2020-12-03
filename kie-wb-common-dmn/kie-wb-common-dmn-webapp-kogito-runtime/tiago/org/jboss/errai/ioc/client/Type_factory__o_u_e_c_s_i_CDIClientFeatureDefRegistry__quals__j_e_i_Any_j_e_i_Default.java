package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesDefRegistry;
import org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry;
import org.uberfire.experimental.service.backend.BackendExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinition;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefinitionProvider;
import org.uberfire.experimental.service.definition.impl.ExperimentalFeatureDefRegistryImpl;

public class Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<CDIClientFeatureDefRegistry> { private class Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CDIClientFeatureDefRegistry implements Proxy<CDIClientFeatureDefRegistry> {
    private final ProxyHelper<CDIClientFeatureDefRegistry> proxyHelper = new ProxyHelperImpl<CDIClientFeatureDefRegistry>("Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final CDIClientFeatureDefRegistry instance) {

    }

    public CDIClientFeatureDefRegistry asBeanType() {
      return this;
    }

    public void setInstance(final CDIClientFeatureDefRegistry instance) {
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
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadRegistry();
      } else {
        super.loadRegistry();
      }
    }

    @Override public ExperimentalFeatureDefinition getFeatureById(String definitionId) {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        final ExperimentalFeatureDefinition retVal = proxiedInstance.getFeatureById(definitionId);
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Collection getAllFeatures() {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getAllFeatures();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Collection getGlobalFeatures() {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getGlobalFeatures();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Collection getUserFeatures() {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getUserFeatures();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void register(ExperimentalFeatureDefinitionProvider definitionProvider) {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.register(definitionProvider);
      } else {
        super.register(definitionProvider);
      }
    }

    @Override public void register(ExperimentalFeatureDefinition featureDefinition) {
      if (proxyHelper != null) {
        final CDIClientFeatureDefRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.register(featureDefinition);
      } else {
        super.register(featureDefinition);
      }
    }
  }
  public Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CDIClientFeatureDefRegistry.class, "Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CDIClientFeatureDefRegistry.class, ExperimentalFeatureDefRegistryImpl.class, Object.class, ExperimentalFeatureDefRegistry.class, ClientExperimentalFeaturesDefRegistry.class });
  }

  public CDIClientFeatureDefRegistry createInstance(final ContextManager contextManager) {
    final Caller<BackendExperimentalFeatureDefRegistry> _backendRegistry_0 = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { BackendExperimentalFeatureDefRegistry.class }, new Annotation[] { });
    final CDIClientFeatureDefRegistry instance = new CDIClientFeatureDefRegistry(_backendRegistry_0);
    registerDependentScopedReference(instance, _backendRegistry_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry ([org.jboss.errai.common.client.api.Caller])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CDIClientFeatureDefRegistry> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}