package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer.DynamicFormRendererView;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRendererViewImpl;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGeneratorManager;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;

public class Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicFormRenderer> { public Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DynamicFormRenderer.class, "Type_factory__o_k_w_c_f_d_c_DynamicFormRenderer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicFormRenderer.class, Object.class, IsWidget.class, IsFormView.class });
  }

  public DynamicFormRenderer createInstance(final ContextManager contextManager) {
    final FormHandlerGeneratorManager _formHandlerGenerator_1 = (FormHandlerGeneratorManager) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_i_FormHandlerGeneratorManager__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormRendererView _view_0 = (DynamicFormRendererViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_DynamicFormRendererViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormModelGenerator _dynamicFormModelGenerator_2 = (DynamicFormModelGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormRenderer instance = new DynamicFormRenderer(_view_0, _formHandlerGenerator_1, _dynamicFormModelGenerator_2);
    registerDependentScopedReference(instance, _formHandlerGenerator_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dynamicFormModelGenerator_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DynamicFormRenderer instance) {
    DynamicFormRenderer_init(instance);
  }

  public native static void DynamicFormRenderer_init(DynamicFormRenderer instance) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer::init()();
  }-*/;
}