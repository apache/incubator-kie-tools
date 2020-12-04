package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.UnanchoredStaticWorkbenchPanelView;

public class Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<UnanchoredStaticWorkbenchPanelPresenter> { public Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UnanchoredStaticWorkbenchPanelPresenter.class, "Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UnanchoredStaticWorkbenchPanelPresenter.class, AbstractWorkbenchPanelPresenter.class, Object.class, WorkbenchPanelPresenter.class });
  }

  public UnanchoredStaticWorkbenchPanelPresenter createInstance(final ContextManager contextManager) {
    final PerspectiveManager _perspectiveManager_1 = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_2 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final UnanchoredStaticWorkbenchPanelView _view_0 = (UnanchoredStaticWorkbenchPanelView) contextManager.getInstance("Type_factory__o_u_c_w_p_i_UnanchoredStaticWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final UnanchoredStaticWorkbenchPanelPresenter instance = new UnanchoredStaticWorkbenchPanelPresenter(_view_0, _perspectiveManager_1, _placeManager_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UnanchoredStaticWorkbenchPanelPresenter instance) {
    AbstractWorkbenchPanelPresenter_init(instance);
  }

  public native static void AbstractWorkbenchPanelPresenter_init(AbstractWorkbenchPanelPresenter instance) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter::init()();
  }-*/;
}