package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs.View;
import org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbsView;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainer;
import org.uberfire.ext.widgets.common.client.breadcrumbs.header.UberfireBreadcrumbsContainerImpl;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter;

public class Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireBreadcrumbs> { public Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireBreadcrumbs.class, "Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { UberfireBreadcrumbs.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PerspectiveChange", new AbstractCDIEventCallback<PerspectiveChange>() {
      public void fireEvent(final PerspectiveChange event) {
        final UberfireBreadcrumbs instance = Factory.maybeUnwrapProxy((UberfireBreadcrumbs) context.getInstance("Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbs__quals__j_e_i_Any_j_e_i_Default"));
        UberfireBreadcrumbs_perspectiveChangeEvent_PerspectiveChange(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PerspectiveChange []";
      }
    });
  }

  public UberfireBreadcrumbs createInstance(final ContextManager contextManager) {
    final UberfireBreadcrumbsContainer _uberfireBreadcrumbsContainer_0 = (UberfireBreadcrumbsContainerImpl) contextManager.getInstance("Type_factory__o_u_e_w_c_c_b_h_UberfireBreadcrumbsContainerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DefaultBreadcrumbsPresenter> _breadcrumbsPresenters_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DefaultBreadcrumbsPresenter.class }, new Annotation[] { });
    final PlaceManager _placeManager_2 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final View _view_3 = (UberfireBreadcrumbsView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_b_UberfireBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default");
    final UberfireBreadcrumbs instance = new UberfireBreadcrumbs(_uberfireBreadcrumbsContainer_0, _breadcrumbsPresenters_1, _placeManager_2, _view_3);
    registerDependentScopedReference(instance, _breadcrumbsPresenters_1);
    registerDependentScopedReference(instance, _view_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UberfireBreadcrumbs instance) {
    instance.createBreadcrumbs();
  }

  public native static void UberfireBreadcrumbs_perspectiveChangeEvent_PerspectiveChange(UberfireBreadcrumbs instance, PerspectiveChange a0) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.UberfireBreadcrumbs::perspectiveChangeEvent(Lorg/uberfire/client/workbench/events/PerspectiveChange;)(a0);
  }-*/;
}