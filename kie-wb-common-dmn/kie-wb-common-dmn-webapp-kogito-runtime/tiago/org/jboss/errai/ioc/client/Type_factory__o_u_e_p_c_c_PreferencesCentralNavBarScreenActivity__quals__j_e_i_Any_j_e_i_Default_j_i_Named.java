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
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreen;
import org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreenActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<PreferencesCentralNavBarScreenActivity> { public Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(PreferencesCentralNavBarScreenActivity.class, "Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "PreferencesCentralNavBarScreen", true));
    handle.setAssignableTypes(new Class[] { PreferencesCentralNavBarScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("PreferencesCentralNavBarScreen") });
  }

  public PreferencesCentralNavBarScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PreferencesCentralNavBarScreenActivity instance = new PreferencesCentralNavBarScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final PreferencesCentralNavBarScreen PreferencesCentralNavBarScreenActivity_realPresenter = (PreferencesCentralNavBarScreen) contextManager.getInstance("Type_factory__o_u_e_p_c_c_PreferencesCentralNavBarScreen__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PreferencesCentralNavBarScreenActivity_realPresenter);
    PreferencesCentralNavBarScreenActivity_PreferencesCentralNavBarScreen_realPresenter(instance, PreferencesCentralNavBarScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static PreferencesCentralNavBarScreen PreferencesCentralNavBarScreenActivity_PreferencesCentralNavBarScreen_realPresenter(PreferencesCentralNavBarScreenActivity instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreenActivity::realPresenter;
  }-*/;

  native static void PreferencesCentralNavBarScreenActivity_PreferencesCentralNavBarScreen_realPresenter(PreferencesCentralNavBarScreenActivity instance, PreferencesCentralNavBarScreen value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.PreferencesCentralNavBarScreenActivity::realPresenter = value;
  }-*/;
}