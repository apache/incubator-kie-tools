package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.EditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.AbstractEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateEditableColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.impl.DateTimePickerCell;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<DateEditableColumnGenerator> { public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DateEditableColumnGenerator.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_i_DateEditableColumnGenerator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DateEditableColumnGenerator.class, AbstractEditableColumnGenerator.class, Object.class, EditableColumnGenerator.class });
  }

  public DateEditableColumnGenerator createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DateTimePickerCell> _dateTimePickerCells_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DateTimePickerCell.class }, new Annotation[] { });
    final DateEditableColumnGenerator instance = new DateEditableColumnGenerator(_translationService_0, _dateTimePickerCells_1);
    registerDependentScopedReference(instance, _translationService_0);
    registerDependentScopedReference(instance, _dateTimePickerCells_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DateEditableColumnGenerator) instance, contextManager);
  }

  public void destroyInstanceHelper(final DateEditableColumnGenerator instance, final ContextManager contextManager) {
    instance.destroy();
  }
}