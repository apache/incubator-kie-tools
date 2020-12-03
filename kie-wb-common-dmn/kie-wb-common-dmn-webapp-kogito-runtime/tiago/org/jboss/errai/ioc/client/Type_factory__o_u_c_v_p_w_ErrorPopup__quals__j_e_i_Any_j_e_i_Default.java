package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.widgets.ErrorPopup;
import org.uberfire.client.views.pfly.widgets.ErrorPopup.View;
import org.uberfire.client.views.pfly.widgets.ErrorPopupView;

public class Type_factory__o_u_c_v_p_w_ErrorPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopup> { public Type_factory__o_u_c_v_p_w_ErrorPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ErrorPopup.class, "Type_factory__o_u_c_v_p_w_ErrorPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ErrorPopup.class, Object.class });
  }

  public ErrorPopup createInstance(final ContextManager contextManager) {
    final View _view_0 = (ErrorPopupView) contextManager.getInstance("Type_factory__o_u_c_v_p_w_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default");
    final ErrorPopup instance = new ErrorPopup(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ErrorPopup instance) {
    instance.init();
  }
}