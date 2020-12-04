package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.validation.Validator;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.init.FormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.client.init.impl.StaticFormHandlerGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.StaticContext;
import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.impl.FieldStateValidatorImpl;

public class Type_factory__o_k_w_c_f_d_c_i_i_StaticFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_StaticContext extends Factory<StaticFormHandlerGenerator> { public Type_factory__o_k_w_c_f_d_c_i_i_StaticFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_StaticContext() {
    super(new FactoryHandleImpl(StaticFormHandlerGenerator.class, "Type_factory__o_k_w_c_f_d_c_i_i_StaticFormHandlerGenerator__quals__j_e_i_Any_o_k_w_c_f_d_s_s_StaticContext", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StaticFormHandlerGenerator.class, Object.class, FormHandlerGenerator.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new StaticContext() {
        public Class annotationType() {
          return StaticContext.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.forms.dynamic.service.shared.StaticContext()";
        }
    } });
  }

  public StaticFormHandlerGenerator createInstance(final ContextManager contextManager) {
    final Validator _validator_0 = (Validator) contextManager.getInstance("Provider_factory__j_v_Validator__quals__j_e_i_Any_j_e_i_Default");
    final FieldStateValidator _fieldStateValidator_1 = (FieldStateValidatorImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_p_e_h_i_FieldStateValidatorImpl__quals__j_e_i_Any_j_e_i_Default");
    final StaticFormHandlerGenerator instance = new StaticFormHandlerGenerator(_validator_0, _fieldStateValidator_1);
    registerDependentScopedReference(instance, _validator_0);
    registerDependentScopedReference(instance, _fieldStateValidator_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}