package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.kogito.IsKogito;

public class Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_Default extends Factory<IsKogito> { private class Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends IsKogito implements Proxy<IsKogito> {
    private final ProxyHelper<IsKogito> proxyHelper = new ProxyHelperImpl<IsKogito>("Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final IsKogito instance) {

    }

    public IsKogito asBeanType() {
      return this;
    }

    public void setInstance(final IsKogito instance) {
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

    @Override public boolean get() {
      if (proxyHelper != null) {
        final IsKogito proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.get();
        return retVal;
      } else {
        return super.get();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final IsKogito proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IsKogito.class, "Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IsKogito.class, Object.class });
  }

  public IsKogito createInstance(final ContextManager contextManager) {
    final IsKogito instance = new IsKogito();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<IsKogito> proxyImpl = new Type_factory__o_k_w_c_w_c_k_IsKogito__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}