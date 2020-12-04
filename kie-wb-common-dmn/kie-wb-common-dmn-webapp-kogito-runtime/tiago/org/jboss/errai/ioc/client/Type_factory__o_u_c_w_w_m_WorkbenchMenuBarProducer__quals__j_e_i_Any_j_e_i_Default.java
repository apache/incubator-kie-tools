package org.jboss.errai.ioc.client;

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
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.views.pfly.menu.WorkbenchMenuBarView;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.AbstractWorkbenchMenuProducer;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter.View;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.experimental.client.service.auth.ExperimentalActivitiesAuthorizationManagerImpl;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;

public class Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuBarProducer> { private class Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends WorkbenchMenuBarProducer implements Proxy<WorkbenchMenuBarProducer> {
    private final ProxyHelper<WorkbenchMenuBarProducer> proxyHelper = new ProxyHelperImpl<WorkbenchMenuBarProducer>("Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final WorkbenchMenuBarProducer instance) {

    }

    public WorkbenchMenuBarProducer asBeanType() {
      return this;
    }

    public void setInstance(final WorkbenchMenuBarProducer instance) {
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

    @Override public WorkbenchMenuBarPresenter getInstance() {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchMenuBarPresenter retVal = proxiedInstance.getInstance();
        return retVal;
      } else {
        return super.getInstance();
      }
    }

    @Override protected WorkbenchMenuBarPresenter makeDefaultPresenter() {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchMenuBarPresenter retVal = WorkbenchMenuBarProducer_makeDefaultPresenter(proxiedInstance);
        return retVal;
      } else {
        return super.makeDefaultPresenter();
      }
    }

