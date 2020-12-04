package org.jboss.errai.ioc.client;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.DefaultBeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragContext;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;

public class Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchDragAndDropManager> { private class Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchDragAndDropManager implements Proxy<WorkbenchDragAndDropManager> {
    private final ProxyHelper<WorkbenchDragAndDropManager> proxyHelper = new ProxyHelperImpl<WorkbenchDragAndDropManager>("Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchDragAndDropManager instance) {

    }

    public WorkbenchDragAndDropManager asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchDragAndDropManager instance) {
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

    @Override public void makeDraggable(IsWidget draggable, IsWidget dragHandle) {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.makeDraggable(draggable, dragHandle);
      } else {
        super.makeDraggable(draggable, dragHandle);
      }
    }

    @Override public void registerDropController(WorkbenchPanelView owner, DropController dropController) {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerDropController(owner, dropController);
      } else {
        super.registerDropController(owner, dropController);
      }
    }

    @Override public void unregisterDropController(WorkbenchPanelView view) {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unregisterDropController(view);
      } else {
        super.unregisterDropController(view);
      }
    }

    @Override public void unregisterDropControllers() {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unregisterDropControllers();
      } else {
        super.unregisterDropControllers();
      }
    }

    @Override public WorkbenchDragContext getWorkbenchContext() {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchDragContext retVal = proxiedInstance.getWorkbenchContext();
        return retVal;
      } else {
        return super.getWorkbenchContext();
      }
    }

    @Override public void setWorkbenchContext(WorkbenchDragContext workbenchContext) {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setWorkbenchContext(workbenchContext);
      } else {
        super.setWorkbenchContext(workbenchContext);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchDragAndDropManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchDragAndDropManager.class, "Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchDragAndDropManager.class, Object.class });
  }

  public WorkbenchDragAndDropManager createInstance(final ContextManager contextManager) {
    final WorkbenchDragAndDropManager instance = new WorkbenchDragAndDropManager();
    setIncompleteInstance(instance);
    final DefaultBeanFactory WorkbenchDragAndDropManager_factory = (DefaultBeanFactory) contextManager.getInstance("Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchDragAndDropManager_BeanFactory_factory(instance, WorkbenchDragAndDropManager_factory);
    final WorkbenchPickupDragController WorkbenchDragAndDropManager_dragController = (WorkbenchPickupDragController) contextManager.getInstance("Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchDragAndDropManager_WorkbenchPickupDragController_dragController(instance, WorkbenchDragAndDropManager_dragController);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchDragAndDropManager> proxyImpl = new Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static WorkbenchPickupDragController WorkbenchDragAndDropManager_WorkbenchPickupDragController_dragController(WorkbenchDragAndDropManager instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager::dragController;
  }-*/;

  native static void WorkbenchDragAndDropManager_WorkbenchPickupDragController_dragController(WorkbenchDragAndDropManager instance, WorkbenchPickupDragController value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager::dragController = value;
  }-*/;

  native static BeanFactory WorkbenchDragAndDropManager_BeanFactory_factory(WorkbenchDragAndDropManager instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager::factory;
  }-*/;

  native static void WorkbenchDragAndDropManager_BeanFactory_factory(WorkbenchDragAndDropManager instance, BeanFactory value) /*-{
    instance.@org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager::factory = value;
  }-*/;
}