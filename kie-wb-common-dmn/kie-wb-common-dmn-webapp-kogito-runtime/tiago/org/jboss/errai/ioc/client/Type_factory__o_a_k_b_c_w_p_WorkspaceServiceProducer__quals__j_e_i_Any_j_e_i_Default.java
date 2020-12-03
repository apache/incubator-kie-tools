package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.workspace.producer.WorkspaceServiceProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_w_p_WorkspaceServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkspaceServiceProducer> { public Type_factory__o_a_k_b_c_w_p_WorkspaceServiceProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkspaceServiceProducer.class, "Type_factory__o_a_k_b_c_w_p_WorkspaceServiceProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkspaceServiceProducer.class, Object.class });
  }

  public WorkspaceServiceProducer createInstance(final ContextManager contextManager) {
    final WorkspaceServiceProducer instance = new WorkspaceServiceProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}