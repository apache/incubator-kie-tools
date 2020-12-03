package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.views.AnimatedFloatingWidgetView;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingView;
import org.kie.workbench.common.stunner.core.client.components.views.FloatingWidgetView;

public class Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default extends Factory<AnimatedFloatingWidgetView> { public Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AnimatedFloatingWidgetView.class, "Type_factory__o_k_w_c_s_c_w_v_AnimatedFloatingWidgetView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AnimatedFloatingWidgetView.class, FloatingWidgetView.class, Object.class, FloatingView.class });
  }

  public AnimatedFloatingWidgetView createInstance(final ContextManager contextManager) {
    final AnimatedFloatingWidgetView instance = new AnimatedFloatingWidgetView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}