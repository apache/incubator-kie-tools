package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.widgets.views.LoadingBox;
import org.kie.workbench.common.stunner.client.widgets.views.LoadingBox.View;
import org.kie.workbench.common.stunner.client.widgets.views.LoadingBoxView;

public class Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_Default extends Factory<LoadingBox> { private class Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LoadingBox implements Proxy<LoadingBox> {
    private final ProxyHelper<LoadingBox> proxyHelper = new ProxyHelperImpl<LoadingBox>("Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LoadingBox instance) {

    }

    public LoadingBox asBeanType() {
      return this;
    }

    public void setInstance(final LoadingBox instance) {
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

    @Override public void show() {
      if (proxyHelper != null) {
        final LoadingBox proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        super.show();
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final LoadingBox proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        super.hide();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LoadingBox proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LoadingBox.class, "Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LoadingBox.class, Object.class });
  }

  public LoadingBox createInstance(final ContextManager contextManager) {
    final View _view_0 = (LoadingBoxView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_LoadingBoxView__quals__j_e_i_Any_j_e_i_Default");
    final LoadingBox instance = new LoadingBox(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LoadingBox> proxyImpl = new Type_factory__o_k_w_c_s_c_w_v_LoadingBox__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}