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
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DefaultPreferenceFormActivity> { public Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DefaultPreferenceFormActivity.class, "Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm", true));
    handle.setAssignableTypes(new Class[] { DefaultPreferenceFormActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm") });
  }

  public DefaultPreferenceFormActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DefaultPreferenceFormActivity instance = new DefaultPreferenceFormActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final DefaultPreferenceForm DefaultPreferenceFormActivity_realPresenter = (DefaultPreferenceForm) contextManager.getInstance("Type_factory__o_u_e_p_c_c_f_DefaultPreferenceForm__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefaultPreferenceFormActivity_realPresenter);
    DefaultPreferenceFormActivity_DefaultPreferenceForm_realPresenter(instance, DefaultPreferenceFormActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static DefaultPreferenceForm DefaultPreferenceFormActivity_DefaultPreferenceForm_realPresenter(DefaultPreferenceFormActivity instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormActivity::realPresenter;
  }-*/;

  native static void DefaultPreferenceFormActivity_DefaultPreferenceForm_realPresenter(DefaultPreferenceFormActivity instance, DefaultPreferenceForm value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormActivity::realPresenter = value;
  }-*/;
}