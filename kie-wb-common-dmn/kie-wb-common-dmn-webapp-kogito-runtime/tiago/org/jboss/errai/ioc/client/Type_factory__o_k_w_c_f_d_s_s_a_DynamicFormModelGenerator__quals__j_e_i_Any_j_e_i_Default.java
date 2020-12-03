package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.adf.engine.client.formGeneration.util.ClientPropertuValueExtractor;
import org.kie.workbench.common.forms.adf.engine.shared.FormBuildingService;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.util.PropertyValueExtractor;
import org.kie.workbench.common.forms.adf.engine.shared.impl.FormBuildingServiceImpl;
import org.kie.workbench.common.forms.dynamic.service.shared.adf.DynamicFormModelGenerator;

public class Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicFormModelGenerator> { public Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DynamicFormModelGenerator.class, "Type_factory__o_k_w_c_f_d_s_s_a_DynamicFormModelGenerator__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicFormModelGenerator.class, Object.class });
  }

  public DynamicFormModelGenerator createInstance(final ContextManager contextManager) {
    final FormBuildingService _formBuildingService_0 = (FormBuildingServiceImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_a_e_s_i_FormBuildingServiceImpl__quals__j_e_i_Any_j_e_i_Default");
    final PropertyValueExtractor _propertyValueExtractor_1 = (ClientPropertuValueExtractor) contextManager.getInstance("Type_factory__o_k_w_c_f_a_e_c_f_u_ClientPropertuValueExtractor__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormModelGenerator instance = new DynamicFormModelGenerator(_formBuildingService_0, _propertyValueExtractor_1);
    registerDependentScopedReference(instance, _propertyValueExtractor_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}