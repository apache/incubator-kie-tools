package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.impl.StringColumnGenerator;

public class Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_StringColumnGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<StringColumnGenerator> { public Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_StringColumnGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StringColumnGenerator.class, "Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_StringColumnGenerator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StringColumnGenerator.class, Object.class, ColumnGenerator.class });
  }

  public StringColumnGenerator createInstance(final ContextManager contextManager) {
    final StringColumnGenerator instance = new StringColumnGenerator();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}