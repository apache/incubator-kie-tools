package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabel;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.label.FieldLabelViewImpl;

public class Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldLabel> { public Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldLabel.class, "Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldLabel.class, Object.class, IsElement.class, Presenter.class });
  }

  public FieldLabel createInstance(final ContextManager contextManager) {
    final FieldLabelView _view_0 = (FieldLabelViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_l_FieldLabelViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final FieldLabel instance = new FieldLabel(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}