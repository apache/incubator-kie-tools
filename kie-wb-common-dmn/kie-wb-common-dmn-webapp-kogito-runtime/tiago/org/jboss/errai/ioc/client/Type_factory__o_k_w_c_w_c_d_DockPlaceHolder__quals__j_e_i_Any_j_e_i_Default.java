package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolder;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBase;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBaseView;
import org.kie.workbench.common.widgets.client.docks.DockPlaceHolderBaseViewImpl;

public class Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default extends Factory<DockPlaceHolder> { private class Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DockPlaceHolder implements Proxy<DockPlaceHolder> {
    private final ProxyHelper<DockPlaceHolder> proxyHelper = new ProxyHelperImpl<DockPlaceHolder>("Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DockPlaceHolder instance) {

    }

    public DockPlaceHolder asBeanType() {
      return this;
    }

    public void setInstance(final DockPlaceHolder instance) {
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

    @Override public void init(DockPlaceHolderBaseView view) {
      if (proxyHelper != null) {
        final DockPlaceHolder proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(view);
      } else {
        super.init(view);
      }
    }

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final DockPlaceHolder proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public IsWidget getView() {
      if (proxyHelper != null) {
        final DockPlaceHolder proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public void setView(IsWidget widget) {
      if (proxyHelper != null) {
        final DockPlaceHolder proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setView(widget);
      } else {
        super.setView(widget);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DockPlaceHolder proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DockPlaceHolder.class, "Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DockPlaceHolder.class, DockPlaceHolderBase.class, Object.class });
  }

  public DockPlaceHolder createInstance(final ContextManager contextManager) {
    final DockPlaceHolder instance = new DockPlaceHolder();
    setIncompleteInstance(instance);
    final DockPlaceHolderBaseView init_view_0 = (DockPlaceHolderBaseViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_d_DockPlaceHolderBaseViewImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, init_view_0);
    instance.init(init_view_0);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DockPlaceHolder> proxyImpl = new Type_factory__o_k_w_c_w_c_d_DockPlaceHolder__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}