package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.menu.WorkbenchViewModeSwitcherPresenter.View;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.views.pfly.menu.WorkbenchViewModeSwitcherView;

public class Type_factory__o_u_c_v_p_m_WorkbenchViewModeSwitcherView__quals__j_e_i_Any_j_e_i_Default extends Factory<WorkbenchViewModeSwitcherView> { public Type_factory__o_u_c_v_p_m_WorkbenchViewModeSwitcherView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WorkbenchViewModeSwitcherView.class, "Type_factory__o_u_c_v_p_m_WorkbenchViewModeSwitcherView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WorkbenchViewModeSwitcherView.class, Object.class, View.class, UberView.class, IsWidget.class, HasPresenter.class });
  }

  public WorkbenchViewModeSwitcherView createInstance(final ContextManager contextManager) {
    final WorkbenchViewModeSwitcherView instance = new WorkbenchViewModeSwitcherView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}