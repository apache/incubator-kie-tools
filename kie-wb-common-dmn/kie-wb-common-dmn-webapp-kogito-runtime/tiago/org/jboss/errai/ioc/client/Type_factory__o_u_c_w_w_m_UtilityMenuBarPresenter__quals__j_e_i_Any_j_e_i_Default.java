package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.views.pfly.menu.UtilityMenuBarView;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBar;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter.View;
import org.uberfire.workbench.model.menu.Menus;

public class Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<UtilityMenuBarPresenter> { private class Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends UtilityMenuBarPresenter implements Proxy<UtilityMenuBarPresenter> {
    private final ProxyHelper<UtilityMenuBarPresenter> proxyHelper = new ProxyHelperImpl<UtilityMenuBarPresenter>("Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final UtilityMenuBarPresenter instance) {

    }

    public UtilityMenuBarPresenter asBeanType() {
      return this;
    }

    public void setInstance(final UtilityMenuBarPresenter instance) {
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

    @Override public IsWidget getView() {
      if (proxyHelper != null) {
        final UtilityMenuBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public void addMenus(Menus menus) {
      if (proxyHelper != null) {
        final UtilityMenuBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addMenus(menus);
      } else {
        super.addMenus(menus);
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final UtilityMenuBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        super.clear();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final UtilityMenuBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UtilityMenuBarPresenter.class, "Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UtilityMenuBarPresenter.class, Object.class, UtilityMenuBar.class, HasMenus.class });
  }

  public UtilityMenuBarPresenter createInstance(final ContextManager contextManager) {
    final UtilityMenuBarPresenter instance = new UtilityMenuBarPresenter();
    setIncompleteInstance(instance);
    final UtilityMenuBarView UtilityMenuBarPresenter_view = (UtilityMenuBarView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default");
    UtilityMenuBarPresenter_View_view(instance, UtilityMenuBarPresenter_view);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<UtilityMenuBarPresenter> proxyImpl = new Type_factory__o_u_c_w_w_m_UtilityMenuBarPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static View UtilityMenuBarPresenter_View_view(UtilityMenuBarPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter::view;
  }-*/;

  native static void UtilityMenuBarPresenter_View_view(UtilityMenuBarPresenter instance, View value) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter::view = value;
  }-*/;
}