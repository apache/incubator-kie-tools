package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Undefined;

public class Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default extends Factory<Undefined> { private class Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends Undefined implements Proxy<Undefined> {
    private final ProxyHelper<Undefined> proxyHelper = new ProxyHelperImpl<Undefined>("Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final Undefined instance) {

    }

    public Undefined asBeanType() {
      return this;
    }

    public void setInstance(final Undefined instance) {
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
        final Undefined proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName();
        return retVal;
      } else {
        return super.getName();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final Undefined proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Undefined.class, "Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Undefined.class, Category.class, Object.class });
  }

  public Undefined createInstance(final ContextManager contextManager) {
    final Undefined instance = new Undefined();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<Undefined> proxyImpl = new Type_factory__o_u_w_c_Undefined__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}