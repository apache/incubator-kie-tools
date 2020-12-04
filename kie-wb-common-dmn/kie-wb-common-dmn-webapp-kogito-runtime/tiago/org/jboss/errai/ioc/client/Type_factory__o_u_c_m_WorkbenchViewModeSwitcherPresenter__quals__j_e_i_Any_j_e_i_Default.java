package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter.View;
import org.uberfire.client.views.pfly.menu.WorkbenchViewModeSwitcherView;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;

public class Type_factory__o_u_c_m_WorkbenchViewModeSwitcherPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchViewModeSwitcherPresenter> { public Type_factory__o_u_c_m_WorkbenchViewModeSwitcherPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchViewModeSwitcherPresenter.class, "Type_factory__o_u_c_m_WorkbenchViewModeSwitcherPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchViewModeSwitcherPresenter.class, Object.class, IsWidget.class });
  }

  public WorkbenchViewModeSwitcherPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (WorkbenchViewModeSwitcherView) contextManager.getInstance("Type_factory__o_u_c_v_p_m_WorkbenchViewModeSwitcherView__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchMenuBar _menubar_1 = (WorkbenchMenuBarPresenter) contextManager.getInstance("Producer_factory__o_u_c_w_w_m_WorkbenchMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchViewModeSwitcherPresenter instance = new WorkbenchViewModeSwitcherPresenter(_view_0, _menubar_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _menubar_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onPerspectiveChangeSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.events.PerspectiveChange", new AbstractCDIEventCallback<PerspectiveChange>() {
      public void fireEvent(final PerspectiveChange event) {
        WorkbenchViewModeSwitcherPresenter_onPerspectiveChange_PerspectiveChange(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PerspectiveChange []";
      }
    }));
    thisInstance.setReference(instance, "onPlaceMinimizedSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMinimizedEvent", new AbstractCDIEventCallback<PlaceMinimizedEvent>() {
      public void fireEvent(final PlaceMinimizedEvent event) {
        WorkbenchViewModeSwitcherPresenter_onPlaceMinimized_PlaceMinimizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMinimizedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onPlaceMaximizedSubscription", CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceMaximizedEvent", new AbstractCDIEventCallback<PlaceMaximizedEvent>() {
      public void fireEvent(final PlaceMaximizedEvent event) {
        WorkbenchViewModeSwitcherPresenter_onPlaceMaximized_PlaceMaximizedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceMaximizedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((WorkbenchViewModeSwitcherPresenter) instance, contextManager);
  }

  public void destroyInstanceHelper(final WorkbenchViewModeSwitcherPresenter instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onPerspectiveChangeSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onPlaceMinimizedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onPlaceMaximizedSubscription", Subscription.class)).remove();
  }

  public native static void WorkbenchViewModeSwitcherPresenter_onPlaceMinimized_PlaceMinimizedEvent(WorkbenchViewModeSwitcherPresenter instance, PlaceMinimizedEvent a0) /*-{
    instance.@org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter::onPlaceMinimized(Lorg/uberfire/client/workbench/events/PlaceMinimizedEvent;)(a0);
  }-*/;

  public native static void WorkbenchViewModeSwitcherPresenter_onPlaceMaximized_PlaceMaximizedEvent(WorkbenchViewModeSwitcherPresenter instance, PlaceMaximizedEvent a0) /*-{
    instance.@org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter::onPlaceMaximized(Lorg/uberfire/client/workbench/events/PlaceMaximizedEvent;)(a0);
  }-*/;

  public native static void WorkbenchViewModeSwitcherPresenter_onPerspectiveChange_PerspectiveChange(WorkbenchViewModeSwitcherPresenter instance, PerspectiveChange a0) /*-{
    instance.@org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter::onPerspectiveChange(Lorg/uberfire/client/workbench/events/PerspectiveChange;)(a0);
  }-*/;
}