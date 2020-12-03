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
import org.uberfire.jsbridge.client.loading.LazyLoadingScreen;
import org.uberfire.jsbridge.client.loading.LazyLoadingScreenActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_j_c_l_LazyLoadingScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<LazyLoadingScreenActivity> { public Type_factory__o_u_j_c_l_LazyLoadingScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(LazyLoadingScreenActivity.class, "Type_factory__o_u_j_c_l_LazyLoadingScreenActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "LazyLoadingScreen", true));
    handle.setAssignableTypes(new Class[] { LazyLoadingScreenActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("LazyLoadingScreen") });
  }

  public LazyLoadingScreenActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final LazyLoadingScreenActivity instance = new LazyLoadingScreenActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final LazyLoadingScreen LazyLoadingScreenActivity_realPresenter = (LazyLoadingScreen) contextManager.getInstance("Type_factory__o_u_j_c_l_LazyLoadingScreen__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, LazyLoadingScreenActivity_realPresenter);
    LazyLoadingScreenActivity_LazyLoadingScreen_realPresenter(instance, LazyLoadingScreenActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static LazyLoadingScreen LazyLoadingScreenActivity_LazyLoadingScreen_realPresenter(LazyLoadingScreenActivity instance) /*-{
    return instance.@org.uberfire.jsbridge.client.loading.LazyLoadingScreenActivity::realPresenter;
  }-*/;

  native static void LazyLoadingScreenActivity_LazyLoadingScreen_realPresenter(LazyLoadingScreenActivity instance, LazyLoadingScreen value) /*-{
    instance.@org.uberfire.jsbridge.client.loading.LazyLoadingScreenActivity::realPresenter = value;
  }-*/;
}