package org.jboss.errai.ioc.client;

import elemental2.dom.HTMLElement;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar.View;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBarView;
import org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchEngine;

public class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeSearchBar> { private class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeSearchBar implements Proxy<DataTypeSearchBar> {
    private final ProxyHelper<DataTypeSearchBar> proxyHelper = new ProxyHelperImpl<DataTypeSearchBar>("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null);
    }

    public void initProxyProperties(final DataTypeSearchBar instance) {

    }

    public DataTypeSearchBar asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeSearchBar instance) {
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

    @Override public HTMLElement getElement() {
      if (proxyHelper != null) {
        final DataTypeSearchBar proxiedInstance = proxyHelper.getInstance(this);
        final HTMLElement retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        return super.getElement();
      }
    }

    @Override public void refresh() {
      if (proxyHelper != null) {
        final DataTypeSearchBar proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.refresh();
      } else {
        super.refresh();
      }
    }

    @Override public void reset() {
      if (proxyHelper != null) {
        final DataTypeSearchBar proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.reset();
      } else {
        super.reset();
      }
    }

    @Override public boolean isEnabled() {
      if (proxyHelper != null) {
        final DataTypeSearchBar proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isEnabled();
        return retVal;
      } else {
        return super.isEnabled();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeSearchBar proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeSearchBar.class, "Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeSearchBar.class, Object.class });
  }

  public DataTypeSearchBar createInstance(final ContextManager contextManager) {
    final View _view_0 = (DataTypeSearchBarView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBarView__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeSearchEngine _searchEngine_1 = (DataTypeSearchEngine) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchEngine__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeList _dataTypeList_2 = (DataTypeList) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_l_DataTypeList__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeSearchBar instance = new DataTypeSearchBar(_view_0, _searchEngine_1, _dataTypeList_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DataTypeSearchBar instance) {
    DataTypeSearchBar_setup(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_s_DataTypeSearchBar__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar ([org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar$View, org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchEngine, org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeSearchBar> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void DataTypeSearchBar_setup(DataTypeSearchBar instance) /*-{
    instance.@org.kie.workbench.common.dmn.client.editors.types.search.DataTypeSearchBar::setup()();
  }-*/;
}