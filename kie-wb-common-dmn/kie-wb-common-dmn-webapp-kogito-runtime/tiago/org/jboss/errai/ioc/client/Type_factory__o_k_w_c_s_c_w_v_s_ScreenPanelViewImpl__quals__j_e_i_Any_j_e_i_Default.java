package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelView;
import org.kie.workbench.common.stunner.client.widgets.views.session.ScreenPanelViewImpl;

public class Type_factory__o_k_w_c_s_c_w_v_s_ScreenPanelViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ScreenPanelViewImpl> { public Type_factory__o_k_w_c_s_c_w_v_s_ScreenPanelViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ScreenPanelViewImpl.class, "Type_factory__o_k_w_c_s_c_w_v_s_ScreenPanelViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ScreenPanelViewImpl.class, Object.class, ScreenPanelView.class, IsWidget.class });
  }

  public ScreenPanelViewImpl createInstance(final ContextManager contextManager) {
    final ScreenPanelViewImpl instance = new ScreenPanelViewImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}