package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentView;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponentViewImpl;
import org.uberfire.experimental.client.service.impl.CDIClientFeatureDefRegistry;
import org.uberfire.experimental.service.definition.ExperimentalFeatureDefRegistry;

public class Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DisabledFeatureComponent> { public Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DisabledFeatureComponent.class, "Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DisabledFeatureComponent.class, Object.class, IsElement.class });
  }

  public DisabledFeatureComponent createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_2 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ExperimentalFeatureDefRegistry _defRegistry_1 = (CDIClientFeatureDefRegistry) contextManager.getInstance("Type_factory__o_u_e_c_s_i_CDIClientFeatureDefRegistry__quals__j_e_i_Any_j_e_i_Default");
    final DisabledFeatureComponentView _view_0 = (DisabledFeatureComponentViewImpl) contextManager.getInstance("Type_factory__o_u_e_c_d_c_DisabledFeatureComponentViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DisabledFeatureComponent instance = new DisabledFeatureComponent(_view_0, _defRegistry_1, _translationService_2);
    registerDependentScopedReference(instance, _translationService_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}