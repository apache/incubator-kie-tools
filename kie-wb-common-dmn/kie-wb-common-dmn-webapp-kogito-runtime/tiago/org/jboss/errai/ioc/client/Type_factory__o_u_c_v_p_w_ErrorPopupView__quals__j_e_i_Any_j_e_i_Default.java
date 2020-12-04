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
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.ErrorPopup.View;
import org.uberfire.client.views.pfly.widgets.ErrorPopupView;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;

public class Type_factory__o_u_c_v_p_w_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<ErrorPopupView> { public interface o_u_c_v_p_w_ErrorPopupViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/ErrorPopupView.html") public TextResource getContents(); }
  public Type_factory__o_u_c_v_p_w_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ErrorPopupView.class, "Type_factory__o_u_c_v_p_w_ErrorPopupView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ErrorPopupView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public ErrorPopupView createInstance(final ContextManager contextManager) {
    final ErrorPopupView instance = new ErrorPopupView();
    setIncompleteInstance(instance);
    final TextArea ErrorPopupView_detailArea = (TextArea) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_TextArea__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_detailArea);
    ErrorPopupView_TextArea_detailArea(instance, ErrorPopupView_detailArea);
    final Div ErrorPopupView_detailAreaContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_detailAreaContainer);
    ErrorPopupView_Div_detailAreaContainer(instance, ErrorPopupView_detailAreaContainer);
    final TranslationService ErrorPopupView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ErrorPopupView_translationService);
    ErrorPopupView_TranslationService_translationService(instance, ErrorPopupView_translationService);
    final Span ErrorPopupView_detailAnchorIcon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_detailAnchorIcon);
    ErrorPopupView_Span_detailAnchorIcon(instance, ErrorPopupView_detailAnchorIcon);
    final Anchor ErrorPopupView_detailAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_detailAnchor);
    ErrorPopupView_Anchor_detailAnchor(instance, ErrorPopupView_detailAnchor);
    final Span ErrorPopupView_standardNotification = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_standardNotification);
    ErrorPopupView_Span_standardNotification(instance, ErrorPopupView_standardNotification);
    final Modal ErrorPopupView_modal = (Modal) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ErrorPopupView_modal);
    ErrorPopupView_Modal_modal(instance, ErrorPopupView_modal);
    final Div ErrorPopupView_messageContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_messageContainer);
    ErrorPopupView_Div_messageContainer(instance, ErrorPopupView_messageContainer);
    final InlineNotification ErrorPopupView_inlineNotification = (InlineNotification) contextManager.getInstance("Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ErrorPopupView_inlineNotification);
    ErrorPopupView_InlineNotification_inlineNotification(instance, ErrorPopupView_inlineNotification);
    final Div ErrorPopupView_detailContainer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ErrorPopupView_detailContainer);
    ErrorPopupView_Div_detailContainer(instance, ErrorPopupView_detailContainer);
    o_u_c_v_p_w_ErrorPopupViewTemplateResource templateForErrorPopupView = GWT.create(o_u_c_v_p_w_ErrorPopupViewTemplateResource.class);
    Element parentElementForTemplateOfErrorPopupView = TemplateUtil.getRootTemplateParentElement(templateForErrorPopupView.getContents().getText(), "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfErrorPopupView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfErrorPopupView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(9);
    dataFieldMetas.put("message-container", new DataFieldMeta());
    dataFieldMetas.put("inline-notification", new DataFieldMeta());
    dataFieldMetas.put("standard-notification", new DataFieldMeta());
    dataFieldMetas.put("detail-container", new DataFieldMeta());
    dataFieldMetas.put("detail-area", new DataFieldMeta());
    dataFieldMetas.put("detail-area-container", new DataFieldMeta());
    dataFieldMetas.put("detail-anchor-icon", new DataFieldMeta());
    dataFieldMetas.put("detail-anchor", new DataFieldMeta());
    dataFieldMetas.put("modal", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Div_messageContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "message-container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return TemplateWidgetMapper.get(ErrorPopupView_InlineNotification_inlineNotification(instance));
      }
    }, dataFieldElements, dataFieldMetas, "inline-notification");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Span_standardNotification(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "standard-notification");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Div_detailContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "detail-container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_TextArea_detailArea(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "detail-area");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Div_detailAreaContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "detail-area-container");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Span_detailAnchorIcon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "detail-anchor-icon");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Anchor_detailAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "detail-anchor");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.ErrorPopupView", "org/uberfire/client/views/pfly/widgets/ErrorPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(ErrorPopupView_Modal_modal(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "modal");
    templateFieldsMap.put("message-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Div_messageContainer(instance))));
    templateFieldsMap.put("inline-notification", TemplateWidgetMapper.get(ErrorPopupView_InlineNotification_inlineNotification(instance)));
    templateFieldsMap.put("standard-notification", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Span_standardNotification(instance))));
    templateFieldsMap.put("detail-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Div_detailContainer(instance))));
    templateFieldsMap.put("detail-area", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_TextArea_detailArea(instance))));
    templateFieldsMap.put("detail-area-container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Div_detailAreaContainer(instance))));
    templateFieldsMap.put("detail-anchor-icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Span_detailAnchorIcon(instance))));
    templateFieldsMap.put("detail-anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ErrorPopupView_Anchor_detailAnchor(instance))));
    templateFieldsMap.put("modal", ElementWrapperWidget.getWidget(ErrorPopupView_Modal_modal(instance).getElement()));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfErrorPopupView), templateFieldsMap.values());
    final EventListener listenerForMouseEventCallingOnCloseClick = new EventListener() {
      public void call(Event event) {
        ErrorPopupView_onCloseClick_MouseEvent(instance, Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, dataFieldElements.get("close-button"), listenerForMouseEventCallingOnCloseClick, "click");
    final EventListener listenerForMouseEventCallingOnDetailClick = new EventListener() {
      public void call(Event event) {
        ErrorPopupView_onDetailClick_MouseEvent(instance, Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, templateFieldsMap.get("detail-anchor"), listenerForMouseEventCallingOnDetailClick, "click");
    final EventListener listenerForMouseEventCallingOnOkClick = new EventListener() {
      public void call(Event event) {
        ErrorPopupView_onOkClick_MouseEvent(instance, Js.cast(event));
      }
    };
    TemplateUtil.setupBrowserEventListener(instance, dataFieldElements.get("ok-button"), listenerForMouseEventCallingOnOkClick, "click");
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ErrorPopupView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ErrorPopupView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final ErrorPopupView instance) {
    instance.init();
  }

  native static Div ErrorPopupView_Div_detailAreaContainer(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailAreaContainer;
  }-*/;

  native static void ErrorPopupView_Div_detailAreaContainer(ErrorPopupView instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailAreaContainer = value;
  }-*/;

  native static Anchor ErrorPopupView_Anchor_detailAnchor(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailAnchor;
  }-*/;

  native static void ErrorPopupView_Anchor_detailAnchor(ErrorPopupView instance, Anchor value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailAnchor = value;
  }-*/;

  native static Div ErrorPopupView_Div_detailContainer(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailContainer;
  }-*/;

  native static void ErrorPopupView_Div_detailContainer(ErrorPopupView instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailContainer = value;
  }-*/;

  native static Span ErrorPopupView_Span_detailAnchorIcon(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailAnchorIcon;
  }-*/;

  native static void ErrorPopupView_Span_detailAnchorIcon(ErrorPopupView instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailAnchorIcon = value;
  }-*/;

  native static Div ErrorPopupView_Div_messageContainer(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::messageContainer;
  }-*/;

  native static void ErrorPopupView_Div_messageContainer(ErrorPopupView instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::messageContainer = value;
  }-*/;

  native static InlineNotification ErrorPopupView_InlineNotification_inlineNotification(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::inlineNotification;
  }-*/;

  native static void ErrorPopupView_InlineNotification_inlineNotification(ErrorPopupView instance, InlineNotification value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::inlineNotification = value;
  }-*/;

  native static TextArea ErrorPopupView_TextArea_detailArea(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailArea;
  }-*/;

  native static void ErrorPopupView_TextArea_detailArea(ErrorPopupView instance, TextArea value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::detailArea = value;
  }-*/;

  native static Modal ErrorPopupView_Modal_modal(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::modal;
  }-*/;

  native static void ErrorPopupView_Modal_modal(ErrorPopupView instance, Modal value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::modal = value;
  }-*/;

  native static Span ErrorPopupView_Span_standardNotification(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::standardNotification;
  }-*/;

  native static void ErrorPopupView_Span_standardNotification(ErrorPopupView instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::standardNotification = value;
  }-*/;

  native static TranslationService ErrorPopupView_TranslationService_translationService(ErrorPopupView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::translationService;
  }-*/;

  native static void ErrorPopupView_TranslationService_translationService(ErrorPopupView instance, TranslationService value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::translationService = value;
  }-*/;

  public native static void ErrorPopupView_onDetailClick_MouseEvent(ErrorPopupView instance, MouseEvent a0) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::onDetailClick(Lorg/jboss/errai/common/client/dom/MouseEvent;)(a0);
  }-*/;

  public native static void ErrorPopupView_onCloseClick_MouseEvent(ErrorPopupView instance, MouseEvent a0) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::onCloseClick(Lorg/jboss/errai/common/client/dom/MouseEvent;)(a0);
  }-*/;

  public native static void ErrorPopupView_onOkClick_MouseEvent(ErrorPopupView instance, MouseEvent a0) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.ErrorPopupView::onOkClick(Lorg/jboss/errai/common/client/dom/MouseEvent;)(a0);
  }-*/;
}