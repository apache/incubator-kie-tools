package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeManagerStackStore;

public class Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeManagerStackStore> { private class Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeManagerStackStore implements Proxy<DataTypeManagerStackStore> {
    private final ProxyHelper<DataTypeManagerStackStore> proxyHelper = new ProxyHelperImpl<DataTypeManagerStackStore>("Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DataTypeManagerStackStore instance) {

    }

    public DataTypeManagerStackStore asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeManagerStackStore instance) {
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

    @Override public List get(String uuid) {
      if (proxyHelper != null) {
        final DataTypeManagerStackStore proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.get(uuid);
        return retVal;
      } else {
        return super.get(uuid);
      }
    }

    @Override public void put(String uuid, List types) {
      if (proxyHelper != null) {
        final DataTypeManagerStackStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.put(uuid, types);
      } else {
        super.put(uuid, types);
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final DataTypeManagerStackStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeManagerStackStore proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeManagerStackStore.class, "Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeManagerStackStore.class, Object.class });
  }

  public DataTypeManagerStackStore createInstance(final ContextManager contextManager) {
    final DataTypeManagerStackStore instance = new DataTypeManagerStackStore();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeManagerStackStore> proxyImpl = new Type_factory__o_k_w_c_d_c_e_t_c_DataTypeManagerStackStore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}