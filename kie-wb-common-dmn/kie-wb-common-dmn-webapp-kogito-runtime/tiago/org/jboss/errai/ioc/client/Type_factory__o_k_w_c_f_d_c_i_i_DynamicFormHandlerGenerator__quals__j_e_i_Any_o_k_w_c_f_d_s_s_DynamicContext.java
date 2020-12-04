package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.validation.client.dynamic.DynamicValidator;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.init.impl.DynamicFormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.DynamicContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldStateValidatorImpl;

public class Type_factory__o_k_w_c_f_d_c_i_i_DynamicFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_DynamicContext extends Factory<DynamicFormHandlerGenerator> { public Type_factory__o_k_w_c_f_d_c_i_i_DynamicFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_DynamicContext() {
    super(new FactoryHandleImpl(DynamicFormHandlerGenerator.class, "Type_factory__o_k_w_c_f_d_c_i_i_DynamicFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_DynamicContext", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicFormHandlerGenerator.class, Object.class, FormHandlerGenerator.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DynamicContext() {
        public Class annotationType() {
          return DynamicContext.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.forms.dynamic.service.shared.DynamicContext()";
        }
    } });
  }

  public DynamicFormHandlerGenerator createInstance(final ContextManager contextManager) {
    final FieldStateValidator _fieldStateValidator_1 = (FieldStateValidatorImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default");
    final DynamicValidator _validator_0 = (DynamicValidator) contextManager.getInstance("DynamicValidatorFactory");
    final MapModelBindingHelper _helper_2 = (MapModelBindingHelper) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_h_MapModelBindingHelper__quals__j_e_i_Any_j_e_i_Default");
    final DynamicFormHandlerGenerator instance = new DynamicFormHandlerGenerator(_validator_0, _fieldStateValidator_1, _helper_2);
    registerDependentScopedReference(instance, _fieldStateValidator_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}