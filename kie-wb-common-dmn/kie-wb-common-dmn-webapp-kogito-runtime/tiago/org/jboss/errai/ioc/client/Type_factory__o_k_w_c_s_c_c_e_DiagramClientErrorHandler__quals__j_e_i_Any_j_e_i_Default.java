package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.error.ClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.error.DiagramClientErrorHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramClientErrorHandler> { public Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramClientErrorHandler.class, "Type_factory__o_k_w_c_s_c_c_e_DiagramClientErrorHandler__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramClientErrorHandler.class, ClientErrorHandler.class, Object.class });
  }

  public DiagramClientErrorHandler createInstance(final ContextManager contextManager) {
    final ClientTranslationService _translationService_0 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DiagramClientErrorHandler instance = new DiagramClientErrorHandler(_translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}