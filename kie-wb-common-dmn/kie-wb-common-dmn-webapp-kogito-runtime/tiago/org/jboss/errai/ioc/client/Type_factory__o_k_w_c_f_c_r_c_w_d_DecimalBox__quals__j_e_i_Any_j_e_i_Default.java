package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBox;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox.DecimalBoxViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBox__quals__j_e_i_Any_j_e_i_Default extends Factory<DecimalBox> { public Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBox__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecimalBox.class, "Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBox__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecimalBox.class, Object.class, IsWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasHandlers.class });
  }

  public DecimalBox createInstance(final ContextManager contextManager) {
    final DecimalBoxView _view_0 = (DecimalBoxViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_r_c_w_d_DecimalBoxViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DecimalBox instance = new DecimalBox(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}