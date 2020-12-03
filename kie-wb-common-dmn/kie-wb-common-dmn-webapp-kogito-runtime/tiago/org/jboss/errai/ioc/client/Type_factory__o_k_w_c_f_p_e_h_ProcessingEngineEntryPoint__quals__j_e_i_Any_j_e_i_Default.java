package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.processing.engine.handling.ProcessingEngineEntryPoint;

public class Type_factory__o_k_w_c_f_p_e_h_ProcessingEngineEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<ProcessingEngineEntryPoint> { public Type_factory__o_k_w_c_f_p_e_h_ProcessingEngineEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ProcessingEngineEntryPoint.class, "Type_factory__o_k_w_c_f_p_e_h_ProcessingEngineEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { ProcessingEngineEntryPoint.class, Object.class });
  }

  public ProcessingEngineEntryPoint createInstance(final ContextManager contextManager) {
    final ProcessingEngineEntryPoint instance = new ProcessingEngineEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}