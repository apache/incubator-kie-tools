package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.WorkbenchLayout;

public class Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutSelection> { private class Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LayoutSelection implements Proxy<LayoutSelection> {
    private final ProxyHelper<LayoutSelection> proxyHelper = new ProxyHelperImpl<LayoutSelection>("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LayoutSelection instance) {

    }

    public LayoutSelection asBeanType() {
      return this;
    }

    public void setInstance(final LayoutSelection instance) {
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

    @Override public WorkbenchLayout get() {
      if (proxyHelper != null) {
        final LayoutSelection proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchLayout retVal = proxiedInstance.get();
        return retVal;
      } else {
        return super.get();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LayoutSelection proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutSelection.class, "Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutSelection.class, Object.class });
  }

  public LayoutSelection createInstance(final ContextManager contextManager) {
    final LayoutSelection instance = new LayoutSelection();
    setIncompleteInstance(instance);
    final SyncBeanManager LayoutSelection_iocManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LayoutSelection_iocManager);
    LayoutSelection_SyncBeanManager_iocManager(instance, LayoutSelection_iocManager);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LayoutSelection> proxyImpl = new Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static SyncBeanManager LayoutSelection_SyncBeanManager_iocManager(LayoutSelection instance) /*-{
    return instance.@org.uberfire.client.workbench.LayoutSelection::iocManager;
  }-*/;

  native static void LayoutSelection_SyncBeanManager_iocManager(LayoutSelection instance, SyncBeanManager value) /*-{
    instance.@org.uberfire.client.workbench.LayoutSelection::iocManager = value;
  }-*/;
}