package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import jsinterop.base.Js;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;

public class Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ConfirmPopup> { public interface o_u_c_v_p_w_ConfirmPopupTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/ConfirmPopup.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConfirmPopup.class, "Type_factory__o_u_c_v_p_w_ConfirmPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConfirmPopup.class, Object.class });
  }

  public ConfirmPopup createInstance(final ContextManager contextManager) {
    final ConfirmPopup instance = new ConfirmPopup();
    setIncompleteInstance(instance);
    final InlineNotification ConfirmPopup_confirmInlineNotification = (InlineNotification) contextManager.getInstance("Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ConfirmPopup_confirmInlineNotification);
    ConfirmPopup_InlineNotification_confirmInlineNotification(instance, ConfirmPopup_confirmInlineNotification);
    final Span ConfirmPopup_modalTitle = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ConfirmPopup_modalTitle);
    ConfirmPopup_Span_modalTitle(instance, ConfirmPopup_modalTitle);
    final Span ConfirmPopup_modalConfirmationMessageLabel = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ConfirmPopup_modalConfirmationMessageLabel);
    ConfirmPopup_Span_modalConfirmationMessageLabel(instance, ConfirmPopup_modalConfirmationMessageLabel);
    final Button ConfirmPopup_okButton = (Button) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ConfirmPopup_okButton);
    ConfirmPopup_Button_okButton(instance, ConfirmPopup_okButton);
    final Button ConfirmPopup_cancelButton = (Button) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ConfirmPopup_cancelButton);
    ConfirmPopup_Button_cancelButton(instance, ConfirmPopup_cancelButton);
    final Modal ConfirmPopup_modal = (Modal) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ConfirmPopup_modal);
    ConfirmPopup_Modal_modal(instance, ConfirmPopup_modal);
    final TranslationService ConfirmPopup_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ConfirmPopup_translationService);
    ConfirmPopup_TranslationService_translationService(instance, ConfirmPopup_translationService);
    o_u_c_v_p_w_ConfirmPopupTemplateResource templateForConfirmPopup = GWT.create(o_u_c_v_p_w_ConfirmPopupTemplateResource.class);
    Element parentElementForTemplateOfConfirmPopup = TemplateUtil.getRootTemplateParentElement(templateForConfirmPopup.getContents().getText(), "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfConfirmPopup));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfConfirmPopup));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("confirm-title", new DataFieldMeta());
    dataFieldMetas.put("confirm-inline-notification", new DataFieldMeta());
    dataFieldMetas.put("confirm-message", new DataFieldMeta());
    dataFieldMetas.put("confirm-cancel", new DataFieldMeta());
    dataFieldMetas.put("confirm-ok", new DataFieldMeta());
    dataFieldMetas.put("modal", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ConfirmPopup", "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ConfirmPopup_Span_modalTitle(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "confirm-title");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ConfirmPopup", "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", new Supplier<Widget>() {
      public Widget get() {
        return TemplateWidgetMapper.get(ConfirmPopup_InlineNotification_confirmInlineNotification(instance));
      }
    }, dataFieldElements, dataFieldMetas, "confirm-inline-notification");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ConfirmPopup", "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ConfirmPopup_Span_modalConfirmationMessageLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "confirm-message");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ConfirmPopup", "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(ConfirmPopup_Button_cancelButton(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "confirm-cancel");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ConfirmPopup", "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(ConfirmPopup_Button_okButton(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "confirm-ok");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ConfirmPopup", "org/uberfire/client/views/pfly/widgets/ConfirmPopup.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(ConfirmPopup_Modal_modal(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "modal");
    templateFieldsMap.put("confirm-title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ConfirmPopup_Span_modalTitle(instance))));
    templateFieldsMap.put("confirm-inline-notification", TemplateWidgetMapper.get(ConfirmPopup_InlineNotification_confirmInlineNotification(instance)));
    templateFieldsMap.put("confirm-message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ConfirmPopup_Span_modalConfirmationMessageLabel(instance))));
    templateFieldsMap.put("confirm-cancel", ElementWrapperWidget.getWidget(ConfirmPopup_Button_cancelButton(instance).getElement(), null));
    templateFieldsMap.put("confirm-ok", ElementWrapperWidget.getWidget(ConfirmPopup_Button_okButton(instance).getElement(), null));
    templateFieldsMap.put("modal", ElementWrapperWidget.getWidget(ConfirmPopup_Modal_modal(instance).getElement()));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfConfirmPopup), templateFieldsMap.values());
    final EventListener listenerForMouseEventCallingOnCloseClick = new EventListener() {
      public void call(Event event) {
        instance.onCloseClick(Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, dataFieldElements.get("confirm-close"), listenerForMouseEventCallingOnCloseClick, "click");
    final EventListener listenerForMouseEventCallingOnCancelClick = new EventListener() {
      public void call(Event event) {
        instance.onCancelClick(Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, templateFieldsMap.get("confirm-cancel"), listenerForMouseEventCallingOnCancelClick, "click");
    final EventListener listenerForMouseEventCallingOnOkClick = new EventListener() {
      public void call(Event event) {
        instance.onOkClick(Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, templateFieldsMap.get("confirm-ok"), listenerForMouseEventCallingOnOkClick, "click");
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ConfirmPopup) instance, contextManager);
  }

  public void destroyInstanceHelper(final ConfirmPopup instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final ConfirmPopup instance) {
    instance.init();
  }

  native static Button ConfirmPopup_Button_cancelButton(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::cancelButton;
  }-*/;

  native static void ConfirmPopup_Button_cancelButton(ConfirmPopup instance, Button value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::cancelButton = value;
  }-*/;

  native static TranslationService ConfirmPopup_TranslationService_translationService(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::translationService;
  }-*/;

  native static void ConfirmPopup_TranslationService_translationService(ConfirmPopup instance, TranslationService value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::translationService = value;
  }-*/;

  native static Modal ConfirmPopup_Modal_modal(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::modal;
  }-*/;

  native static void ConfirmPopup_Modal_modal(ConfirmPopup instance, Modal value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::modal = value;
  }-*/;

  native static Span ConfirmPopup_Span_modalTitle(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::modalTitle;
  }-*/;

  native static void ConfirmPopup_Span_modalTitle(ConfirmPopup instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::modalTitle = value;
  }-*/;

  native static InlineNotification ConfirmPopup_InlineNotification_confirmInlineNotification(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::confirmInlineNotification;
  }-*/;

  native static void ConfirmPopup_InlineNotification_confirmInlineNotification(ConfirmPopup instance, InlineNotification value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::confirmInlineNotification = value;
  }-*/;

  native static Span ConfirmPopup_Span_modalConfirmationMessageLabel(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::modalConfirmationMessageLabel;
  }-*/;

  native static void ConfirmPopup_Span_modalConfirmationMessageLabel(ConfirmPopup instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::modalConfirmationMessageLabel = value;
  }-*/;

  native static Button ConfirmPopup_Button_okButton(ConfirmPopup instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::okButton;
  }-*/;

  native static void ConfirmPopup_Button_okButton(ConfirmPopup instance, Button value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ConfirmPopup::okButton = value;
  }-*/;
}