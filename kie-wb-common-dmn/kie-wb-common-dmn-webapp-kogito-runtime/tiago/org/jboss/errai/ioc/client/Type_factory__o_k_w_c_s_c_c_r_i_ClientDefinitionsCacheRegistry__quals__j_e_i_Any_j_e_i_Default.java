package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefaultDefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public class Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDefinitionsCacheRegistry> { private class Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientDefinitionsCacheRegistry implements Proxy<ClientDefinitionsCacheRegistry> {
    private final ProxyHelper<ClientDefinitionsCacheRegistry> proxyHelper = new ProxyHelperImpl<ClientDefinitionsCacheRegistry>("Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientDefinitionsCacheRegistry instance) {

    }

    public ClientDefinitionsCacheRegistry asBeanType() {
      return this;
    }

    public void setInstance(final ClientDefinitionsCacheRegistry instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final ClientDefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public DefinitionsCacheRegistry getRegistry() {
      if (proxyHelper != null) {
        final ClientDefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionsCacheRegistry retVal = proxiedInstance.getRegistry();
        return retVal;
      } else {
        return super.getRegistry();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientDefinitionsCacheRegistry proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientDefinitionsCacheRegistry.class, "Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientDefinitionsCacheRegistry.class, Object.class });
  }

  public ClientDefinitionsCacheRegistry createInstance(final ContextManager contextManager) {
    final DefaultDefinitionsCacheRegistry _definitionsCacheRegistry_0 = (DefaultDefinitionsCacheRegistry) contextManager.getInstance("Type_factory__o_k_w_c_s_c_r_i_DefaultDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final ClientDefinitionsCacheRegistry instance = new ClientDefinitionsCacheRegistry(_definitionsCacheRegistry_0);
    registerDependentScopedReference(instance, _definitionsCacheRegistry_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientDefinitionsCacheRegistry instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientDefinitionsCacheRegistry> proxyImpl = new Type_factory__o_k_w_c_s_c_c_r_i_ClientDefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}