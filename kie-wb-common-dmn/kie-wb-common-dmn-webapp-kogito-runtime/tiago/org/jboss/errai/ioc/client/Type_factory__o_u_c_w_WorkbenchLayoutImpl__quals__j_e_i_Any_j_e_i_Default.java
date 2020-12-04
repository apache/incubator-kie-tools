package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.WorkbenchLayoutImpl;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.client.workbench.events.WorkbenchProfileCssClass;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchPickupDragController;
import org.uberfire.mvp.Command;

public class Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchLayoutImpl> { private class Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchLayoutImpl implements Proxy<WorkbenchLayoutImpl> {
    private final ProxyHelper<WorkbenchLayoutImpl> proxyHelper = new ProxyHelperImpl<WorkbenchLayoutImpl>("Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchLayoutImpl instance) {

    }

    public WorkbenchLayoutImpl asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchLayoutImpl instance) {
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

    @Override public HeaderPanel getRoot() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        final HeaderPanel retVal = proxiedInstance.getRoot();
        return retVal;
      } else {
        return super.getRoot();
      }
    }

    @Override public HasWidgets getPerspectiveContainer() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        final HasWidgets retVal = proxiedInstance.getPerspectiveContainer();
        return retVal;
      } else {
        return super.getPerspectiveContainer();
      }
    }

    @Override public void onBootstrap() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onBootstrap();
      } else {
        super.onBootstrap();
      }
    }

    @Override public void onResize() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onResize();
      } else {
        super.onResize();
      }
    }

    @Override public void resizeTo(int width, int height) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resizeTo(width, height);
      } else {
        super.resizeTo(width, height);
      }
    }

    @Override public void maximize(Widget w) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.maximize(w);
      } else {
        super.maximize(w);
      }
    }

    @Override public void maximize(Widget w, Command callback) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.maximize(w, callback);
      } else {
        super.maximize(w, callback);
      }
    }

    @Override public void unmaximize(Widget w) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unmaximize(w);
      } else {
        super.unmaximize(w);
      }
    }

    @Override public void unmaximize(Widget w, Command callback) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unmaximize(w, callback);
      } else {
        super.unmaximize(w, callback);
      }
    }

    @Override public void setMarginWidgets(boolean isStandaloneMode, Set headersToKeep) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setMarginWidgets(isStandaloneMode, headersToKeep);
      } else {
        super.setMarginWidgets(isStandaloneMode, headersToKeep);
      }
    }

    @Override public void addWorkbenchProfileCssClass(WorkbenchProfileCssClass workbenchProfileCssClass) {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addWorkbenchProfileCssClass(workbenchProfileCssClass);
      } else {
        super.addWorkbenchProfileCssClass(workbenchProfileCssClass);
      }
    }

    @Override protected Div getHeaderPanel() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        final Div retVal = WorkbenchLayoutImpl_getHeaderPanel(proxiedInstance);
        return retVal;
      } else {
        return super.getHeaderPanel();
      }
    }

    @Override protected Div getFooterPanel() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        final Div retVal = WorkbenchLayoutImpl_getFooterPanel(proxiedInstance);
        return retVal;
      } else {
        return super.getFooterPanel();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchLayoutImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchLayoutImpl.class, "Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchLayoutImpl.class, Object.class, WorkbenchLayout.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.WorkbenchProfileCssClass", new AbstractCDIEventCallback<WorkbenchProfileCssClass>() {
      public void fireEvent(final WorkbenchProfileCssClass event) {
        final WorkbenchLayoutImpl instance = Factory.maybeUnwrapProxy((WorkbenchLayoutImpl) context.getInstance("Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_Default"));
        instance.addWorkbenchProfileCssClass(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.WorkbenchProfileCssClass []";
      }
    });
  }

  public WorkbenchLayoutImpl createInstance(final ContextManager contextManager) {
    final HeaderPanel _root_1 = (HeaderPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_HeaderPanel__quals__j_e_i_Any_j_e_i_Default");
    final UberfireDocksContainer _uberfireDocksContainer_3 = (UberfireDocksContainer) contextManager.getInstance("Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default");
    final Div _footerPanel_6 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final SyncBeanManager _iocManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchDragAndDropManager _dndManager_2 = (WorkbenchDragAndDropManager) contextManager.getInstance("Type_factory__o_u_c_w_w_d_WorkbenchDragAndDropManager__quals__j_e_i_Any_j_e_i_Default");
    final Div _headerPanel_5 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final WorkbenchPickupDragController _dragController_4 = (WorkbenchPickupDragController) contextManager.getInstance("Type_factory__o_u_c_w_w_d_WorkbenchPickupDragController__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchLayoutImpl instance = new WorkbenchLayoutImpl(_iocManager_0, _root_1, _dndManager_2, _uberfireDocksContainer_3, _dragController_4, _headerPanel_5, _footerPanel_6);
    registerDependentScopedReference(instance, _root_1);
    registerDependentScopedReference(instance, _footerPanel_6);
    registerDependentScopedReference(instance, _iocManager_0);
    registerDependentScopedReference(instance, _headerPanel_5);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final WorkbenchLayoutImpl instance) {
    WorkbenchLayoutImpl_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchLayoutImpl> proxyImpl = new Type_factory__o_u_c_w_WorkbenchLayoutImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static Div WorkbenchLayoutImpl_getHeaderPanel(WorkbenchLayoutImpl instance) /*-{
    return instance.@org.uberfire.client.workbench.WorkbenchLayoutImpl::getHeaderPanel()();
  }-*/;

  public native static void WorkbenchLayoutImpl_init(WorkbenchLayoutImpl instance) /*-{
    instance.@org.uberfire.client.workbench.WorkbenchLayoutImpl::init()();
  }-*/;

  public native static Div WorkbenchLayoutImpl_getFooterPanel(WorkbenchLayoutImpl instance) /*-{
    return instance.@org.uberfire.client.workbench.WorkbenchLayoutImpl::getFooterPanel()();
  }-*/;
}