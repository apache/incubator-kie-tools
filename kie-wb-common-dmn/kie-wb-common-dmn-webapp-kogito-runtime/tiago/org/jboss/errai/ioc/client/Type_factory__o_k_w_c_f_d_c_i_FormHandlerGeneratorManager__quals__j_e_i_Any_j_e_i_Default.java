package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGeneratorManager;
import org.kie.workbench.common.forms.dynamic.client.init.impl.DynamicFormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.init.impl.StaticFormHandlerGenerator;

public class Type_factory__o_k_w_c_f_d_c_i_FormHandlerGeneratorManager__quals__j_e_i_Any_j_e_i_Default extends Factory<FormHandlerGeneratorManager> { public Type_factory__o_k_w_c_f_d_c_i_FormHandlerGeneratorManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormHandlerGeneratorManager.class, "Type_factory__o_k_w_c_f_d_c_i_FormHandlerGeneratorManager__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormHandlerGeneratorManager.class, Object.class });
  }

  public FormHandlerGeneratorManager createInstance(final ContextManager contextManager) {
    final FormHandlerGenerator _staticGenerator_0 = (StaticFormHandlerGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_i_i_StaticFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_StaticContext");
    final FormHandlerGenerator _dynamicGenerator_1 = (DynamicFormHandlerGenerator) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_i_i_DynamicFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_DynamicContext");
    final FormHandlerGeneratorManager instance = new FormHandlerGeneratorManager(_staticGenerator_0, _dynamicGenerator_1);
    registerDependentScopedReference(instance, _staticGenerator_0);
    registerDependentScopedReference(instance, _dynamicGenerator_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}