package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.StunnerLienzoCore;

public class Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerLienzoCore> { private class Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends StunnerLienzoCore implements Proxy<StunnerLienzoCore> {
    private final ProxyHelper<StunnerLienzoCore> proxyHelper = new ProxyHelperImpl<StunnerLienzoCore>("Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final StunnerLienzoCore instance) {

    }

    public StunnerLienzoCore asBeanType() {
      return this;
    }

    public void setInstance(final StunnerLienzoCore instance) {
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

    @Override public void init() {
      if (proxyHelper != null) {
        final StunnerLienzoCore proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init();
      } else {
        super.init();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final StunnerLienzoCore proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerLienzoCore.class, "Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, true, null, true));
    handle.setAssignableTypes(new Class[] { StunnerLienzoCore.class, Object.class });
  }

  public StunnerLienzoCore createInstance(final ContextManager contextManager) {
    final StunnerLienzoCore instance = new StunnerLienzoCore();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final StunnerLienzoCore instance) {
    instance.init();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<StunnerLienzoCore> proxyImpl = new Type_factory__o_k_w_c_s_c_l_StunnerLienzoCore__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}