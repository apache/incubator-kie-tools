package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.pmmleditor.marshaller.PMMLEditorMarshallerServiceProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<PMMLEditorMarshallerServiceProducer> { public Type_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerServiceProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PMMLEditorMarshallerServiceProducer.class, "Type_factory__o_a_k_b_c_p_m_PMMLEditorMarshallerServiceProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PMMLEditorMarshallerServiceProducer.class, Object.class });
  }

  public PMMLEditorMarshallerServiceProducer createInstance(final ContextManager contextManager) {
    final PMMLEditorMarshallerServiceProducer instance = new PMMLEditorMarshallerServiceProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}