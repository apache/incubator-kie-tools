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
import org.uberfire.client.workbench.StandaloneEditorPerspective;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_c_w_StandaloneEditorPerspective__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<StandaloneEditorPerspective> { public Type_factory__o_u_c_w_StandaloneEditorPerspective__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(StandaloneEditorPerspective.class, "Type_factory__o_u_c_w_StandaloneEditorPerspective__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "StandaloneEditorPerspective", true));
    handle.setAssignableTypes(new Class[] { StandaloneEditorPerspective.class, AbstractWorkbenchPerspectiveActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, PerspectiveActivity.class, ContextSensitiveActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("StandaloneEditorPerspective") });
  }

  public StandaloneEditorPerspective createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final StandaloneEditorPerspective instance = new StandaloneEditorPerspective(_placeManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}