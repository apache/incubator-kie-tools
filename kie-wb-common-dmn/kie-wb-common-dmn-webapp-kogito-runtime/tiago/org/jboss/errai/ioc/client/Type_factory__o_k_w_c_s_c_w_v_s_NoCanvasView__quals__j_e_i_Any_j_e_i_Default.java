package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.views.session.NoCanvasView;

public class Type_factory__o_k_w_c_s_c_w_v_s_NoCanvasView__quals__j_e_i_Any_j_e_i_Default extends Factory<NoCanvasView> { public Type_factory__o_k_w_c_s_c_w_v_s_NoCanvasView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NoCanvasView.class, "Type_factory__o_k_w_c_s_c_w_v_s_NoCanvasView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NoCanvasView.class, Object.class, IsWidget.class });
  }

  public NoCanvasView createInstance(final ContextManager contextManager) {
    final NoCanvasView instance = new NoCanvasView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}