package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.AbstractFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer.ModalFormDisplayerView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayerViewImpl;

public class Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<ModalFormDisplayer> { public Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ModalFormDisplayer.class, "Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ModalFormDisplayer.class, AbstractFormDisplayer.class, Object.class, FormDisplayer.class, IsWidget.class });
  }

  public ModalFormDisplayer createInstance(final ContextManager contextManager) {
    final ModalFormDisplayerView _view_0 = (ModalFormDisplayerViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ModalFormDisplayer instance = new ModalFormDisplayer(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}