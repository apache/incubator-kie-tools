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
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspectiveActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_p_c_c_PreferencesCentralPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreferencesCentralPerspectiveActivity> { public Type_factory__o_u_e_p_c_c_PreferencesCentralPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(PreferencesCentralPerspectiveActivity.class, "Type_factory__o_u_e_p_c_c_PreferencesCentralPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "PreferencesCentralPerspective", true));
    handle.setAssignableTypes(new Class[] { PreferencesCentralPerspectiveActivity.class, AbstractWorkbenchPerspectiveActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PerspectiveActivity.class, ContextSensitiveActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("PreferencesCentralPerspective") });
  }

  public PreferencesCentralPerspectiveActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PreferencesCentralPerspectiveActivity instance = new PreferencesCentralPerspectiveActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final PreferencesCentralPerspective PreferencesCentralPerspectiveActivity_realPresenter = (PreferencesCentralPerspective) contextManager.getInstance("Type_factory__o_u_e_p_c_c_PreferencesCentralPerspective__quals__j_e_i_Any_j_e_i_Default");
    PreferencesCentralPerspectiveActivity_PreferencesCentralPerspective_realPresenter(instance, PreferencesCentralPerspectiveActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static PreferencesCentralPerspective PreferencesCentralPerspectiveActivity_PreferencesCentralPerspective_realPresenter(PreferencesCentralPerspectiveActivity instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.PreferencesCentralPerspectiveActivity::realPresenter;
  }-*/;

  native static void PreferencesCentralPerspectiveActivity_PreferencesCentralPerspective_realPresenter(PreferencesCentralPerspectiveActivity instance, PreferencesCentralPerspective value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.PreferencesCentralPerspectiveActivity::realPresenter = value;
  }-*/;
}