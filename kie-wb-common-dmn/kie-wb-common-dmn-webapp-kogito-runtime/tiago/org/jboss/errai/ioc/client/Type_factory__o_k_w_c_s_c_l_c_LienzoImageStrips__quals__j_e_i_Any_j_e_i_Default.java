package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.LienzoImageStrips;
import org.kie.workbench.common.stunner.core.client.shape.ImageStrip;
import org.uberfire.mvp.Command;

public class Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoImageStrips> { private class Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LienzoImageStrips implements Proxy<LienzoImageStrips> {
    private final ProxyHelper<LienzoImageStrips> proxyHelper = new ProxyHelperImpl<LienzoImageStrips>("Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LienzoImageStrips instance) {

    }

    public LienzoImageStrips asBeanType() {
      return this;
    }

    public void setInstance(final LienzoImageStrips instance) {
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

    @Override public void register(ImageStrip[] strips, Command callback) {
      if (proxyHelper != null) {
        final LienzoImageStrips proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.register(strips, callback);
      } else {
        super.register(strips, callback);
      }
    }

    @Override public void remove(ImageStrip[] strips) {
      if (proxyHelper != null) {
        final LienzoImageStrips proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.remove(strips);
      } else {
        super.remove(strips);
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final LienzoImageStrips proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        super.destroy();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LienzoImageStrips proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoImageStrips.class, "Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoImageStrips.class, Object.class });
  }

  public LienzoImageStrips createInstance(final ContextManager contextManager) {
    final LienzoImageStrips instance = new LienzoImageStrips();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoImageStrips) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoImageStrips instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LienzoImageStrips> proxyImpl = new Type_factory__o_k_w_c_s_c_l_c_LienzoImageStrips__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}