    @Override protected WorkbenchMenuBarPresenter makeStandalonePresenter() {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchMenuBarPresenter retVal = WorkbenchMenuBarProducer_makeStandalonePresenter(proxiedInstance);
        return retVal;
      } else {
        return super.makeStandalonePresenter();
      }
    }

    @Override protected void onPlaceMinimized(PlaceMinimizedEvent event) {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        WorkbenchMenuBarProducer_onPlaceMinimized_PlaceMinimizedEvent(proxiedInstance, event);
      } else {
        super.onPlaceMinimized(event);
      }
    }

    @Override protected void onPlaceMaximized(PlaceMaximizedEvent event) {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        WorkbenchMenuBarProducer_onPlaceMaximized_PlaceMaximizedEvent(proxiedInstance, event);
      } else {
        super.onPlaceMaximized(event);
      }
    }

    @Override public WorkbenchMenuBarPresenter getWorbenchMenu() {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        final WorkbenchMenuBarPresenter retVal = proxiedInstance.getWorbenchMenu();
        return retVal;
      } else {
        return super.getWorbenchMenu();
      }
    }

    @Override protected void onPerspectiveChange(PerspectiveChange perspectiveChange) {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        AbstractWorkbenchMenuProducer_onPerspectiveChange_PerspectiveChange(proxiedInstance, perspectiveChange);
      } else {
        super.onPerspectiveChange(perspectiveChange);
      }
    }

    @Override protected void onPerspectiveHide(PerspectiveVisibiltiyChangeEvent setPerspectiveVisibleEvent) {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        AbstractWorkbenchMenuProducer_onPerspectiveHide_PerspectiveVisibiltiyChangeEvent(proxiedInstance, setPerspectiveVisibleEvent);
      } else {
        super.onPerspectiveHide(setPerspectiveVisibleEvent);
      }
    }

    @Override protected boolean isStandalone() {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = AbstractWorkbenchMenuProducer_isStandalone(proxiedInstance);
        return retVal;
      } else {
        return super.isStandalone();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final WorkbenchMenuBarProducer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchMenuBarProducer.class, "Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchMenuBarProducer.class, AbstractWorkbenchMenuProducer.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMinimizedEvent", new AbstractCDIEventCallback<PlaceMinimizedEvent>() {
      public void fireEvent(final PlaceMinimizedEvent event) {
        final WorkbenchMenuBarProducer instance = Factory.maybeUnwrapProxy((WorkbenchMenuBarProducer) context.getInstance("Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default"));
        WorkbenchMenuBarProducer_onPlaceMinimized_PlaceMinimizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMinimizedEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMaximizedEvent", new AbstractCDIEventCallback<PlaceMaximizedEvent>() {
      public void fireEvent(final PlaceMaximizedEvent event) {
        final WorkbenchMenuBarProducer instance = Factory.maybeUnwrapProxy((WorkbenchMenuBarProducer) context.getInstance("Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default"));
        WorkbenchMenuBarProducer_onPlaceMaximized_PlaceMaximizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMaximizedEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PerspectiveChange", new AbstractCDIEventCallback<PerspectiveChange>() {
      public void fireEvent(final PerspectiveChange event) {
        final WorkbenchMenuBarProducer instance = Factory.maybeUnwrapProxy((WorkbenchMenuBarProducer) context.getInstance("Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default"));
        AbstractWorkbenchMenuProducer_onPerspectiveChange_PerspectiveChange(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PerspectiveChange []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent", new AbstractCDIEventCallback<PerspectiveVisibiltiyChangeEvent>() {
      public void fireEvent(final PerspectiveVisibiltiyChangeEvent event) {
        final WorkbenchMenuBarProducer instance = Factory.maybeUnwrapProxy((WorkbenchMenuBarProducer) context.getInstance("Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default"));
        AbstractWorkbenchMenuProducer_onPerspectiveHide_PerspectiveVisibiltiyChangeEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent []";
      }
    });
  }

  public WorkbenchMenuBarProducer createInstance(final ContextManager contextManager) {
    final AuthorizationManager _authzManager_0 = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    final PerspectiveManager _perspectiveManager_1 = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_2 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final View _view_6 = (WorkbenchMenuBarView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_WorkbenchMenuBarView__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalActivitiesAuthorizationManager _experimentalActivitiesAuthorizationManager_5 = (ExperimentalActivitiesAuthorizationManagerImpl) contextManager.getInstance("Type_factory__o_u_e_c_s_a_ExperimentalActivitiesAuthorizationManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ActivityManager _activityManager_3 = (ActivityManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final User _identity_4 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchMenuBarProducer instance = new WorkbenchMenuBarProducer(_authzManager_0, _perspectiveManager_1, _placeManager_2, _activityManager_3, _identity_4, _experimentalActivitiesAuthorizationManager_5, _view_6);
    registerDependentScopedReference(instance, _identity_4);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<WorkbenchMenuBarProducer> proxyImpl = new Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void WorkbenchMenuBarProducer_onPlaceMaximized_PlaceMaximizedEvent(WorkbenchMenuBarProducer instance, PlaceMaximizedEvent a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer::onPlaceMaximized(Lorg/uberfire/client/workbench/events/PlaceMaximizedEvent;)(a0);
  }-*/;

  public native static void AbstractWorkbenchMenuProducer_onPerspectiveChange_PerspectiveChange(AbstractWorkbenchMenuProducer instance, PerspectiveChange a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.AbstractWorkbenchMenuProducer::onPerspectiveChange(Lorg/uberfire/client/workbench/events/PerspectiveChange;)(a0);
  }-*/;

  public native static WorkbenchMenuBarPresenter WorkbenchMenuBarProducer_makeStandalonePresenter(WorkbenchMenuBarProducer instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer::makeStandalonePresenter()();
  }-*/;

  public native static WorkbenchMenuBarPresenter WorkbenchMenuBarProducer_makeDefaultPresenter(WorkbenchMenuBarProducer instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer::makeDefaultPresenter()();
  }-*/;

  public native static void AbstractWorkbenchMenuProducer_onPerspectiveHide_PerspectiveVisibiltiyChangeEvent(AbstractWorkbenchMenuProducer instance, PerspectiveVisibiltiyChangeEvent a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.AbstractWorkbenchMenuProducer::onPerspectiveHide(Lorg/uberfire/client/workbench/widgets/menu/events/PerspectiveVisibiltiyChangeEvent;)(a0);
  }-*/;

  public native static boolean AbstractWorkbenchMenuProducer_isStandalone(AbstractWorkbenchMenuProducer instance) /*-{
    return instance.@org.uberfire.client.workbench.widgets.menu.AbstractWorkbenchMenuProducer::isStandalone()();
  }-*/;

  public native static void WorkbenchMenuBarProducer_onPlaceMinimized_PlaceMinimizedEvent(WorkbenchMenuBarProducer instance, PlaceMinimizedEvent a0) /*-{
    instance.@org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer::onPlaceMinimized(Lorg/uberfire/client/workbench/events/PlaceMinimizedEvent;)(a0);
  }-*/;
}