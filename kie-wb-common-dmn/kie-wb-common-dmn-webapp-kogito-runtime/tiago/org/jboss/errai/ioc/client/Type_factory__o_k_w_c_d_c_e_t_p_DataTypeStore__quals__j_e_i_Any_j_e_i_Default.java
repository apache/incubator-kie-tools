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
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

public class Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeStore> { private class Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeStore implements Proxy<DataTypeStore> {
    private final ProxyHelper<DataTypeStore> proxyHelper = new ProxyHelperImpl<DataTypeStore>("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DataTypeStore instance) {

    }

    public DataTypeStore asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeStore instance) {
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

    @Override public DataType get(String uuid) {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        final DataType retVal = proxiedInstance.get(uuid);
        return retVal;
      } else {
        return super.get(uuid);
      }
    }

    @Override public void index(String uuid, DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.index(uuid, dataType);
      } else {
        super.index(uuid, dataType);
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public int size() {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.size();
        return retVal;
      } else {
        return super.size();
      }
    }

    @Override public List getTopLevelDataTypes() {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getTopLevelDataTypes();
        return retVal;
      } else {
        return super.getTopLevelDataTypes();
      }
    }

    @Override public List all() {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.all();
        return retVal;
      } else {
        return super.all();
      }
    }

    @Override public void unIndex(String uuid) {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unIndex(uuid);
      } else {
        super.unIndex(uuid);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeStore proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeStore.class, "Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeStore.class, Object.class });
  }

  public DataTypeStore createInstance(final ContextManager contextManager) {
    final DataTypeStore instance = new DataTypeStore();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeStore> proxyImpl = new Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}