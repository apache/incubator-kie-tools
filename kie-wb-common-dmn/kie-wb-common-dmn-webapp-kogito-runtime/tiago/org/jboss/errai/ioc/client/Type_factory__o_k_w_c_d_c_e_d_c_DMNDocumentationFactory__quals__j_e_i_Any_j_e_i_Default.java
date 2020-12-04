package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.uberfire.rpc.SessionInfo;

public class Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationFactory> { public Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDocumentationFactory.class, "Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDocumentationFactory.class, Object.class });
  }

  public DMNDocumentationFactory createInstance(final ContextManager contextManager) {
    final CanvasFileExport _canvasFileExport_0 = (CanvasFileExport) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_u_CanvasFileExport__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _graphUtils_4 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_1 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationDRDsFactory _drdsFactory_2 = (DMNDocumentationDRDsFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationDRDsFactory__quals__j_e_i_Any_j_e_i_Default");
    final SessionInfo _sessionInfo_3 = (SessionInfo) contextManager.getInstance("Producer_factory__o_u_r_SessionInfo__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationFactory instance = new DMNDocumentationFactory(_canvasFileExport_0, _translationService_1, _drdsFactory_2, _sessionInfo_3, _graphUtils_4);
    registerDependentScopedReference(instance, _graphUtils_4);
    registerDependentScopedReference(instance, _translationService_1);
    registerDependentScopedReference(instance, _drdsFactory_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}