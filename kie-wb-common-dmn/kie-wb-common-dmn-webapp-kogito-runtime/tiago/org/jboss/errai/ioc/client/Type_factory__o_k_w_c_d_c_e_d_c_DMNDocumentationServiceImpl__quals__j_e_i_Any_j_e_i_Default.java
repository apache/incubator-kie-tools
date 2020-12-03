package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationService;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationServiceImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common.DMNDocumentationFactory;
import org.kie.workbench.common.stunner.core.documentation.service.DiagramDocumentationService;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;

public class Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationServiceImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDocumentationServiceImpl> { public Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationServiceImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDocumentationServiceImpl.class, "Type_factory__o_k_w_c_d_c_e_d_c_DMNDocumentationServiceImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDocumentationServiceImpl.class, Object.class, DMNDocumentationService.class, DiagramDocumentationService.class });
  }

  public DMNDocumentationServiceImpl createInstance(final ContextManager contextManager) {
    final ClientMustacheTemplateRenderer _mustacheTemplateRenderer_0 = (ClientMustacheTemplateRenderer) contextManager.getInstance("Type_factory__o_u_e_e_c_c_t_m_ClientMustacheTemplateRenderer__quals__j_e_i_Any_j_e_i_Default");
    final org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory _dmnDocumentationFactory_1 = (DMNDocumentationFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_e_d_c_DMNDocumentationFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNDocumentationServiceImpl instance = new DMNDocumentationServiceImpl(_mustacheTemplateRenderer_0, _dmnDocumentationFactory_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}