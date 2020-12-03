package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.dmn.client.docks.navigator.common.CanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;

public class Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<LazyCanvasFocusUtils> { private class Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LazyCanvasFocusUtils implements Proxy<LazyCanvasFocusUtils> {
    private final ProxyHelper<LazyCanvasFocusUtils> proxyHelper = new ProxyHelperImpl<LazyCanvasFocusUtils>("Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LazyCanvasFocusUtils instance) {

    }

    public LazyCanvasFocusUtils asBeanType() {
      return this;
    }

    public void setInstance(final LazyCanvasFocusUtils instance) {
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

    @Override public void lazyFocus(String nodeUUID) {
      if (proxyHelper != null) {
        final LazyCanvasFocusUtils proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.lazyFocus(nodeUUID);
      } else {
        super.lazyFocus(nodeUUID);
      }
    }

    @Override public void releaseFocus() {
      if (proxyHelper != null) {
        final LazyCanvasFocusUtils proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.releaseFocus();
      } else {
        super.releaseFocus();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LazyCanvasFocusUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LazyCanvasFocusUtils.class, "Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LazyCanvasFocusUtils.class, Object.class });
  }

  public LazyCanvasFocusUtils createInstance(final ContextManager contextManager) {
    final CanvasFocusUtils _canvasFocusUtils_0 = (CanvasFocusUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default");
    final LazyCanvasFocusUtils instance = new LazyCanvasFocusUtils(_canvasFocusUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LazyCanvasFocusUtils> proxyImpl = new Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}