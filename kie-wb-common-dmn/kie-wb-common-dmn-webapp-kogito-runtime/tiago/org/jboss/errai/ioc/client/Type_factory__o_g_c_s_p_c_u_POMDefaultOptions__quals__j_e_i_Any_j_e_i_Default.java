package org.jboss.errai.ioc.client;

import java.util.ArrayList;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.common.services.project.client.util.POMDefaultOptions;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;

public class Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_Default extends Factory<POMDefaultOptions> { private class Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends POMDefaultOptions implements Proxy<POMDefaultOptions> {
    private final ProxyHelper<POMDefaultOptions> proxyHelper = new ProxyHelperImpl<POMDefaultOptions>("Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final POMDefaultOptions instance) {

    }

    public POMDefaultOptions asBeanType() {
      return this;
    }

    public void setInstance(final POMDefaultOptions instance) {
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

    @Override public ArrayList getBuildPlugins() {
      if (proxyHelper != null) {
        final POMDefaultOptions proxiedInstance = proxyHelper.getInstance(this);
        final ArrayList retVal = proxiedInstance.getBuildPlugins();
        return retVal;
      } else {
        return super.getBuildPlugins();
      }
    }

    @Override public String getPackaging() {
      if (proxyHelper != null) {
        final POMDefaultOptions proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPackaging();
        return retVal;
      } else {
        return super.getPackaging();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final POMDefaultOptions proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(POMDefaultOptions.class, "Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { POMDefaultOptions.class, Object.class });
  }

  public POMDefaultOptions createInstance(final ContextManager contextManager) {
    final POMDefaultOptions instance = new POMDefaultOptions();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<POMDefaultOptions> proxyImpl = new Type_factory__o_g_c_s_p_c_u_POMDefaultOptions__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}