package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.forms.client.event.FormPropertiesOpened;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidget;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetView;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetView.Presenter;
import org.kie.workbench.common.stunner.forms.client.widgets.FormPropertiesWidgetViewImpl;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsCanvasSessionHandler;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsFlushManager;
import org.kie.workbench.common.stunner.forms.client.widgets.container.FormsContainer;

public class Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<FormPropertiesWidget> { public Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormPropertiesWidget.class, "Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormPropertiesWidget.class, Object.class, IsElement.class, Presenter.class });
  }

  public FormPropertiesWidget createInstance(final ContextManager contextManager) {
    final FormsCanvasSessionHandler _formSessionHandler_2 = (FormsCanvasSessionHandler) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_FormsCanvasSessionHandler__quals__j_e_i_Any_j_e_i_Default");
    final Event<FormPropertiesOpened> _propertiesOpenedEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { FormPropertiesOpened.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_1 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final FormsFlushManager _formsFlushManager_5 = (FormsFlushManager) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_FormsFlushManager__quals__j_e_i_Any_j_e_i_Default");
    final FormPropertiesWidgetView _view_0 = (FormPropertiesWidgetViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_FormPropertiesWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final FormsContainer _formsContainer_4 = (FormsContainer) contextManager.getInstance("Type_factory__o_k_w_c_s_f_c_w_c_FormsContainer__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_6 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final FormPropertiesWidget instance = new FormPropertiesWidget(_view_0, _definitionUtils_1, _formSessionHandler_2, _propertiesOpenedEvent_3, _formsContainer_4, _formsFlushManager_5, _translationService_6);
    registerDependentScopedReference(instance, _formSessionHandler_2);
    registerDependentScopedReference(instance, _propertiesOpenedEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _formsContainer_4);
    registerDependentScopedReference(instance, _translationService_6);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormPropertiesWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormPropertiesWidget instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final FormPropertiesWidget instance) {
    instance.init();
  }
}