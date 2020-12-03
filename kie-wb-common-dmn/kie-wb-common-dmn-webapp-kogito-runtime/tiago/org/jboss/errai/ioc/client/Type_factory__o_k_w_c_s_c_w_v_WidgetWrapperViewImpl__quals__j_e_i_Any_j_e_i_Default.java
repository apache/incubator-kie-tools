package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperViewImpl;

public class Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<WidgetWrapperViewImpl> { public Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WidgetWrapperViewImpl.class, "Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WidgetWrapperViewImpl.class, Object.class, WidgetWrapperView.class, IsWidget.class });
  }

  public WidgetWrapperViewImpl createInstance(final ContextManager contextManager) {
    final WidgetWrapperViewImpl instance = new WidgetWrapperViewImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final WidgetWrapperViewImpl instance) {
    instance.init();
  }
}