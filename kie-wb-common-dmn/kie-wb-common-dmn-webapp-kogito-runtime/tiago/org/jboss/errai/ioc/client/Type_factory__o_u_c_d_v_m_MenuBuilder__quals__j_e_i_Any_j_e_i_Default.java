package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.docks.view.menu.MenuBuilder;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<MenuBuilder> { private class Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MenuBuilder implements Proxy<MenuBuilder> {
    private final ProxyHelper<MenuBuilder> proxyHelper = new ProxyHelperImpl<MenuBuilder>("Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final MenuBuilder instance) {

    }

    public MenuBuilder asBeanType() {
      return this;
    }

    public void setInstance(final MenuBuilder instance) {
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

    @Override public Widget makeItem(MenuItem item, boolean isRoot) {
      if (proxyHelper != null) {
        final MenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.makeItem(item, isRoot);
        return retVal;
      } else {
        return super.makeItem(item, isRoot);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MenuBuilder proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MenuBuilder.class, "Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MenuBuilder.class, Object.class });
  }

  public MenuBuilder createInstance(final ContextManager contextManager) {
    final MenuBuilder instance = new MenuBuilder();
    setIncompleteInstance(instance);
    final DefaultAuthorizationManager MenuBuilder_authzManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    MenuBuilder_AuthorizationManager_authzManager(instance, MenuBuilder_authzManager);
    final User MenuBuilder_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MenuBuilder_identity);
    MenuBuilder_User_identity(instance, MenuBuilder_identity);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MenuBuilder> proxyImpl = new Type_factory__o_u_c_d_v_m_MenuBuilder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static AuthorizationManager MenuBuilder_AuthorizationManager_authzManager(MenuBuilder instance) /*-{
    return instance.@org.uberfire.client.docks.view.menu.MenuBuilder::authzManager;
  }-*/;

  native static void MenuBuilder_AuthorizationManager_authzManager(MenuBuilder instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.docks.view.menu.MenuBuilder::authzManager = value;
  }-*/;

  native static User MenuBuilder_User_identity(MenuBuilder instance) /*-{
    return instance.@org.uberfire.client.docks.view.menu.MenuBuilder::identity;
  }-*/;

  native static void MenuBuilder_User_identity(MenuBuilder instance, User value) /*-{
    instance.@org.uberfire.client.docks.view.menu.MenuBuilder::identity = value;
  }-*/;
}