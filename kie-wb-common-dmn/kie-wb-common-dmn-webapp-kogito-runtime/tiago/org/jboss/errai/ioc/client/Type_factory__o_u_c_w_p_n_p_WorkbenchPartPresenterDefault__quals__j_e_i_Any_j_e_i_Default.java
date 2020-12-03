package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.part.AbstractWorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.pmgr.nswe.part.WorkbenchPartPresenterDefault;
import org.uberfire.client.workbench.pmgr.nswe.part.WorkbenchPartView;

public class Type_factory__o_u_c_w_p_n_p_WorkbenchPartPresenterDefault__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchPartPresenterDefault> { public Type_factory__o_u_c_w_p_n_p_WorkbenchPartPresenterDefault__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchPartPresenterDefault.class, "Type_factory__o_u_c_w_p_n_p_WorkbenchPartPresenterDefault__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchPartPresenterDefault.class, AbstractWorkbenchPartPresenter.class, Object.class, WorkbenchPartPresenter.class });
  }

  public WorkbenchPartPresenterDefault createInstance(final ContextManager contextManager) {
    final WorkbenchPartView _view_0 = (WorkbenchPartView) contextManager.getInstance("Type_factory__o_u_c_w_p_n_p_WorkbenchPartView__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchPartPresenterDefault instance = new WorkbenchPartPresenterDefault(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final WorkbenchPartPresenterDefault instance) {
    AbstractWorkbenchPartPresenter_init(instance);
  }

  public native static void AbstractWorkbenchPartPresenter_init(AbstractWorkbenchPartPresenter instance) /*-{
    instance.@org.uberfire.client.workbench.part.AbstractWorkbenchPartPresenter::init()();
  }-*/;
}