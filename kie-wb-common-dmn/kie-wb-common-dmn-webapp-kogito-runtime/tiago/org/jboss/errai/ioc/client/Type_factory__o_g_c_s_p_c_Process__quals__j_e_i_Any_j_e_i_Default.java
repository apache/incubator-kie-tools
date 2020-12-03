package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.categories.Process;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.workbench.category.Category;

public class Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_Default extends Factory<Process> { private class Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends Process implements Proxy<Process> {
    private final ProxyHelper<Process> proxyHelper = new ProxyHelperImpl<Process>("Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final Process instance) {

    }

    public Process asBeanType() {
      return this;
    }

    public void setInstance(final Process instance) {
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
        final Process proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName();
        return retVal;
      } else {
        return super.getName();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final Process proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Process.class, "Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Process.class, Category.class, Object.class });
  }

  public Process createInstance(final ContextManager contextManager) {
    final Process instance = new Process();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<Process> proxyImpl = new Type_factory__o_g_c_s_p_c_Process__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}