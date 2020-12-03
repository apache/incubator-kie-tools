package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.widgets.toolbar.DMNGraphProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.layout.GraphProcessor;

public class Type_factory__o_k_w_c_d_c_w_t_DMNGraphProcessor__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNGraphProcessor> { public Type_factory__o_k_w_c_d_c_w_t_DMNGraphProcessor__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNGraphProcessor.class, "Type_factory__o_k_w_c_d_c_w_t_DMNGraphProcessor__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGraphProcessor.class, Object.class, GraphProcessor.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNGraphProcessor createInstance(final ContextManager contextManager) {
    final DMNGraphProcessor instance = new DMNGraphProcessor();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}