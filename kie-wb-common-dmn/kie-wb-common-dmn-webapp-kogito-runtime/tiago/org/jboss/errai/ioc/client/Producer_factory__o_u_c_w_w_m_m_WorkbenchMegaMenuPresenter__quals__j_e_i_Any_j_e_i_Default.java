package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.client.workbench.widgets.menu.base.WorkbenchBaseMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuProducer;

public class Producer_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchMegaMenuPresenter> { public Producer_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchMegaMenuPresenter.class, "Producer_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchMegaMenuPresenter.class, WorkbenchBaseMenuPresenter.class, Object.class, HasMenus.class });
  }

  public WorkbenchMegaMenuPresenter createInstance(final ContextManager contextManager) {
    WorkbenchMegaMenuProducer producerInstance = contextManager.getInstance("Type_factory__o_u_c_w_w_m_m_WorkbenchMegaMenuProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final WorkbenchMegaMenuPresenter instance = producerInstance.getInstance();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }
}