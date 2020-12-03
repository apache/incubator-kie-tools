package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;

public class Type_factory__o_k_w_c_f_a_e_s_f_l_LayoutGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutGenerator> { public Type_factory__o_k_w_c_f_a_e_s_f_l_LayoutGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutGenerator.class, "Type_factory__o_k_w_c_f_a_e_s_f_l_LayoutGenerator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutGenerator.class, Object.class });
  }

  public LayoutGenerator createInstance(final ContextManager contextManager) {
    final LayoutGenerator instance = new LayoutGenerator();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}