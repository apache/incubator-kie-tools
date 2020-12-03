package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspective;
import org.uberfire.ext.preferences.client.admin.AdminPagePerspectiveActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_p_c_a_AdminPagePerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AdminPagePerspectiveActivity> { public Type_factory__o_u_e_p_c_a_AdminPagePerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(AdminPagePerspectiveActivity.class, "Type_factory__o_u_e_p_c_a_AdminPagePerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "AdminPagePerspective", true));
    handle.setAssignableTypes(new Class[] { AdminPagePerspectiveActivity.class, AbstractWorkbenchPerspectiveActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PerspectiveActivity.class, ContextSensitiveActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("AdminPagePerspective") });
  }

  public AdminPagePerspectiveActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final AdminPagePerspectiveActivity instance = new AdminPagePerspectiveActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final AdminPagePerspective AdminPagePerspectiveActivity_realPresenter = (AdminPagePerspective) contextManager.getInstance("Type_factory__o_u_e_p_c_a_AdminPagePerspective__quals__j_e_i_Any_j_e_i_Default");
    AdminPagePerspectiveActivity_AdminPagePerspective_realPresenter(instance, AdminPagePerspectiveActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static AdminPagePerspective AdminPagePerspectiveActivity_AdminPagePerspective_realPresenter(AdminPagePerspectiveActivity instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPagePerspectiveActivity::realPresenter;
  }-*/;

  native static void AdminPagePerspectiveActivity_AdminPagePerspective_realPresenter(AdminPagePerspectiveActivity instance, AdminPagePerspective value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPagePerspectiveActivity::realPresenter = value;
  }-*/;
}