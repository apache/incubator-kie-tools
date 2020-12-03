package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuItemsBuilder;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuSessionItems;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.DMNEditorSessionCommands;

public class Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuSessionItems__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditorMenuSessionItems> { public Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuSessionItems__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNEditorMenuSessionItems.class, "Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuSessionItems__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditorMenuSessionItems.class, Object.class });
  }

  public DMNEditorMenuSessionItems createInstance(final ContextManager contextManager) {
    final DMNEditorMenuItemsBuilder _itemsBuilder_0 = (DMNEditorMenuItemsBuilder) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditorSessionCommands _sessionCommands_1 = (DMNEditorSessionCommands) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_DMNEditorSessionCommands__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DMNEditorMenuSessionItems instance = new DMNEditorMenuSessionItems(_itemsBuilder_0, _sessionCommands_1);
    registerDependentScopedReference(instance, _itemsBuilder_0);
    registerDependentScopedReference(instance, _sessionCommands_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNEditorMenuSessionItems) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNEditorMenuSessionItems instance, final ContextManager contextManager) {
    instance.destroy();
  }
}