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
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInput;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.MultipleInputViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.creator.input.widget.MultipleInputComponent;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInput__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleInput> { public Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInput__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleInput.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInput__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleInput.class, Object.class, IsWidget.class, Presenter.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasHandlers.class });
  }

  public MultipleInput createInstance(final ContextManager contextManager) {
    final MultipleInputView _view_0 = (MultipleInputViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_MultipleInputViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final MultipleInputComponent _component_1 = (MultipleInputComponent) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_l_c_i_w_MultipleInputComponent__quals__j_e_i_Any_j_e_i_Default");
    final MultipleInput instance = new MultipleInput(_view_0, _component_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _component_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}