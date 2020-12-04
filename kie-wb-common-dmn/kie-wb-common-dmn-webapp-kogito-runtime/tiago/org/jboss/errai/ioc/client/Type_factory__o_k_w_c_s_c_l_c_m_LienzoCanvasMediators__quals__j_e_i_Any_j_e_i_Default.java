package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.client.context.EditorContextProvider;
import org.appformer.kogito.bridge.client.context.impl.KogitoEditorContextProviderImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.components.mediators.LienzoCanvasMediators;
import org.kie.workbench.common.stunner.client.lienzo.components.views.LienzoCanvasNotification;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoKeyEventHandlerImpl;

public class Type_factory__o_k_w_c_s_c_l_c_m_LienzoCanvasMediators__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasMediators> { public Type_factory__o_k_w_c_s_c_l_c_m_LienzoCanvasMediators__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoCanvasMediators.class, "Type_factory__o_k_w_c_s_c_l_c_m_LienzoCanvasMediators__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoCanvasMediators.class, Object.class });
  }

  public LienzoCanvasMediators createInstance(final ContextManager contextManager) {
    final KeyEventHandler _keyEventHandler_0 = (KogitoKeyEventHandlerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationService _translationService_1 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final EditorContextProvider _editorContextProvider_3 = (KogitoEditorContextProviderImpl) contextManager.getInstance("Type_factory__o_a_k_b_c_c_i_KogitoEditorContextProviderImpl__quals__j_e_i_Any_j_e_i_Default");
    final LienzoCanvasNotification _notification_2 = (LienzoCanvasNotification) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_v_LienzoCanvasNotification__quals__j_e_i_Any_j_e_i_Default");
    final LienzoCanvasMediators instance = new LienzoCanvasMediators(_keyEventHandler_0, _translationService_1, _notification_2, _editorContextProvider_3);
    registerDependentScopedReference(instance, _keyEventHandler_0);
    registerDependentScopedReference(instance, _notification_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LienzoCanvasMediators) instance, contextManager);
  }

  public void destroyInstanceHelper(final LienzoCanvasMediators instance, final ContextManager contextManager) {
    instance.destroy();
  }
}