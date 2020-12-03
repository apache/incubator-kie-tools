package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editor.DMNEditorMenuItemsBuilder;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNEditorMenuItemsBuilder> { public Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNEditorMenuItemsBuilder.class, "Type_factory__o_k_w_c_d_w_k_c_c_e_DMNEditorMenuItemsBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNEditorMenuItemsBuilder.class, Object.class });
  }

  public DMNEditorMenuItemsBuilder createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final PopupUtil _popupUtil_1 = (PopupUtil) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_p_PopupUtil__quals__j_e_i_Any_j_e_i_Default");
    final DMNEditorMenuItemsBuilder instance = new DMNEditorMenuItemsBuilder(_translationService_0, _popupUtil_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}