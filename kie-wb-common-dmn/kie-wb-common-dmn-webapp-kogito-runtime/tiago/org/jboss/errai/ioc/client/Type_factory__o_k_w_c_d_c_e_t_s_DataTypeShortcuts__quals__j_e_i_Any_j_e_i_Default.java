package org.jboss.errai.ioc.client;

import elemental2.dom.Element;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcuts;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;

public class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default extends Factory<DataTypeShortcuts> { private class Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DataTypeShortcuts implements Proxy<DataTypeShortcuts> {
    private final ProxyHelper<DataTypeShortcuts> proxyHelper = new ProxyHelperImpl<DataTypeShortcuts>("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final DataTypeShortcuts instance) {

    }

    public DataTypeShortcuts asBeanType() {
      return this;
    }

    public void setInstance(final DataTypeShortcuts instance) {
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

    @Override public void init(DataTypeList dataTypeList) {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(dataTypeList);
      } else {
        super.init(dataTypeList);
      }
    }

    @Override public void setup() {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup();
      } else {
        super.setup();
      }
    }

    @Override public void teardown() {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.teardown();
      } else {
        super.teardown();
      }
    }

    @Override public void reset() {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.reset();
      } else {
        super.reset();
      }
    }

    @Override public void highlight(Element element) {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.highlight(element);
      } else {
        super.highlight(element);
      }
    }

    @Override public void enable() {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.enable();
      } else {
        super.enable();
      }
    }

    @Override public void disable() {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.disable();
      } else {
        super.disable();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DataTypeShortcuts proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataTypeShortcuts.class, "Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataTypeShortcuts.class, Object.class });
  }

  public DataTypeShortcuts createInstance(final ContextManager contextManager) {
    final DataTypeListShortcuts _listShortcuts_0 = (DataTypeListShortcuts) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_t_s_DataTypeListShortcuts__quals__j_e_i_Any_j_e_i_Default");
    final DataTypeShortcuts instance = new DataTypeShortcuts(_listShortcuts_0);
    registerDependentScopedReference(instance, _listShortcuts_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_k_w_c_d_c_e_t_s_DataTypeShortcuts__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts an exception was thrown from this constructor: @javax.inject.Inject()  public org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts ([org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeListShortcuts])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DataTypeShortcuts> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}