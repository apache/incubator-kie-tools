package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererManagerImpl;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;

public class Type_factory__o_k_w_c_f_d_c_r_FieldLayoutComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLayoutComponent> { public Type_factory__o_k_w_c_f_d_c_r_FieldLayoutComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldLayoutComponent.class, "Type_factory__o_k_w_c_f_d_c_r_FieldLayoutComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldLayoutComponent.class, Object.class, FormLayoutComponent.class, LayoutDragComponent.class });
  }

  public FieldLayoutComponent createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FieldRendererManager _fieldRendererManager_0 = (FieldRendererManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_FieldRendererManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final FieldLayoutComponent instance = new FieldLayoutComponent(_fieldRendererManager_0, _translationService_1);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _fieldRendererManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FieldLayoutComponent) instance, contextManager);
  }

  public void destroyInstanceHelper(final FieldLayoutComponent instance, final ContextManager contextManager) {
    instance.destroy();
  }
}