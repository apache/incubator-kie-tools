package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.client.editors.types.persistence.ItemDefinitionStore;

public class Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default extends Factory<ItemDefinitionStore> { private class Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ItemDefinitionStore implements Proxy<ItemDefinitionStore> {
    private final ProxyHelper<ItemDefinitionStore> proxyHelper = new ProxyHelperImpl<ItemDefinitionStore>("Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final ItemDefinitionStore instance) {

    }

    public ItemDefinitionStore asBeanType() {
      return this;
    }

    public void setInstance(final ItemDefinitionStore instance) {
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

    @Override public ItemDefinition get(String uuid) {
      if (proxyHelper != null) {
        final ItemDefinitionStore proxiedInstance = proxyHelper.getInstance(this);
        final ItemDefinition retVal = proxiedInstance.get(uuid);
        return retVal;
      } else {
        return super.get(uuid);
      }
    }

    @Override public void index(String uuid, ItemDefinition itemDefinition) {
      if (proxyHelper != null) {
        final ItemDefinitionStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.index(uuid, itemDefinition);
      } else {
        super.index(uuid, itemDefinition);
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final ItemDefinitionStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public void unIndex(String uuid) {
      if (proxyHelper != null) {
        final ItemDefinitionStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unIndex(uuid);
      } else {
        super.unIndex(uuid);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ItemDefinitionStore proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ItemDefinitionStore.class, "Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ItemDefinitionStore.class, Object.class });
  }

  public ItemDefinitionStore createInstance(final ContextManager contextManager) {
    final ItemDefinitionStore instance = new ItemDefinitionStore();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ItemDefinitionStore> proxyImpl = new Type_factory__o_k_w_c_d_c_e_t_p_ItemDefinitionStore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}