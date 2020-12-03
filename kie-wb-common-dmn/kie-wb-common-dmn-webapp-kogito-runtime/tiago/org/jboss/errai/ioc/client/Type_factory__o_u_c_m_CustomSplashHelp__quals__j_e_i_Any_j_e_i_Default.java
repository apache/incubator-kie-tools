package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_Default extends Factory<CustomSplashHelp> { private class Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CustomSplashHelp implements Proxy<CustomSplashHelp> {
    private final ProxyHelper<CustomSplashHelp> proxyHelper = new ProxyHelperImpl<CustomSplashHelp>("Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final CustomSplashHelp instance) {

    }

    public CustomSplashHelp asBeanType() {
      return this;
    }

    public void setInstance(final CustomSplashHelp instance) {
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

    @Override public void push(CustomMenuBuilder element) {
      if (proxyHelper != null) {
        final CustomSplashHelp proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.push(element);
      } else {
        super.push(element);
      }
    }

    @Override public MenuItem build() {
      if (proxyHelper != null) {
        final CustomSplashHelp proxiedInstance = proxyHelper.getInstance(this);
        final MenuItem retVal = proxiedInstance.build();
        return retVal;
      } else {
        return super.build();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CustomSplashHelp proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CustomSplashHelp.class, "Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CustomSplashHelp.class, Object.class, CustomMenuBuilder.class });
  }

  public CustomSplashHelp createInstance(final ContextManager contextManager) {
    final CustomSplashHelp instance = new CustomSplashHelp();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CustomSplashHelp> proxyImpl = new Type_factory__o_u_c_m_CustomSplashHelp__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}