package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.canvas.controls.keyboard.shortcut.AppendDecisionShortcut;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.AbstractAppendNodeShortcut;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.shortcut.KeyboardShortcut;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ToolboxDomainLookups;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;

public class Type_factory__o_k_w_c_d_c_c_c_k_s_AppendDecisionShortcut__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<AppendDecisionShortcut> { public Type_factory__o_k_w_c_d_c_c_c_k_s_AppendDecisionShortcut__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(AppendDecisionShortcut.class, "Type_factory__o_k_w_c_d_c_c_c_k_s_AppendDecisionShortcut__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AppendDecisionShortcut.class, AbstractAppendNodeShortcut.class, Object.class, KeyboardShortcut.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public AppendDecisionShortcut createInstance(final ContextManager contextManager) {
    final DefinitionsCacheRegistry _definitionsCacheRegistry_1 = (DefinitionsCacheRegistry) contextManager.getInstance("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final GeneralCreateNodeAction _generalCreateNodeAction_2 = (GeneralCreateNodeAction) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_t_a_GeneralCreateNodeAction__quals__j_e_i_Any_j_e_i_Default");
    final ToolboxDomainLookups _toolboxDomainLookups_0 = (ToolboxDomainLookups) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_t_a_ToolboxDomainLookups__quals__j_e_i_Any_j_e_i_Default");
    final AppendDecisionShortcut instance = new AppendDecisionShortcut(_toolboxDomainLookups_0, _definitionsCacheRegistry_1, _generalCreateNodeAction_2);
    registerDependentScopedReference(instance, _generalCreateNodeAction_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}