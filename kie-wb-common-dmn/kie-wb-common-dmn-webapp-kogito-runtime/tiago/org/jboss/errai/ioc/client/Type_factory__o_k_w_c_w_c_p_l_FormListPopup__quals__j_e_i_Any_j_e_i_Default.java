package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopup;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopupView;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopupView.Presenter;
import org.kie.workbench.common.widgets.client.popups.list.FormListPopupViewImpl;

public class Type_factory__o_k_w_c_w_c_p_l_FormListPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<FormListPopup> { public Type_factory__o_k_w_c_w_c_p_l_FormListPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormListPopup.class, "Type_factory__o_k_w_c_w_c_p_l_FormListPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormListPopup.class, Object.class, Presenter.class });
  }

  public FormListPopup createInstance(final ContextManager contextManager) {
    final FormListPopupView _view_0 = (FormListPopupViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_l_FormListPopupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final FormListPopup instance = new FormListPopup(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}