package org.jboss.errai.ioc.client;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationMenu;
import org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationPresenter;
import org.uberfire.workbench.model.menu.MenuItem;

public class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchConfigurationMenu> { private class Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchConfigurationMenu implements Proxy<WorkbenchConfigurationMenu> {
    private final ProxyHelper<WorkbenchConfigurationMenu> proxyHelper = new ProxyHelperImpl<WorkbenchConfigurationMenu>("Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchConfigurationMenu instance) {

    }

    public WorkbenchConfigurationMenu asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchConfigurationMenu instance) {
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

    @Override public void setup() {
      if (proxyHelper != null) {
        final WorkbenchConfigurationMenu proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup();
      } else {
        super.setup();
      }
    }

    @Override public List getMenuItems() {
      if (proxyHelper != null) {
        final WorkbenchConfigurationMenu proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getMenuItems();
        return retVal;
      } else {
        return super.getMenuItems();
      }
    }

    @Override public MenuItem getToplevelMenu() {
      if (proxyHelper != null) {
        final WorkbenchConfigurationMenu proxiedInstance = proxyHelper.getInstance(this);
        final MenuItem retVal = proxiedInstance.getToplevelMenu();
        return retVal;
      } else {
        return super.getToplevelMenu();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchConfigurationMenu proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchConfigurationMenu.class, "Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchConfigurationMenu.class, Object.class });
  }

  public WorkbenchConfigurationMenu createInstance(final ContextManager contextManager) {
    final WorkbenchConfigurationMenu instance = new WorkbenchConfigurationMenu();
    setIncompleteInstance(instance);
    final SyncBeanManager WorkbenchConfigurationMenu_iocBeanManager = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, WorkbenchConfigurationMenu_iocBeanManager);
    WorkbenchConfigurationMenu_SyncBeanManager_iocBeanManager(instance, WorkbenchConfigurationMenu_iocBeanManager);
    final WorkbenchConfigurationPresenter WorkbenchConfigurationMenu_newResourcePresenter = (WorkbenchConfigurationPresenter) contextManager.getInstance("Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationPresenter__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchConfigurationMenu_WorkbenchConfigurationPresenter_newResourcePresenter(instance, WorkbenchConfigurationMenu_newResourcePresenter);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final WorkbenchConfigurationMenu instance) {
    instance.setup();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchConfigurationMenu> proxyImpl = new Type_factory__o_k_w_c_w_c_h_w_c_WorkbenchConfigurationMenu__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static SyncBeanManager WorkbenchConfigurationMenu_SyncBeanManager_iocBeanManager(WorkbenchConfigurationMenu instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationMenu::iocBeanManager;
  }-*/;

  native static void WorkbenchConfigurationMenu_SyncBeanManager_iocBeanManager(WorkbenchConfigurationMenu instance, SyncBeanManager value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationMenu::iocBeanManager = value;
  }-*/;

  native static WorkbenchConfigurationPresenter WorkbenchConfigurationMenu_WorkbenchConfigurationPresenter_newResourcePresenter(WorkbenchConfigurationMenu instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationMenu::newResourcePresenter;
  }-*/;

  native static void WorkbenchConfigurationMenu_WorkbenchConfigurationPresenter_newResourcePresenter(WorkbenchConfigurationMenu instance, WorkbenchConfigurationPresenter value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.workbench.configuration.WorkbenchConfigurationMenu::newResourcePresenter = value;
  }-*/;
}