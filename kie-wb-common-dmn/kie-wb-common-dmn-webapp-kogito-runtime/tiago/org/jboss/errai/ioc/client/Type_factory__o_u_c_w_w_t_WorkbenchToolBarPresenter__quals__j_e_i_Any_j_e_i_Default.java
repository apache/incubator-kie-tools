package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.views.pfly.toolbar.WorkbenchToolBarView;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter;
import org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter.View;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.toolbar.ToolBar;

public class Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchToolBarPresenter> { private class Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchToolBarPresenter implements Proxy<WorkbenchToolBarPresenter> {
    private final ProxyHelper<WorkbenchToolBarPresenter> proxyHelper = new ProxyHelperImpl<WorkbenchToolBarPresenter>("Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchToolBarPresenter instance) {

    }

    public WorkbenchToolBarPresenter asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchToolBarPresenter instance) {
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

    @Override public IsWidget getView() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        final IsWidget retVal = proxiedInstance.getView();
        return retVal;
      } else {
        return super.getView();
      }
    }

    @Override public int getHeight() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getHeight();
        return retVal;
      } else {
        return super.getHeight();
      }
    }

    @Override public void hide() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide();
      } else {
        super.hide();
      }
    }

    @Override public void show() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show();
      } else {
        super.show();
      }
    }

    @Override public void addItemsFor(PlaceRequest place) {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addItemsFor(place);
      } else {
        super.addItemsFor(place);
      }
    }

    @Override public void addWorkbenchItem(ToolBar toolBar) {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addWorkbenchItem(toolBar);
      } else {
        super.addWorkbenchItem(toolBar);
      }
    }

    @Override public void addWorkbenchPerspective(ToolBar toolBar) {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addWorkbenchPerspective(toolBar);
      } else {
        super.addWorkbenchPerspective(toolBar);
      }
    }

    @Override public void clearWorkbenchItems() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clearWorkbenchItems();
      } else {
        super.clearWorkbenchItems();
      }
    }

    @Override public void clearWorkbenchPerspectiveItems() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clearWorkbenchPerspectiveItems();
      } else {
        super.clearWorkbenchPerspectiveItems();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchToolBarPresenter proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchToolBarPresenter.class, "Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchToolBarPresenter.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.ClosePlaceEvent", new AbstractCDIEventCallback<ClosePlaceEvent>() {
      public void fireEvent(final ClosePlaceEvent event) {
        final WorkbenchToolBarPresenter instance = Factory.maybeUnwrapProxy((WorkbenchToolBarPresenter) context.getInstance("Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default"));
        WorkbenchToolBarPresenter_onWorkbenchPartClose_ClosePlaceEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.ClosePlaceEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceGainFocusEvent", new AbstractCDIEventCallback<PlaceGainFocusEvent>() {
      public void fireEvent(final PlaceGainFocusEvent event) {
        final WorkbenchToolBarPresenter instance = Factory.maybeUnwrapProxy((WorkbenchToolBarPresenter) context.getInstance("Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_Default"));
        WorkbenchToolBarPresenter_onWorkbenchPartOnFocus_PlaceGainFocusEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceGainFocusEvent []";
      }
    });
  }

  public WorkbenchToolBarPresenter createInstance(final ContextManager contextManager) {
    final WorkbenchToolBarPresenter instance = new WorkbenchToolBarPresenter();
    setIncompleteInstance(instance);
    final PlaceManagerImpl WorkbenchToolBarPresenter_placeManager = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    WorkbenchToolBarPresenter_PlaceManager_placeManager(instance, WorkbenchToolBarPresenter_placeManager);
    final WorkbenchToolBarView WorkbenchToolBarPresenter_view = (WorkbenchToolBarView) contextManager.getInstance("Type_factory__o_u_c_v_p_t_WorkbenchToolBarView__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, WorkbenchToolBarPresenter_view);
    WorkbenchToolBarPresenter_View_view(instance, WorkbenchToolBarPresenter_view);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchToolBarPresenter> proxyImpl = new Type_factory__o_u_c_w_w_t_WorkbenchToolBarPresenter__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceManager WorkbenchToolBarPresenter_PlaceManager_placeManager(WorkbenchToolBarPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter::placeManager;
  }-*/;

  native static void WorkbenchToolBarPresenter_PlaceManager_placeManager(WorkbenchToolBarPresenter instance, PlaceManager value) /*-{
    instance.@org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter::placeManager = value;
  }-*/;

  native static View WorkbenchToolBarPresenter_View_view(WorkbenchToolBarPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter::view;
  }-*/;

  native static void WorkbenchToolBarPresenter_View_view(WorkbenchToolBarPresenter instance, View value) /*-{
    instance.@org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter::view = value;
  }-*/;

  public native static void WorkbenchToolBarPresenter_onWorkbenchPartOnFocus_PlaceGainFocusEvent(WorkbenchToolBarPresenter instance, PlaceGainFocusEvent a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter::onWorkbenchPartOnFocus(Lorg/uberfire/client/workbench/events/PlaceGainFocusEvent;)(a0);
  }-*/;

  public native static void WorkbenchToolBarPresenter_onWorkbenchPartClose_ClosePlaceEvent(WorkbenchToolBarPresenter instance, ClosePlaceEvent a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.toolbar.WorkbenchToolBarPresenter::onWorkbenchPartClose(Lorg/uberfire/client/workbench/events/ClosePlaceEvent;)(a0);
  }-*/;
}