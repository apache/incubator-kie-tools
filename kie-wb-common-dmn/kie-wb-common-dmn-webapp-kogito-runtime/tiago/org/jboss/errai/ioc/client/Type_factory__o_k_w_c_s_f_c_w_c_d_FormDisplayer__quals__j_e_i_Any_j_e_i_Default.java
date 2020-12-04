package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayer;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerView;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerView.Presenter;
import org.kie.workbench.common.stunner.forms.client.widgets.container.displayer.FormDisplayerViewImpl;

public class Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayer__quals__j_e_i_Any_j_e_i_Default extends Factory<FormDisplayer> { public Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormDisplayer.class, "Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormDisplayer.class, Object.class, Presenter.class, IsElement.class });
  }

  public FormDisplayer createInstance(final ContextManager contextManager) {
    final FormDisplayerView _view_0 = (FormDisplayerViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_c_d_FormDisplayerViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormRenderer _renderer_1 = (DynamicFormRenderer) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormModelGenerator _modelGenerator_2 = (DynamicFormModelGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default");
    final FormDisplayer instance = new FormDisplayer(_view_0, _renderer_1, _modelGenerator_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _renderer_1);
    registerDependentScopedReference(instance, _modelGenerator_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormDisplayer) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormDisplayer instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final FormDisplayer instance) {
    instance.init();
  }
}