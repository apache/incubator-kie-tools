package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldReadOnlyEntryProcessor;

public class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldReadOnlyEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldReadOnlyEntryProcessor> { public Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldReadOnlyEntryProcessor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldReadOnlyEntryProcessor.class, "Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldReadOnlyEntryProcessor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldReadOnlyEntryProcessor.class, Object.class, MetaDataEntryProcessor.class });
  }

  public FieldReadOnlyEntryProcessor createInstance(final ContextManager contextManager) {
    final FieldReadOnlyEntryProcessor instance = new FieldReadOnlyEntryProcessor();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}