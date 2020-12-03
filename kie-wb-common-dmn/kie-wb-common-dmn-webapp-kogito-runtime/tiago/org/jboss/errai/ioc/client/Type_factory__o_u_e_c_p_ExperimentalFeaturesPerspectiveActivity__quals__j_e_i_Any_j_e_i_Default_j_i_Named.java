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
import org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspective;
import org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspectiveActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<ExperimentalFeaturesPerspectiveActivity> { public Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(ExperimentalFeaturesPerspectiveActivity.class, "Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "ExperimentalFeaturesPerspective", true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesPerspectiveActivity.class, AbstractWorkbenchPerspectiveActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PerspectiveActivity.class, ContextSensitiveActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("ExperimentalFeaturesPerspective") });
  }

  public ExperimentalFeaturesPerspectiveActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeaturesPerspectiveActivity instance = new ExperimentalFeaturesPerspectiveActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final ExperimentalFeaturesPerspective ExperimentalFeaturesPerspectiveActivity_realPresenter = (ExperimentalFeaturesPerspective) contextManager.getInstance("Type_factory__o_u_e_c_p_ExperimentalFeaturesPerspective__quals__j_e_i_Any_j_e_i_Default");
    ExperimentalFeaturesPerspectiveActivity_ExperimentalFeaturesPerspective_realPresenter(instance, ExperimentalFeaturesPerspectiveActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static ExperimentalFeaturesPerspective ExperimentalFeaturesPerspectiveActivity_ExperimentalFeaturesPerspective_realPresenter(ExperimentalFeaturesPerspectiveActivity instance) /*-{
    return instance.@org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspectiveActivity::realPresenter;
  }-*/;

  native static void ExperimentalFeaturesPerspectiveActivity_ExperimentalFeaturesPerspective_realPresenter(ExperimentalFeaturesPerspectiveActivity instance, ExperimentalFeaturesPerspective value) /*-{
    instance.@org.uberfire.experimental.client.perspective.ExperimentalFeaturesPerspectiveActivity::realPresenter = value;
  }-*/;
}