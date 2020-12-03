package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspective;
import org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspectiveActivity;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<AuthoringPerspectiveActivity> { public Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(AuthoringPerspectiveActivity.class, "Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspectiveActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "AuthoringPerspective", true));
    handle.setAssignableTypes(new Class[] { AuthoringPerspectiveActivity.class, AbstractWorkbenchPerspectiveActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PerspectiveActivity.class, ContextSensitiveActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("AuthoringPerspective") });
  }

  public AuthoringPerspectiveActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final AuthoringPerspectiveActivity instance = new AuthoringPerspectiveActivity(_placeManager_0);
    setIncompleteInstance(instance);
    final AuthoringPerspective AuthoringPerspectiveActivity_realPresenter = (AuthoringPerspective) contextManager.getInstance("Type_factory__o_k_w_c_k_w_b_c_p_AuthoringPerspective__quals__j_e_i_Any_j_e_i_Default");
    AuthoringPerspectiveActivity_AuthoringPerspective_realPresenter(instance, AuthoringPerspectiveActivity_realPresenter);
    setIncompleteInstance(null);
    return instance;
  }

  native static AuthoringPerspective AuthoringPerspectiveActivity_AuthoringPerspective_realPresenter(AuthoringPerspectiveActivity instance) /*-{
    return instance.@org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspectiveActivity::realPresenter;
  }-*/;

  native static void AuthoringPerspectiveActivity_AuthoringPerspective_realPresenter(AuthoringPerspectiveActivity instance, AuthoringPerspective value) /*-{
    instance.@org.kie.workbench.common.kogito.webapp.base.client.perspectives.AuthoringPerspectiveActivity::realPresenter = value;
  }-*/;
}