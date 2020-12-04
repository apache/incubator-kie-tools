package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.docks.navigator.common.CanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.common.LazyCanvasFocusUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramSelected;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorBaseItemFactory;
import org.kie.workbench.common.dmn.client.docks.navigator.factories.DecisionNavigatorNestedItemFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorBaseItemFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorBaseItemFactory> { public Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorBaseItemFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorBaseItemFactory.class, "Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorBaseItemFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorBaseItemFactory.class, Object.class });
  }

  public DecisionNavigatorBaseItemFactory createInstance(final ContextManager contextManager) {
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final LazyCanvasFocusUtils _lazyCanvasFocusUtils_6 = (LazyCanvasFocusUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_c_LazyCanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_5 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final CanvasFocusUtils _canvasFocusUtils_2 = (CanvasFocusUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_c_CanvasFocusUtils__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_4 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorNestedItemFactory _nestedItemFactory_0 = (DecisionNavigatorNestedItemFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_f_DecisionNavigatorNestedItemFactory__quals__j_e_i_Any_j_e_i_Default");
    final Event<DMNDiagramSelected> _selectedEvent_7 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { DMNDiagramSelected.class }, new Annotation[] { });
    final TextPropertyProviderFactory _textPropertyProviderFactory_1 = (TextPropertyProviderFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorBaseItemFactory instance = new DecisionNavigatorBaseItemFactory(_nestedItemFactory_0, _textPropertyProviderFactory_1, _canvasFocusUtils_2, _definitionUtils_3, _translationService_4, _dmnDiagramsSession_5, _lazyCanvasFocusUtils_6, _selectedEvent_7);
    registerDependentScopedReference(instance, _translationService_4);
    registerDependentScopedReference(instance, _nestedItemFactory_0);
    registerDependentScopedReference(instance, _selectedEvent_7);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}