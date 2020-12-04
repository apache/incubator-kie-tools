package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationMessageTranslatorUtils;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupView;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupView.Presenter;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_k_w_c_w_c_p_v_ValidationPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidationPopup> { public Type_factory__o_k_w_c_w_c_p_v_ValidationPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValidationPopup.class, "Type_factory__o_k_w_c_w_c_p_v_ValidationPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValidationPopup.class, Object.class, Presenter.class });
  }

  public ValidationPopup createInstance(final ContextManager contextManager) {
    final ValidationPopupView _view_0 = (ValidationPopupViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_v_ValidationPopupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_2 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ValidationMessageTranslatorUtils _validationMessageTranslatorUtils_1 = (ValidationMessageTranslatorUtils) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_v_ValidationMessageTranslatorUtils__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notificationEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final ValidationPopup instance = new ValidationPopup(_view_0, _validationMessageTranslatorUtils_1, _translationService_2, _notificationEvent_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _translationService_2);
    registerDependentScopedReference(instance, _notificationEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ValidationPopup instance) {
    instance.init();
  }
}