package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionSetRuleAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.PropertyAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.bootstrap.BootstrapAdapterFactory;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;

public class Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<AdapterManagerImpl> { private class Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends AdapterManagerImpl implements Proxy<AdapterManagerImpl> {
    private final ProxyHelper<AdapterManagerImpl> proxyHelper = new ProxyHelperImpl<AdapterManagerImpl>("Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final AdapterManagerImpl instance) {

    }

    public AdapterManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final AdapterManagerImpl instance) {
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

    @Override public DefinitionSetAdapter forDefinitionSet() {
      if (proxyHelper != null) {
        final AdapterManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionSetAdapter retVal = proxiedInstance.forDefinitionSet();
        return retVal;
      } else {
        return super.forDefinitionSet();
      }
    }

    @Override public DefinitionSetRuleAdapter forRules() {
      if (proxyHelper != null) {
        final AdapterManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionSetRuleAdapter retVal = proxiedInstance.forRules();
        return retVal;
      } else {
        return super.forRules();
      }
    }

    @Override public DefinitionAdapter forDefinition() {
      if (proxyHelper != null) {
        final AdapterManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionAdapter retVal = proxiedInstance.forDefinition();
        return retVal;
      } else {
        return super.forDefinition();
      }
    }

    @Override public PropertyAdapter forProperty() {
      if (proxyHelper != null) {
        final AdapterManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PropertyAdapter retVal = proxiedInstance.forProperty();
        return retVal;
      } else {
        return super.forProperty();
      }
    }

    @Override public AdapterRegistry registry() {
      if (proxyHelper != null) {
        final AdapterManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final AdapterRegistry retVal = proxiedInstance.registry();
        return retVal;
      } else {
        return super.registry();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final AdapterManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdapterManagerImpl.class, "Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdapterManagerImpl.class, Object.class, AdapterManager.class });
  }

  public AdapterManagerImpl createInstance(final ContextManager contextManager) {
    final RegistryFactory _registryFactory_0 = (ClientRegistryFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final BootstrapAdapterFactory _bootstrapAdapterFactory_1 = (BootstrapAdapterFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_a_b_BootstrapAdapterFactory__quals__j_e_i_Any_j_e_i_Default");
    final AdapterManagerImpl instance = new AdapterManagerImpl(_registryFactory_0, _bootstrapAdapterFactory_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<AdapterManagerImpl> proxyImpl = new Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}