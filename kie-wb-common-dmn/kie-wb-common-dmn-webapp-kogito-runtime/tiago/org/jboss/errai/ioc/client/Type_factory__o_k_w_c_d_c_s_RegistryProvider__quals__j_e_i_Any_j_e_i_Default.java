package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.RegistryChangeListener;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.session.CommandRegistryHolder;
import org.kie.workbench.common.dmn.client.session.RegistryProvider;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;

public class Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<RegistryProvider> { private class Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends RegistryProvider implements Proxy<RegistryProvider> {
    private final ProxyHelper<RegistryProvider> proxyHelper = new ProxyHelperImpl<RegistryProvider>("Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final RegistryProvider instance) {

    }

    public RegistryProvider asBeanType() {
      return this;
    }

    public void setInstance(final RegistryProvider instance) {
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

    @Override public Registry getCurrentCommandRegistry() {
      if (proxyHelper != null) {
        final RegistryProvider proxiedInstance = proxyHelper.getInstance(this);
        final Registry retVal = proxiedInstance.getCurrentCommandRegistry();
        return retVal;
      } else {
        return super.getCurrentCommandRegistry();
      }
    }

    @Override public void setRegistryChangeListener(RegistryChangeListener registryChangeListener) {
      if (proxyHelper != null) {
        final RegistryProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setRegistryChangeListener(registryChangeListener);
      } else {
        super.setRegistryChangeListener(registryChangeListener);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final RegistryProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RegistryProvider.class, "Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RegistryProvider.class, Object.class });
  }

  public RegistryProvider createInstance(final ContextManager contextManager) {
    final GraphsProvider _graphsProvider_1 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<CommandRegistryHolder> _registryHolders_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CommandRegistryHolder.class }, new Annotation[] { });
    final RegistryProvider instance = new RegistryProvider(_registryHolders_0, _graphsProvider_1);
    registerDependentScopedReference(instance, _registryHolders_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_s_RegistryProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.session.RegistryProvider an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.session.RegistryProvider ([org.jboss.errai.ioc.client.api.ManagedInstance, org.kie.workbench.common.stunner.core.diagram.GraphsProvider])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<RegistryProvider> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}