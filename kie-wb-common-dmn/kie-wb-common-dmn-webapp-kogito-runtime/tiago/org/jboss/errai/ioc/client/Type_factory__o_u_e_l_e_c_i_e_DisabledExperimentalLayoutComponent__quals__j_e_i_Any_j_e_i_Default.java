package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.experimental.client.disabled.component.DisabledFeatureComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.experimental.DisabledExperimentalLayoutComponent;

public class Type_factory__o_u_e_l_e_c_i_e_DisabledExperimentalLayoutComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<DisabledExperimentalLayoutComponent> { public Type_factory__o_u_e_l_e_c_i_e_DisabledExperimentalLayoutComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DisabledExperimentalLayoutComponent.class, "Type_factory__o_u_e_l_e_c_i_e_DisabledExperimentalLayoutComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DisabledExperimentalLayoutComponent.class, Object.class, LayoutDragComponent.class });
  }

  public DisabledExperimentalLayoutComponent createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DisabledFeatureComponent _component_0 = (DisabledFeatureComponent) contextManager.getInstance("Type_factory__o_u_e_c_d_c_DisabledFeatureComponent__quals__j_e_i_Any_j_e_i_Default");
    final DisabledExperimentalLayoutComponent instance = new DisabledExperimentalLayoutComponent(_component_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _component_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}