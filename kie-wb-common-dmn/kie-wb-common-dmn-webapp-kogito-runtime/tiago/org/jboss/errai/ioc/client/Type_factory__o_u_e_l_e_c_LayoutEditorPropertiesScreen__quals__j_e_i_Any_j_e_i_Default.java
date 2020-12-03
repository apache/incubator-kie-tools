package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.layout.editor.client.LayoutEditorPropertiesScreen;
import org.uberfire.ext.layout.editor.client.widgets.LayoutEditorPropertiesPresenter;

public class Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutEditorPropertiesScreen> { private class Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutEditorPropertiesScreen implements Proxy<LayoutEditorPropertiesScreen> {
    private final ProxyHelper<LayoutEditorPropertiesScreen> proxyHelper = new ProxyHelperImpl<LayoutEditorPropertiesScreen>("Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutEditorPropertiesScreen instance) {

    }

    public LayoutEditorPropertiesScreen asBeanType() {
      return this;
    }

    public void setInstance(final LayoutEditorPropertiesScreen instance) {
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

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesScreen proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public IsElement getView() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesScreen proxiedInstance = proxyHelper.getInstance(this);
        final IsElement retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutEditorPropertiesScreen proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutEditorPropertiesScreen.class, "Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutEditorPropertiesScreen.class, Object.class });
  }

  public LayoutEditorPropertiesScreen createInstance(final ContextManager contextManager) {
    final LayoutEditorPropertiesPresenter _propertiesPresenter_0 = (LayoutEditorPropertiesPresenter) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutEditorPropertiesPresenter__quals__j_e_i_Any_j_e_i_Default");
    final LayoutEditorPropertiesScreen instance = new LayoutEditorPropertiesScreen(_propertiesPresenter_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutEditorPropertiesScreen> proxyImpl = new Type_factory__o_u_e_l_e_c_LayoutEditorPropertiesScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}