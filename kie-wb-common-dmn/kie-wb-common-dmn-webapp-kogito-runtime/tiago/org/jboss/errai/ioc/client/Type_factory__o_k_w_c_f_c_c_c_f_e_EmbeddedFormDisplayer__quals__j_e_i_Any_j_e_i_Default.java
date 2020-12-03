package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.AbstractFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.FormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer.EmbeddedFormDisplayerView;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayerViewImpl;

public class Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<EmbeddedFormDisplayer> { public Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EmbeddedFormDisplayer.class, "Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EmbeddedFormDisplayer.class, AbstractFormDisplayer.class, Object.class, FormDisplayer.class, IsWidget.class });
  }

  public EmbeddedFormDisplayer createInstance(final ContextManager contextManager) {
    final EmbeddedFormDisplayerView _view_0 = (EmbeddedFormDisplayerViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final EmbeddedFormDisplayer instance = new EmbeddedFormDisplayer(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}