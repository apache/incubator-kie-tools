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
import org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.ImmutableWorkbenchPanelView;

public class Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ImmutableWorkbenchPanelPresenter> { public Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImmutableWorkbenchPanelPresenter.class, "Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImmutableWorkbenchPanelPresenter.class, AbstractWorkbenchPanelPresenter.class, Object.class, WorkbenchPanelPresenter.class });
  }

  public ImmutableWorkbenchPanelPresenter createInstance(final ContextManager contextManager) {
    final ImmutableWorkbenchPanelView _view_0 = (ImmutableWorkbenchPanelView) contextManager.getInstance("Type_factory__o_u_c_w_p_i_ImmutableWorkbenchPanelView__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final PlaceManager _placeManager_2 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final PerspectiveManager _perspectiveManager_1 = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ImmutableWorkbenchPanelPresenter instance = new ImmutableWorkbenchPanelPresenter(_view_0, _perspectiveManager_1, _placeManager_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ImmutableWorkbenchPanelPresenter instance) {
    AbstractWorkbenchPanelPresenter_init(instance);
  }

  public native static void AbstractWorkbenchPanelPresenter_init(AbstractWorkbenchPanelPresenter instance) /*-{
    instance.@org.uberfire.client.workbench.panels.impl.AbstractWorkbenchPanelPresenter::init()();
  }-*/;
}