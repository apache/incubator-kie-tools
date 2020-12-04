package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.AbstractActivity;
import org.uberfire.client.mvp.AbstractWorkbenchActivity;
import org.uberfire.client.mvp.AbstractWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ContextSensitiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.WorkbenchActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;
import org.uberfire.experimental.client.disabled.screen.DisabledFeatureActivity;
import org.uberfire.security.Resource;
import org.uberfire.security.authz.RuntimeFeatureResource;
import org.uberfire.security.authz.RuntimeResource;

public class Type_factory__o_u_e_c_d_s_DisabledFeatureActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named extends Factory<DisabledFeatureActivity> { public Type_factory__o_u_e_c_d_s_DisabledFeatureActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named() {
    super(new FactoryHandleImpl(DisabledFeatureActivity.class, "Type_factory__o_u_e_c_d_s_DisabledFeatureActivity__quals__j_e_i_Any_j_e_i_Default_j_i_Named", Dependent.class, false, "appformer.experimental.disabledFeatureTitle", true));
    handle.setAssignableTypes(new Class[] { DisabledFeatureActivity.class, AbstractWorkbenchScreenActivity.class, AbstractWorkbenchActivity.class, AbstractActivity.class, Object.class, Activity.class, RuntimeFeatureResource.class, RuntimeResource.class, Resource.class, WorkbenchActivity.class, ContextSensitiveActivity.class, WorkbenchScreenActivity.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, QualifierUtil.DEFAULT_ANNOTATION, QualifierUtil.createNamed("appformer.experimental.disabledFeatureTitle") });
  }

  public DisabledFeatureActivity createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DisabledFeatureComponent _component_1 = (DisabledFeatureComponent) contextManager.getInstance("Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_2 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DisabledFeatureActivity instance = new DisabledFeatureActivity(_placeManager_0, _component_1, _translationService_2);
    registerDependentScopedReference(instance, _component_1);
    registerDependentScopedReference(instance, _translationService_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}