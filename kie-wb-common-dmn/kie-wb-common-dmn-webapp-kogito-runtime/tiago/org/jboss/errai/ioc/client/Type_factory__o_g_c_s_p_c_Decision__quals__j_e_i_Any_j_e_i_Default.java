package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.categories.Decision;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.workbench.category.Category;

public class Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default extends Factory<Decision> { private class Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends Decision implements Proxy<Decision> {
    private final ProxyHelper<Decision> proxyHelper = new ProxyHelperImpl<Decision>("Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final Decision instance) {

    }

    public Decision asBeanType() {
      return this;
    }

    public void setInstance(final Decision instance) {
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

    @Override public String getName() {
      if (proxyHelper != null) {
        final Decision proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName();
        return retVal;
      } else {
        return super.getName();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final Decision proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Decision.class, "Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Decision.class, Category.class, Object.class });
  }

  public Decision createInstance(final ContextManager contextManager) {
    final Decision instance = new Decision();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<Decision> proxyImpl = new Type_factory__o_g_c_s_p_c_Decision__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}