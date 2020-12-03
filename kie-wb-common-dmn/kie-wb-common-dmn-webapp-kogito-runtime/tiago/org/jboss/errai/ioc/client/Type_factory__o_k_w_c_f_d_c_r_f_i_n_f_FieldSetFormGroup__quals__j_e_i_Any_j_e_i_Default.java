package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.AbstractFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.AbstractNestedFormFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.fieldSet.FieldSetFormGroupViewImpl;
import org.kie.workbench.common.forms.processing.engine.handling.FieldContainer;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldSetFormGroup> { public Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldSetFormGroup.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldSetFormGroup.class, AbstractNestedFormFormGroup.class, AbstractFormGroup.class, Object.class, FormGroup.class, FieldContainer.class, IsElement.class });
  }

  public FieldSetFormGroup createInstance(final ContextManager contextManager) {
    final FieldSetFormGroupView _view_0 = (FieldSetFormGroupViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_i_n_f_FieldSetFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final FieldSetFormGroup instance = new FieldSetFormGroup(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}