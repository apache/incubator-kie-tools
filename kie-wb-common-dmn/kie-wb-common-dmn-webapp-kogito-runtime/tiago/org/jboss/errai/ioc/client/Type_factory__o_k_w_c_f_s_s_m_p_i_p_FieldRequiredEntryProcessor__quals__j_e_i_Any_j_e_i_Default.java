package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryProcessor;
import org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors.FieldRequiredEntryProcessor;

public class Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldRequiredEntryProcessor__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRequiredEntryProcessor> { public Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldRequiredEntryProcessor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldRequiredEntryProcessor.class, "Type_factory__o_k_w_c_f_s_s_m_p_i_p_FieldRequiredEntryProcessor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldRequiredEntryProcessor.class, Object.class, MetaDataEntryProcessor.class });
  }

  public FieldRequiredEntryProcessor createInstance(final ContextManager contextManager) {
    final FieldRequiredEntryProcessor instance = new FieldRequiredEntryProcessor();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}