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
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAhead;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead.BindableTypeAheadViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAhead__quals__j_e_i_Any_j_e_i_Default extends Factory<BindableTypeAhead> { public Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAhead__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BindableTypeAhead.class, "Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAhead__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BindableTypeAhead.class, Object.class, IsWidget.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasHandlers.class });
  }

  public BindableTypeAhead createInstance(final ContextManager contextManager) {
    final BindableTypeAheadView _view_0 = (BindableTypeAheadViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_r_c_w_t_BindableTypeAheadViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final BindableTypeAhead instance = new BindableTypeAhead(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}