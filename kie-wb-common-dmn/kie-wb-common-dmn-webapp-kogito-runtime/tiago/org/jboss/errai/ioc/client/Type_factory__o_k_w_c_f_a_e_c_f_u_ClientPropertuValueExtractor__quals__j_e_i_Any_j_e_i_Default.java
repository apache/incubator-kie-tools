package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.util.ClientPropertuValueExtractor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.impl.AbstractPropertyValueExtractor;

public class Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientPropertuValueExtractor> { public Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientPropertuValueExtractor.class, "Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientPropertuValueExtractor.class, AbstractPropertyValueExtractor.class, Object.class, PropertyValueExtractor.class });
  }

  public ClientPropertuValueExtractor createInstance(final ContextManager contextManager) {
    final ClientPropertuValueExtractor instance = new ClientPropertuValueExtractor();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}