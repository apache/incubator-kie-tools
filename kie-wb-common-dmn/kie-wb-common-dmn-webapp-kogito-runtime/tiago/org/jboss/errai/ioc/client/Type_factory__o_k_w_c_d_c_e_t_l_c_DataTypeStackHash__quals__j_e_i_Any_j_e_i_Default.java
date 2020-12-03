package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeStackHash> { private class Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeStackHash implements Proxy<DataTypeStackHash> {
    private final ProxyHelper<DataTypeStackHash> proxyHelper = new ProxyHelperImpl<DataTypeStackHash>("Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DataTypeStackHash instance) {

    }

    public DataTypeStackHash asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeStackHash instance) {
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

    @Override public String calculateHash(DataType dataType) {
      if (proxyHelper != null) {
        final DataTypeStackHash proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.calculateHash(dataType);
        return retVal;
      } else {
        return super.calculateHash(dataType);
      }
    }

    @Override public String calculateParentHash(DataType reference) {
      if (proxyHelper != null) {
        final DataTypeStackHash proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.calculateParentHash(reference);
        return retVal;
      } else {
        return super.calculateParentHash(reference);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeStackHash proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeStackHash.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeStackHash.class, Object.class });
  }

  public DataTypeStackHash createInstance(final ContextManager contextManager) {
    final DataTypeStore _dataTypeStore_0 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeStackHash instance = new DataTypeStackHash(_dataTypeStore_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_l_c_DataTypeStackHash__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.listview.common.DataTypeStackHash ([org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeStackHash> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}