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
import org.kie.workbench.common.dmn.client.editors.search.DMNDataTypesSubIndex;
import org.kie.workbench.common.dmn.client.editors.search.DMNSubIndex;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.kie.workbench.common.widgets.client.search.common.HasSearchableElements;

public class Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDataTypesSubIndex> { private class Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DMNDataTypesSubIndex implements Proxy<DMNDataTypesSubIndex> {
    private final ProxyHelper<DMNDataTypesSubIndex> proxyHelper = new ProxyHelperImpl<DMNDataTypesSubIndex>("Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final DMNDataTypesSubIndex instance) {

    }

    public DMNDataTypesSubIndex asBeanType() {
      return this;
    }

    public void setInstance(final DMNDataTypesSubIndex instance) {
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

    @Override public List getSearchableElements() {
      if (proxyHelper != null) {
        final DMNDataTypesSubIndex proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getSearchableElements();
        return retVal;
      } else {
        return super.getSearchableElements();
      }
    }

    @Override public void onNoResultsFound() {
      if (proxyHelper != null) {
        final DMNDataTypesSubIndex proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onNoResultsFound();
      } else {
        super.onNoResultsFound();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DMNDataTypesSubIndex proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDataTypesSubIndex.class, "Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDataTypesSubIndex.class, Object.class, DMNSubIndex.class, HasSearchableElements.class });
  }

  public DMNDataTypesSubIndex createInstance(final ContextManager contextManager) {
    final DataTypeStore _dataTypeStore_2 = (DataTypeStore) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_p_DataTypeStore__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeList _dataTypeList_0 = (DataTypeList) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeShortcuts _dataTypeShortcuts_1 = (DataTypeShortcuts) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default");
    final DMNDataTypesSubIndex instance = new DMNDataTypesSubIndex(_dataTypeList_0, _dataTypeShortcuts_1, _dataTypeStore_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_s_DMNDataTypesSubIndex__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.search.DMNDataTypesSubIndex an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.search.DMNDataTypesSubIndex ([org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList, org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts, org.kie.workbench.common.dmn.client.editors.types.persistence.DataTypeStore])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DMNDataTypesSubIndex> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}