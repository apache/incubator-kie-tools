package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.registry.impl.ClientRegistryFactoryImpl;
import org.kie.workbench.common.stunner.core.definition.adapter.Adapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManagerImpl;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManagerImpl;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;

public class Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientDefinitionManager> { private class Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ClientDefinitionManager implements Proxy<ClientDefinitionManager> {
    private final ProxyHelper<ClientDefinitionManager> proxyHelper = new ProxyHelperImpl<ClientDefinitionManager>("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ClientDefinitionManager instance) {

    }

    public ClientDefinitionManager asBeanType() {
      return this;
    }

    public void setInstance(final ClientDefinitionManager instance) {
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
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public TypeDefinitionSetRegistry definitionSets() {
      if (proxyHelper != null) {
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        final TypeDefinitionSetRegistry retVal = proxiedInstance.definitionSets();
        return retVal;
      } else {
        return super.definitionSets();
      }
    }

    @Override public AdapterManager adapters() {
      if (proxyHelper != null) {
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        final AdapterManager retVal = proxiedInstance.adapters();
        return retVal;
      } else {
        return super.adapters();
      }
    }

    @Override protected void addDefinitionSet(Object object) {
      if (proxyHelper != null) {
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        AbstractDefinitionManager_addDefinitionSet_Object(proxiedInstance, object);
      } else {
        super.addDefinitionSet(object);
      }
    }

    @Override protected void addAdapter(Adapter adapter) {
      if (proxyHelper != null) {
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        AbstractDefinitionManager_addAdapter_Adapter(proxiedInstance, adapter);
      } else {
        super.addAdapter(adapter);
      }
    }

    @Override public CloneManager cloneManager() {
      if (proxyHelper != null) {
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        final CloneManager retVal = proxiedInstance.cloneManager();
        return retVal;
      } else {
        return super.cloneManager();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ClientDefinitionManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientDefinitionManager.class, "Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientDefinitionManager.class, AbstractDefinitionManager.class, Object.class, DefinitionManager.class });
  }

  public ClientDefinitionManager createInstance(final ContextManager contextManager) {
    final AdapterManager _adapterManager_2 = (AdapterManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_a_AdapterManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final CloneManager _cloneManager_3 = (CloneManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_d_c_CloneManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final RegistryFactory _registryFactory_1 = (ClientRegistryFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_r_i_ClientRegistryFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _beanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientDefinitionManager instance = new ClientDefinitionManager(_beanManager_0, _registryFactory_1, _adapterManager_2, _cloneManager_3);
    registerDependentScopedReference(instance, _beanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ClientDefinitionManager instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ClientDefinitionManager> proxyImpl = new Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void AbstractDefinitionManager_addAdapter_Adapter(AbstractDefinitionManager instance, Adapter a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager::addAdapter(Lorg/kie/workbench/common/stunner/core/definition/adapter/Adapter;)(a0);
  }-*/;

  public native static void AbstractDefinitionManager_addDefinitionSet_Object(AbstractDefinitionManager instance, Object a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.api.AbstractDefinitionManager::addDefinitionSet(Ljava/lang/Object;)(a0);
  }-*/;
}