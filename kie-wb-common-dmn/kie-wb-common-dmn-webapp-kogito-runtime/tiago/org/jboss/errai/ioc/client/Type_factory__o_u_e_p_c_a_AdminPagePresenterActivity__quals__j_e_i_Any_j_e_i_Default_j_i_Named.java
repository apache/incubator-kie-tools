package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenter;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenterActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_p_c_a_AdminPagePresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AdminPagePresenterActivity> { public Type_factory__o_u_e_p_c_a_AdminPagePresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(AdminPagePresenterActivity.class, "Type_factory__o_u_e_p_c_a_AdminPagePresenterActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "AdminPagePresenter", true));
    handle.setAssignableTypes(new Class[] { AdminPagePresenterActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("AdminPagePresenter") });
  }

  public AdminPagePresenterActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final AdminPagePresenterActivity instance = new AdminPagePresenterActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final AdminPagePresenter AdminPagePresenterActivity_realPresenter = (AdminPagePresenter) contextManager.getInstance("Type_factory__o_u_e_p_c_a_AdminPagePresenter__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AdminPagePresenterActivity_realPresenter);
    AdminPagePresenterActivity_AdminPagePresenter_realPresenter(instance, AdminPagePresenterActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static AdminPagePresenter AdminPagePresenterActivity_AdminPagePresenter_realPresenter(AdminPagePresenterActivity instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPagePresenterActivity::realPresenter;
  }-*/;

  native static void AdminPagePresenterActivity_AdminPagePresenter_realPresenter(AdminPagePresenterActivity instance, AdminPagePresenter value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPagePresenterActivity::realPresenter = value;
  }-*/;
}