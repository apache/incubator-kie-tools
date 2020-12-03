package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.DMNEditorSessionCommands;
import org.kie.workbench.common.stunner.core.client.session.command.ManagedClientSessionCommands;
import org.kie.workbench.common.stunner.kogito.client.session.EditorSessionCommands;

public class Type_factory__o_k_w_c_d_w_k_c_c_s_DMNEditorSessionCommands__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNEditorSessionCommands> { public Type_factory__o_k_w_c_d_w_k_c_c_s_DMNEditorSessionCommands__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNEditorSessionCommands.class, "Type_factory__o_k_w_c_d_w_k_c_c_s_DMNEditorSessionCommands__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditorSessionCommands.class, EditorSessionCommands.class, Object.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNEditorSessionCommands createInstance(final ContextManager contextManager) {
    final ManagedClientSessionCommands _commands_0 = (ManagedClientSessionCommands) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_c_ManagedClientSessionCommands__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditorSessionCommands instance = new DMNEditorSessionCommands(_commands_0);
    registerDependentScopedReference(instance, _commands_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNEditorSessionCommands instance) {
    instance.init();
  }
}