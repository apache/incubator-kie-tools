package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarProducer;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuPresenter;

public class Producer_factory__o_u_c_w_w_m_WorkbenchMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMenuBarPresenter> { public Producer_factory__o_u_c_w_w_m_WorkbenchMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchMenuBarPresenter.class, "Producer_factory__o_u_c_w_w_m_WorkbenchMenuBarPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchMenuBarPresenter.class, WorkbenchBaseMenuPresenter.class, Object.class, HasMenus.class, WorkbenchMenuBar.class });
  }

  public WorkbenchMenuBarPresenter createInstance(final ContextManager contextManager) {
    WorkbenchMenuBarProducer producerInstance = contextManager.getInstance("Type_factory__o_u_c_w_w_m_WorkbenchMenuBarProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final WorkbenchMenuBarPresenter instance = producerInstance.getInstance();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}