package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherMenuBuilder;
import org.uberfire.workbench.model.menu.MenuFactory.CustomMenuBuilder;

public class Type_factory__o_u_c_m_WorkbenchViewModeSwitcherMenuBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchViewModeSwitcherMenuBuilder> { public Type_factory__o_u_c_m_WorkbenchViewModeSwitcherMenuBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchViewModeSwitcherMenuBuilder.class, "Type_factory__o_u_c_m_WorkbenchViewModeSwitcherMenuBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchViewModeSwitcherMenuBuilder.class, Object.class, CustomMenuBuilder.class });
  }

  public WorkbenchViewModeSwitcherMenuBuilder createInstance(final ContextManager contextManager) {
    final WorkbenchViewModeSwitcherMenuBuilder instance = new WorkbenchViewModeSwitcherMenuBuilder();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}