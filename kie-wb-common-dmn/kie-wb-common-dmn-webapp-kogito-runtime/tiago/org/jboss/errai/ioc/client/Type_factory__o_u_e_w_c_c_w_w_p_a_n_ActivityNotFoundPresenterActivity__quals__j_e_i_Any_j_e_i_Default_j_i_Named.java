package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractPopupActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PopupActivity;
import org.uberfire.client.views.pfly.popup.PopupViewImpl;
import org.uberfire.client.workbench.widgets.popup.PopupView;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenter;
import org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenterActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ActivityNotFoundPresenterActivity> { public Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(ActivityNotFoundPresenterActivity.class, "Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "workbench.activity.notfound", true));
    handle.setAssignableTypes(new Class[] { ActivityNotFoundPresenterActivity.class, AbstractPopupActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PopupActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("workbench.activity.notfound") });
  }

  public ActivityNotFoundPresenterActivity createInstance(final ContextManager contextManager) {
    final PopupView _view_1 = (PopupViewImpl) contextManager.getInstance("Type_factory__o_u_c_v_p_p_PopupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ActivityNotFoundPresenterActivity instance = new ActivityNotFoundPresenterActivity(_placeManager_0, _view_1);
    registerDependentScopedReference(instance, _view_1);
    setIncompleteInstance(instance);
    final ActivityNotFoundPresenter ActivityNotFoundPresenterActivity_realPresenter = (ActivityNotFoundPresenter) contextManager.getInstance("Type_factory__o_u_e_w_c_c_w_w_p_a_n_ActivityNotFoundPresenter__quals__j_e_i_Any_j_e_i_Default");
    ActivityNotFoundPresenterActivity_ActivityNotFoundPresenter_realPresenter(instance, ActivityNotFoundPresenterActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ActivityNotFoundPresenterActivity instance) {
    instance.init();
  }

  native static ActivityNotFoundPresenter ActivityNotFoundPresenterActivity_ActivityNotFoundPresenter_realPresenter(ActivityNotFoundPresenterActivity instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenterActivity::realPresenter;
  }-*/;

  native static void ActivityNotFoundPresenterActivity_ActivityNotFoundPresenter_realPresenter(ActivityNotFoundPresenterActivity instance, ActivityNotFoundPresenter value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.workbench.widgets.popups.activities.notfound.ActivityNotFoundPresenterActivity::realPresenter = value;
  }-*/;
}