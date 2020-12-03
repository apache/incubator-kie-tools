package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.FormPopupView.Presenter;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupView;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl;

public class Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBoxFormPopup> { public Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TextBoxFormPopup.class, "Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TextBoxFormPopup.class, FormPopup.class, Object.class, Presenter.class });
  }

  public TextBoxFormPopup createInstance(final ContextManager contextManager) {
    final TextBoxFormPopupView _view_0 = (TextBoxFormPopupViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final TextBoxFormPopup instance = new TextBoxFormPopup(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}