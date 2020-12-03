package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.registry.ClientRegistryFactory;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.DefinitionRegistry;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.registry.diagram.DiagramRegistry;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.AbstractRegistryFactory;
import org.kie.workbench.common.stunner.core.registry.rule.RuleHandlerRegistry;

public class Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientRegistryFactoryImpl> { private class Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientRegistryFactoryImpl implements Proxy<ClientRegistryFactoryImpl> {
    private final ProxyHelper<ClientRegistryFactoryImpl> proxyHelper = new ProxyHelperImpl<ClientRegistryFactoryImpl>("Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientRegistryFactoryImpl instance) {

    }

    public ClientRegistryFactoryImpl asBeanType() {
      return this;
    }

    public void setInstance(final ClientRegistryFactoryImpl instance) {
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

    @Override public AdapterRegistry newAdapterRegistry() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final AdapterRegistry retVal = proxiedInstance.newAdapterRegistry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public TypeDefinitionSetRegistry newDefinitionSetRegistry() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final TypeDefinitionSetRegistry retVal = proxiedInstance.newDefinitionSetRegistry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public DefinitionRegistry newDefinitionRegistry() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionRegistry retVal = proxiedInstance.newDefinitionRegistry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public FactoryRegistry newFactoryRegistry() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final FactoryRegistry retVal = proxiedInstance.newFactoryRegistry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public DiagramRegistry newDiagramRegistry() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final DiagramRegistry retVal = proxiedInstance.newDiagramRegistry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public RuleHandlerRegistry newRuleHandlerRegistry() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final RuleHandlerRegistry retVal = proxiedInstance.newRuleHandlerRegistry();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientRegistryFactoryImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientRegistryFactoryImpl.class, "Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientRegistryFactoryImpl.class, AbstractRegistryFactory.class, Object.class, RegistryFactory.class, ClientRegistryFactory.class });
  }

  public ClientRegistryFactoryImpl createInstance(final ContextManager contextManager) {
    final AdapterManager _adapterManager_0 = (AdapterManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientRegistryFactoryImpl instance = new ClientRegistryFactoryImpl(_adapterManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientRegistryFactoryImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}