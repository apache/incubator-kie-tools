package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.LayoutPanelPresenter;
import org.uberfire.client.workbench.panels.impl.LayoutPanelView;

public class Type_factory__o_u_c_w_p_i_LayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutPanelPresenter> { public Type_factory__o_u_c_w_p_i_LayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutPanelPresenter.class, "Type_factory__o_u_c_w_p_i_LayoutPanelPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutPanelPresenter.class, AbstractWorkbenchPanelPresenter.class, Object.class, WorkbenchPanelPresenter.class });
  }

  public LayoutPanelPresenter createInstance(final ContextManager contextManager) {
    final PerspectiveManager _perspectiveManager_1 = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final LayoutPanelView _view_0 = (LayoutPanelView) contextManager.getInstance("Type_factory__o_u_c_w_p_i_LayoutPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final LayoutPanelPresenter instance = new LayoutPanelPresenter(_view_0, _perspectiveManager_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final LayoutPanelPresenter instance) {
    AbstractWorkbenchPanelPresenter_init(instance);
  }

  public native static void AbstractWorkbenchPanelPresenter_init(AbstractWorkbenchPanelPresenter instance) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter::init()();
  }-*/;
}