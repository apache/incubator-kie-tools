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
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInputViewImpl;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

public class Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInput__quals__j_e_i_Any_j_e_i_Default extends Factory<MultipleSelectorInput> { public Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInput__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MultipleSelectorInput.class, "Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInput__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MultipleSelectorInput.class, Object.class, IsWidget.class, Presenter.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasHandlers.class });
  }

  public MultipleSelectorInput createInstance(final ContextManager contextManager) {
    final MultipleSelectorInputView _view_0 = (MultipleSelectorInputViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_l_s_i_MultipleSelectorInputViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final LiveSearchDropDown _selector_1 = (LiveSearchDropDown) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default");
    final MultipleSelectorInput instance = new MultipleSelectorInput(_view_0, _selector_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _selector_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}