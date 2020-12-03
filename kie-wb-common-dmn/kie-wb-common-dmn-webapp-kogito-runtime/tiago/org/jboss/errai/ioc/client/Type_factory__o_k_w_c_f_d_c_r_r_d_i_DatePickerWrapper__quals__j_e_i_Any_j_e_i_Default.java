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
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperView;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperView.Presenter;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.date.input.DatePickerWrapperViewImpl;

public class Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapper__quals__j_e_i_Any_j_e_i_Default extends Factory<DatePickerWrapper> { public Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DatePickerWrapper.class, "Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DatePickerWrapper.class, Object.class, IsWidget.class, Presenter.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasHandlers.class });
  }

  public DatePickerWrapper createInstance(final ContextManager contextManager) {
    final DatePickerWrapperView _view_0 = (DatePickerWrapperViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_r_d_i_DatePickerWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DatePickerWrapper instance = new DatePickerWrapper(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}