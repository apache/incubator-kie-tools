package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.AbstractEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.StringEditableColumnGenerator;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_StringEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<StringEditableColumnGenerator> { public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_StringEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StringEditableColumnGenerator.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_StringEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StringEditableColumnGenerator.class, AbstractEditableColumnGenerator.class, Object.class, EditableColumnGenerator.class });
  }

  public StringEditableColumnGenerator createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final StringEditableColumnGenerator instance = new StringEditableColumnGenerator(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}