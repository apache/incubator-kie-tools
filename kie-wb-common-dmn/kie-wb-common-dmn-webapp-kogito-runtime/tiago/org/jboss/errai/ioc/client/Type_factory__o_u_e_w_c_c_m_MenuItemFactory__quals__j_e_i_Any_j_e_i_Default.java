package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory;
import org.uberfire.ext.widgets.common.client.menu.MenuItemFactory.MenuItemViewHolder;
import org.uberfire.ext.widgets.common.client.menu.MenuItemView;
import org.uberfire.mvp.Command;

public class Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<MenuItemFactory> { private class Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends MenuItemFactory implements Proxy<MenuItemFactory> {
    private final ProxyHelper<MenuItemFactory> proxyHelper = new ProxyHelperImpl<MenuItemFactory>("Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final MenuItemFactory instance) {

    }

    public MenuItemFactory asBeanType() {
      return this;
    }

    public void setInstance(final MenuItemFactory instance) {
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

    @Override public MenuItemViewHolder makeMenuItemWithIcon(String caption, Command cmd) {
      if (proxyHelper != null) {
        final MenuItemFactory proxiedInstance = proxyHelper.getInstance(this);
        final MenuItemViewHolder retVal = proxiedInstance.makeMenuItemWithIcon(caption, cmd);
        return retVal;
      } else {
        return super.makeMenuItemWithIcon(caption, cmd);
      }
    }

    @Override public MenuItemViewHolder makeMenuItemHeader(String caption) {
      if (proxyHelper != null) {
        final MenuItemFactory proxiedInstance = proxyHelper.getInstance(this);
        final MenuItemViewHolder retVal = proxiedInstance.makeMenuItemHeader(caption);
        return retVal;
      } else {
        return super.makeMenuItemHeader(caption);
      }
    }

    @Override public MenuItemViewHolder makeMenuItemDivider() {
      if (proxyHelper != null) {
        final MenuItemFactory proxiedInstance = proxyHelper.getInstance(this);
        final MenuItemViewHolder retVal = proxiedInstance.makeMenuItemDivider();
        return retVal;
      } else {
        return super.makeMenuItemDivider();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final MenuItemFactory proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MenuItemFactory.class, "Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MenuItemFactory.class, Object.class });
  }

  public MenuItemFactory createInstance(final ContextManager contextManager) {
    final ManagedInstance<MenuItemView> _menuItemViewProducer_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { MenuItemView.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final MenuItemFactory instance = new MenuItemFactory(_menuItemViewProducer_0);
    registerDependentScopedReference(instance, _menuItemViewProducer_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_e_w_c_c_m_MenuItemFactory__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.ext.widgets.common.client.menu.MenuItemFactory an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.ext.widgets.common.client.menu.MenuItemFactory ([org.jboss.errai.ioc.client.api.ManagedInstance])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<MenuItemFactory> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}