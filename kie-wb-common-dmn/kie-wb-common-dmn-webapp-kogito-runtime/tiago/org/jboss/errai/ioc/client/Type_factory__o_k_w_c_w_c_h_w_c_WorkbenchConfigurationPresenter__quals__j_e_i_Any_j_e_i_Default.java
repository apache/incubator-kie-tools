package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationHandler;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter.WorkbenchConfigurationView;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationViewImpl;

public class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchConfigurationPresenter> { private class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchConfigurationPresenter implements Proxy<WorkbenchConfigurationPresenter> {
    private final ProxyHelper<WorkbenchConfigurationPresenter> proxyHelper = new ProxyHelperImpl<WorkbenchConfigurationPresenter>("Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchConfigurationPresenter instance) {

    }

    public WorkbenchConfigurationPresenter asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchConfigurationPresenter instance) {
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

    @Override public void show(WorkbenchConfigurationHandler handler) {
      if (proxyHelper != null) {
        final WorkbenchConfigurationPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(handler);
      } else {
        super.show(handler);
      }
    }

    @Override public void complete() {
      if (proxyHelper != null) {
        final WorkbenchConfigurationPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.complete();
      } else {
        super.complete();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchConfigurationPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchConfigurationPresenter.class, "Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchConfigurationPresenter.class, Object.class });
  }

  public WorkbenchConfigurationPresenter createInstance(final ContextManager contextManager) {
    final WorkbenchConfigurationPresenter instance = new WorkbenchConfigurationPresenter();
    setIncompleteInstance(instance);
    final WorkbenchConfigurationViewImpl WorkbenchConfigurationPresenter_view = (WorkbenchConfigurationViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationViewImpl__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchConfigurationPresenter_WorkbenchConfigurationView_view(instance, WorkbenchConfigurationPresenter_view);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final WorkbenchConfigurationPresenter instance) {
    WorkbenchConfigurationPresenter_setup(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchConfigurationPresenter> proxyImpl = new Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static WorkbenchConfigurationView WorkbenchConfigurationPresenter_WorkbenchConfigurationView_view(WorkbenchConfigurationPresenter instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter::view;
  }-*/;

  native static void WorkbenchConfigurationPresenter_WorkbenchConfigurationView_view(WorkbenchConfigurationPresenter instance, WorkbenchConfigurationView value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter::view = value;
  }-*/;

  public native static void WorkbenchConfigurationPresenter_setup(WorkbenchConfigurationPresenter instance) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter::setup()();
  }-*/;
}