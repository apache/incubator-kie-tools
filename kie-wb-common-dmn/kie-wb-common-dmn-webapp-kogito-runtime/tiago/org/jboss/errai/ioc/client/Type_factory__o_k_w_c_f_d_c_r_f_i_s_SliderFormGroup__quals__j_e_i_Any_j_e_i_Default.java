package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.FormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.ValidableFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.AbstractFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.AbstractValidableFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroup;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.slider.SliderFormGroupViewImpl;
import org.kie.workbench.common.forms.processing.engine.handling.FieldContainer;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroup__quals__j_e_i_Any_j_e_i_Default extends Factory<SliderFormGroup> { public Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SliderFormGroup.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SliderFormGroup.class, AbstractValidableFormGroup.class, AbstractFormGroup.class, Object.class, FormGroup.class, FieldContainer.class, IsElement.class, ValidableFormGroup.class });
  }

  public SliderFormGroup createInstance(final ContextManager contextManager) {
    final SliderFormGroupView _view_0 = (SliderFormGroupViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_i_s_SliderFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final SliderFormGroup instance = new SliderFormGroup(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}