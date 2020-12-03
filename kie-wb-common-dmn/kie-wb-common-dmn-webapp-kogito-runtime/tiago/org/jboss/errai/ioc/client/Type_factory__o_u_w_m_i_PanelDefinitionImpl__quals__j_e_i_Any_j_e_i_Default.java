package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;

public class Type_factory__o_u_w_m_i_PanelDefinitionImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PanelDefinitionImpl> { public Type_factory__o_u_w_m_i_PanelDefinitionImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PanelDefinitionImpl.class, "Type_factory__o_u_w_m_i_PanelDefinitionImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PanelDefinitionImpl.class, Object.class, PanelDefinition.class });
  }

  public PanelDefinitionImpl createInstance(final ContextManager contextManager) {
    final PanelDefinitionImpl instance = new PanelDefinitionImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}