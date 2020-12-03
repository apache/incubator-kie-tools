package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.components.palette.DMNPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.CollapsedPaletteDefinitionBuilder;
import org.kie.workbench.common.stunner.core.client.components.palette.PaletteDefinitionBuilder;

public class Type_factory__o_k_w_c_d_c_c_p_DMNPaletteDefinitionBuilder__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNPaletteDefinitionBuilder> { public Type_factory__o_k_w_c_d_c_c_p_DMNPaletteDefinitionBuilder__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNPaletteDefinitionBuilder.class, "Type_factory__o_k_w_c_d_c_c_p_DMNPaletteDefinitionBuilder__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNPaletteDefinitionBuilder.class, Object.class, PaletteDefinitionBuilder.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNPaletteDefinitionBuilder createInstance(final ContextManager contextManager) {
    final CollapsedPaletteDefinitionBuilder _paletteDefinitionBuilder_0 = (CollapsedPaletteDefinitionBuilder) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_p_CollapsedPaletteDefinitionBuilder__quals__j_e_i_Any_j_e_i_Default");
    final DMNPaletteDefinitionBuilder instance = new DMNPaletteDefinitionBuilder(_paletteDefinitionBuilder_0);
    registerDependentScopedReference(instance, _paletteDefinitionBuilder_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNPaletteDefinitionBuilder instance) {
    instance.init();
  }
}