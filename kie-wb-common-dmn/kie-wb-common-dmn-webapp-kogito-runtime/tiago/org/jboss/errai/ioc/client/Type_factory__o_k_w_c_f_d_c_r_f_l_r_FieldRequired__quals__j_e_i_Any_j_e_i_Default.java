package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequiredView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequiredView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequiredViewImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRequired> { public Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldRequired.class, "Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldRequired.class, Object.class, Presenter.class, IsElement.class });
  }

  public FieldRequired createInstance(final ContextManager contextManager) {
    final FieldRequiredView _view_0 = (FieldRequiredViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final FieldRequired instance = new FieldRequired(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}