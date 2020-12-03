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
import org.uberfire.ext.layout.editor.client.LayoutComponentPaletteScreen;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter;

public class Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutComponentPaletteScreen> { private class Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutComponentPaletteScreen implements Proxy<LayoutComponentPaletteScreen> {
    private final ProxyHelper<LayoutComponentPaletteScreen> proxyHelper = new ProxyHelperImpl<LayoutComponentPaletteScreen>("Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutComponentPaletteScreen instance) {

    }

    public LayoutComponentPaletteScreen asBeanType() {
      return this;
    }

    public void setInstance(final LayoutComponentPaletteScreen instance) {
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
        final LayoutComponentPaletteScreen proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public IsElement getView() {
      if (proxyHelper != null) {
        final LayoutComponentPaletteScreen proxiedInstance = proxyHelper.getInstance(this);
        final IsElement retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutComponentPaletteScreen proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutComponentPaletteScreen.class, "Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutComponentPaletteScreen.class, Object.class });
  }

  public LayoutComponentPaletteScreen createInstance(final ContextManager contextManager) {
    final LayoutComponentPalettePresenter _componentPalette_0 = (LayoutComponentPalettePresenter) contextManager.getInstance("Type_factory__o_u_e_l_e_c_w_LayoutComponentPalettePresenter__quals__j_e_i_Any_j_e_i_Default");
    final LayoutComponentPaletteScreen instance = new LayoutComponentPaletteScreen(_componentPalette_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutComponentPaletteScreen> proxyImpl = new Type_factory__o_u_e_l_e_c_LayoutComponentPaletteScreen__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}