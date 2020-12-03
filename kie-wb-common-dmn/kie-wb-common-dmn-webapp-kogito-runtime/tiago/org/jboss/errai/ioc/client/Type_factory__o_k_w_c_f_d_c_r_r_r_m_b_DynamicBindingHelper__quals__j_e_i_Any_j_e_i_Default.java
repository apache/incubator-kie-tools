package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.AbstractBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.BindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.binding.DynamicBindingHelper;

public class Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_DynamicBindingHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicBindingHelper> { public Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_DynamicBindingHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DynamicBindingHelper.class, "Type_factory__o_k_w_c_f_d_c_r_r_r_m_b_DynamicBindingHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicBindingHelper.class, AbstractBindingHelper.class, Object.class, BindingHelper.class });
  }

  public DynamicBindingHelper createInstance(final ContextManager contextManager) {
    final MapModelBindingHelper _helper_0 = (MapModelBindingHelper) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default");
    final DynamicBindingHelper instance = new DynamicBindingHelper(_helper_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}