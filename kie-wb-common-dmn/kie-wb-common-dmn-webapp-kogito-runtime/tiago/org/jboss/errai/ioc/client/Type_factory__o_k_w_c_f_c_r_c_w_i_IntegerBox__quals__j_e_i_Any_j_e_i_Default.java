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
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBox;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBoxView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.integerBox.IntegerBoxViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBox__quals__j_e_i_Any_j_e_i_Default extends Factory<IntegerBox> { public Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBox__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(IntegerBox.class, "Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBox__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { IntegerBox.class, Object.class, IsWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasHandlers.class });
  }

  public IntegerBox createInstance(final ContextManager contextManager) {
    final IntegerBoxView _view_0 = (IntegerBoxViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_r_c_w_i_IntegerBoxViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final IntegerBox instance = new IntegerBox(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}