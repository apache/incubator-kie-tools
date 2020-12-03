package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.workbench.part.AbstractWorkbenchPartPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.pmgr.unanchored.part.UnanchoredWorkbenchPartPresenter;
import org.uberfire.client.workbench.pmgr.unanchored.part.UnanchoredWorkbenchPartView;

public class Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<UnanchoredWorkbenchPartPresenter> { public Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UnanchoredWorkbenchPartPresenter.class, "Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UnanchoredWorkbenchPartPresenter.class, AbstractWorkbenchPartPresenter.class, Object.class, WorkbenchPartPresenter.class });
  }

  public UnanchoredWorkbenchPartPresenter createInstance(final ContextManager contextManager) {
    final UnanchoredWorkbenchPartView _view_0 = (UnanchoredWorkbenchPartView) contextManager.getInstance("Type_factory__o_u_c_w_p_u_p_UnanchoredWorkbenchPartView__quals__j_e_i_Any_j_e_i_Default");
    final UnanchoredWorkbenchPartPresenter instance = new UnanchoredWorkbenchPartPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UnanchoredWorkbenchPartPresenter instance) {
    AbstractWorkbenchPartPresenter_init(instance);
  }

  public native static void AbstractWorkbenchPartPresenter_init(AbstractWorkbenchPartPresenter instance) /*-{
    instance.@org.uberfire.client.workbench.part.AbstractWorkbenchPartPresenter::init()();
  }-*/;
}