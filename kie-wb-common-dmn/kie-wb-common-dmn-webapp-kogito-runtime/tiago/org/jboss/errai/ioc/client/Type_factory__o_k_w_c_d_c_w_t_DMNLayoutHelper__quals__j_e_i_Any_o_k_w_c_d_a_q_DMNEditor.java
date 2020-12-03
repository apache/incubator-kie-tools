package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNLayoutHelper;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNSugiyamaLayoutService;
import org.kie.workbench.common.stunner.core.client.components.layout.LayoutHelper;

public class Type_factory__o_k_w_c_d_c_w_t_DMNLayoutHelper__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNLayoutHelper> { public Type_factory__o_k_w_c_d_c_w_t_DMNLayoutHelper__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNLayoutHelper.class, "Type_factory__o_k_w_c_d_c_w_t_DMNLayoutHelper__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNLayoutHelper.class, LayoutHelper.class, Object.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNLayoutHelper createInstance(final ContextManager contextManager) {
    final DMNSugiyamaLayoutService _layoutService_0 = (DMNSugiyamaLayoutService) contextManager.getInstance("Type_factory__o_k_w_c_d_c_w_t_DMNSugiyamaLayoutService__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DMNLayoutHelper instance = new DMNLayoutHelper(_layoutService_0);
    registerDependentScopedReference(instance, _layoutService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}