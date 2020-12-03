package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.AbstractFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.AbstractNestedFormFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl;
import org.kie.workbench.common.forms.processing.engine.handling.FieldContainer;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsibleFormGroup> { public Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CollapsibleFormGroup.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CollapsibleFormGroup.class, AbstractNestedFormFormGroup.class, AbstractFormGroup.class, Object.class, FormGroup.class, FieldContainer.class, IsElement.class, Presenter.class });
  }

  public CollapsibleFormGroup createInstance(final ContextManager contextManager) {
    final CollapsibleFormGroupView _view_0 = (CollapsibleFormGroupViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final CollapsibleFormGroup instance = new CollapsibleFormGroup(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}