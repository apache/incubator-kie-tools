package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.panels.DockingWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.client.workbench.panels.impl.AbstractDockingWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.AbstractMultiPartWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelView;

public class Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<MultiListWorkbenchPanelPresenter> { public Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultiListWorkbenchPanelPresenter.class, "Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultiListWorkbenchPanelPresenter.class, AbstractMultiPartWorkbenchPanelPresenter.class, AbstractDockingWorkbenchPanelPresenter.class, AbstractWorkbenchPanelPresenter.class, Object.class, WorkbenchPanelPresenter.class, DockingWorkbenchPanelPresenter.class });
  }

  public MultiListWorkbenchPanelPresenter createInstance(final ContextManager contextManager) {
    final PerspectiveManager _perspectiveManager_2 = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final WorkbenchPanelView<MultiListWorkbenchPanelPresenter> _view_0 = (MultiListWorkbenchPanelView) contextManager.getInstance("Type_factory__o_u_c_w_p_i_MultiListWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ActivityManager _activityManager_1 = (ActivityManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final MultiListWorkbenchPanelPresenter instance = new MultiListWorkbenchPanelPresenter(_view_0, _activityManager_1, _perspectiveManager_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    final PanelManagerImpl AbstractDockingWorkbenchPanelPresenter_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    AbstractDockingWorkbenchPanelPresenter_PanelManager_panelManager(instance, AbstractDockingWorkbenchPanelPresenter_panelManager);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MultiListWorkbenchPanelPresenter instance) {
    AbstractWorkbenchPanelPresenter_init(instance);
  }

  native static PanelManager AbstractDockingWorkbenchPanelPresenter_PanelManager_panelManager(AbstractDockingWorkbenchPanelPresenter instance) /*-{
    return instance.@org.uberfire.client.workbench.panels.impl.AbstractDockingWorkbenchPanelPresenter::panelManager;
  }-*/;

  native static void AbstractDockingWorkbenchPanelPresenter_PanelManager_panelManager(AbstractDockingWorkbenchPanelPresenter instance, PanelManager value) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractDockingWorkbenchPanelPresenter::panelManager = value;
  }-*/;

  public native static void AbstractWorkbenchPanelPresenter_init(AbstractWorkbenchPanelPresenter instance) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter::init()();
  }-*/;
}