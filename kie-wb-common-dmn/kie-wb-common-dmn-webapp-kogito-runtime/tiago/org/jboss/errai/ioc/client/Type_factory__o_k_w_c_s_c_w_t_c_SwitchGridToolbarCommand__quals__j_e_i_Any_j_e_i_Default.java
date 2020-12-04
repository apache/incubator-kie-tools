package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.AbstractToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.command.impl.SwitchGridSessionCommand;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_t_c_SwitchGridToolbarCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SwitchGridToolbarCommand> { public Type_factory__o_k_w_c_s_c_w_t_c_SwitchGridToolbarCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SwitchGridToolbarCommand.class, "Type_factory__o_k_w_c_s_c_w_t_c_SwitchGridToolbarCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SwitchGridToolbarCommand.class, AbstractToolbarCommand.class, Object.class, ToolbarCommand.class });
  }

  public SwitchGridToolbarCommand createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationService _translationService_2 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<SwitchGridSessionCommand> _command_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SwitchGridSessionCommand.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final SwitchGridToolbarCommand instance = new SwitchGridToolbarCommand(_definitionUtils_0, _command_1, _translationService_2);
    registerDependentScopedReference(instance, _command_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}