package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.workbench.WorkbenchLayoutImpl;
import org.uberfire.client.workbench.WorkbenchLayoutInfo;
import org.uberfire.client.workbench.WorkbenchLayoutInfoImpl;

public class Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchLayoutInfoImpl> { private class Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchLayoutInfoImpl implements Proxy<WorkbenchLayoutInfoImpl> {
    private final ProxyHelper<WorkbenchLayoutInfoImpl> proxyHelper = new ProxyHelperImpl<WorkbenchLayoutInfoImpl>("Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchLayoutInfoImpl instance) {

    }

    public WorkbenchLayoutInfoImpl asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchLayoutInfoImpl instance) {
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

    @Override public int getHeaderHeight() {
      if (proxyHelper != null) {
        final WorkbenchLayoutInfoImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getHeaderHeight();
        return retVal;
      } else {
        return super.getHeaderHeight();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchLayoutInfoImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchLayoutInfoImpl.class, "Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchLayoutInfoImpl.class, Object.class, WorkbenchLayoutInfo.class });
  }

  public WorkbenchLayoutInfoImpl createInstance(final ContextManager contextManager) {
    final WorkbenchLayoutInfoImpl instance = new WorkbenchLayoutInfoImpl();
    setIncompleteInstance(instance);
    final WorkbenchLayoutImpl WorkbenchLayoutInfoImpl_workbenchLayout = (WorkbenchLayoutImpl) contextManager.getInstance("Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchLayoutInfoImpl_WorkbenchLayoutImpl_workbenchLayout(instance, WorkbenchLayoutInfoImpl_workbenchLayout);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchLayoutInfoImpl> proxyImpl = new Type_factory__o_u_c_w_WorkbenchLayoutInfoImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static WorkbenchLayoutImpl WorkbenchLayoutInfoImpl_WorkbenchLayoutImpl_workbenchLayout(WorkbenchLayoutInfoImpl instance) /*-{
    return instance.@org.uberfire.client.workbench.WorkbenchLayoutInfoImpl::workbenchLayout;
  }-*/;

  native static void WorkbenchLayoutInfoImpl_WorkbenchLayoutImpl_workbenchLayout(WorkbenchLayoutInfoImpl instance, WorkbenchLayoutImpl value) /*-{
    instance.@org.uberfire.client.workbench.WorkbenchLayoutInfoImpl::workbenchLayout = value;
  }-*/;
}