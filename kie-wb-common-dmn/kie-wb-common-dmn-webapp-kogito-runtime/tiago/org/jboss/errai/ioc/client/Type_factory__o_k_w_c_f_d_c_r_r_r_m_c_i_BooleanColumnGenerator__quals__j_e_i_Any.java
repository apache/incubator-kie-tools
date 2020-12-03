package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.ColumnGenerator;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.columns.impl.BooleanColumnGenerator;

public class Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_BooleanColumnGenerator__quals__j_e_i_Any extends Factory<BooleanColumnGenerator> { public Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_BooleanColumnGenerator__quals__j_e_i_Any() {
    super(new FactoryHandleImpl(BooleanColumnGenerator.class, "Type_factory__o_k_w_c_f_d_c_r_r_r_m_c_i_BooleanColumnGenerator__quals__j_e_i_Any", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BooleanColumnGenerator.class, Object.class, ColumnGenerator.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION });
  }

  public BooleanColumnGenerator createInstance(final ContextManager contextManager) {
    final BooleanColumnGenerator instance = new BooleanColumnGenerator();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}