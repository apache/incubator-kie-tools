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
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchEngine;

public class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSearchEngine> { private class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeSearchEngine implements Proxy<DataTypeSearchEngine> {
    private final ProxyHelper<DataTypeSearchEngine> proxyHelper = new ProxyHelperImpl<DataTypeSearchEngine>("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DataTypeSearchEngine instance) {

    }

    public DataTypeSearchEngine asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeSearchEngine instance) {
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

    @Override public List search(String keyword) {
      if (proxyHelper != null) {
        final DataTypeSearchEngine proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.search(keyword);
        return retVal;
      } else {
        return super.search(keyword);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeSearchEngine proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeSearchEngine.class, "Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeSearchEngine.class, Object.class });
  }

  public DataTypeSearchEngine createInstance(final ContextManager contextManager) {
    final DataTypeStore _dataTypeStore_0 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeSearchEngine instance = new DataTypeSearchEngine(_dataTypeStore_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchEngine an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchEngine ([org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeSearchEngine> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}