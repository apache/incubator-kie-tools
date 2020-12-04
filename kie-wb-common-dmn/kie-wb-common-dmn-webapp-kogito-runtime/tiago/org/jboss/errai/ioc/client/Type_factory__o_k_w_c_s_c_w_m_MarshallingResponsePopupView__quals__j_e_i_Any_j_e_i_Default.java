package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTextAreaElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup.Row;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup.View;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.util.Clipboard;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.ext.widgets.table.client.UberfirePagedTable;

public class Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<MarshallingResponsePopupView> { public interface o_k_w_c_s_c_w_m_MarshallingResponsePopupViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopupView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MarshallingResponsePopupView.class, "Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopupView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MarshallingResponsePopupView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public MarshallingResponsePopupView createInstance(final ContextManager contextManager) {
    final MarshallingResponsePopupView instance = new MarshallingResponsePopupView();
    setIncompleteInstance(instance);
    final HTMLTextAreaElement MarshallingResponsePopupView_clipboardElement = (HTMLTextAreaElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLTextAreaElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_clipboardElement);
    MarshallingResponsePopupView_HTMLTextAreaElement_clipboardElement(instance, MarshallingResponsePopupView_clipboardElement);
    final Modal MarshallingResponsePopupView_modal = (Modal) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_modal);
    MarshallingResponsePopupView_Modal_modal(instance, MarshallingResponsePopupView_modal);
    final Button MarshallingResponsePopupView_cancelButton = (Button) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_cancelButton);
    MarshallingResponsePopupView_Button_cancelButton(instance, MarshallingResponsePopupView_cancelButton);
    final ClientTranslationService MarshallingResponsePopupView_translationService = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    MarshallingResponsePopupView_ClientTranslationService_translationService(instance, MarshallingResponsePopupView_translationService);
    final Button MarshallingResponsePopupView_okButton = (Button) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_okButton);
    MarshallingResponsePopupView_Button_okButton(instance, MarshallingResponsePopupView_okButton);
    final Button MarshallingResponsePopupView_copyToClipboardButton = (Button) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_copyToClipboardButton);
    MarshallingResponsePopupView_Button_copyToClipboardButton(instance, MarshallingResponsePopupView_copyToClipboardButton);
    final HTMLElement MarshallingResponsePopupView_popupTitle = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    registerDependentScopedReference(instance, MarshallingResponsePopupView_popupTitle);
    MarshallingResponsePopupView_HTMLElement_popupTitle(instance, MarshallingResponsePopupView_popupTitle);
    final Clipboard MarshallingResponsePopupView_clipboard = (Clipboard) contextManager.getInstance("Type_factory__o_u_c_u_Clipboard__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_clipboard);
    MarshallingResponsePopupView_Clipboard_clipboard(instance, MarshallingResponsePopupView_clipboard);
    final InlineNotification MarshallingResponsePopupView_popupInlineNotification = (InlineNotification) contextManager.getInstance("Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, MarshallingResponsePopupView_popupInlineNotification);
    MarshallingResponsePopupView_InlineNotification_popupInlineNotification(instance, MarshallingResponsePopupView_popupInlineNotification);
    o_k_w_c_s_c_w_m_MarshallingResponsePopupViewTemplateResource templateForMarshallingResponsePopupView = GWT.create(o_k_w_c_s_c_w_m_MarshallingResponsePopupViewTemplateResource.class);
    Element parentElementForTemplateOfMarshallingResponsePopupView = TemplateUtil.getRootTemplateParentElement(templateForMarshallingResponsePopupView.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMarshallingResponsePopupView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMarshallingResponsePopupView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(7);
    dataFieldMetas.put("modal", new DataFieldMeta());
    dataFieldMetas.put("popup-title", new DataFieldMeta());
    dataFieldMetas.put("popup-inline-notification", new DataFieldMeta());
    dataFieldMetas.put("popup-messages-table", new DataFieldMeta());
    dataFieldMetas.put("cancel-button", new DataFieldMeta());
    dataFieldMetas.put("ok-button", new DataFieldMeta());
    dataFieldMetas.put("clipboard-element", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(MarshallingResponsePopupView_Modal_modal(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "modal");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MarshallingResponsePopupView_HTMLElement_popupTitle(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "popup-title");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return TemplateWidgetMapper.get(MarshallingResponsePopupView_InlineNotification_popupInlineNotification(instance));
      }
    }, dataFieldElements, dataFieldMetas, "popup-inline-notification");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return MarshallingResponsePopupView_UberfirePagedTable_messagesTable(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "popup-messages-table");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(MarshallingResponsePopupView_Button_cancelButton(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "cancel-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(MarshallingResponsePopupView_Button_okButton(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "ok-button");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView", "org/kie/workbench/common/stunner/client/widgets/marshaller/MarshallingResponsePopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(MarshallingResponsePopupView_HTMLTextAreaElement_clipboardElement(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "clipboard-element");
    templateFieldsMap.put("modal", ElementWrapperWidget.getWidget(MarshallingResponsePopupView_Modal_modal(instance).getElement()));
    templateFieldsMap.put("popup-title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MarshallingResponsePopupView_HTMLElement_popupTitle(instance))));
    templateFieldsMap.put("popup-inline-notification", TemplateWidgetMapper.get(MarshallingResponsePopupView_InlineNotification_popupInlineNotification(instance)));
    templateFieldsMap.put("popup-messages-table", MarshallingResponsePopupView_UberfirePagedTable_messagesTable(instance).asWidget());
    templateFieldsMap.put("cancel-button", ElementWrapperWidget.getWidget(MarshallingResponsePopupView_Button_cancelButton(instance).getElement(), null));
    templateFieldsMap.put("ok-button", ElementWrapperWidget.getWidget(MarshallingResponsePopupView_Button_okButton(instance).getElement(), null));
    templateFieldsMap.put("clipboard-element", ElementWrapperWidget.getWidget(TemplateUtil.asElement(MarshallingResponsePopupView_HTMLTextAreaElement_clipboardElement(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfMarshallingResponsePopupView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((MarshallingResponsePopupView) instance, contextManager);
  }

  public void destroyInstanceHelper(final MarshallingResponsePopupView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final MarshallingResponsePopupView instance) {
    MarshallingResponsePopupView_init(instance);
  }

  native static UberfirePagedTable MarshallingResponsePopupView_UberfirePagedTable_messagesTable(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::messagesTable;
  }-*/;

  native static void MarshallingResponsePopupView_UberfirePagedTable_messagesTable(MarshallingResponsePopupView instance, UberfirePagedTable<Row> value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::messagesTable = value;
  }-*/;

  native static Button MarshallingResponsePopupView_Button_copyToClipboardButton(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::copyToClipboardButton;
  }-*/;

  native static void MarshallingResponsePopupView_Button_copyToClipboardButton(MarshallingResponsePopupView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::copyToClipboardButton = value;
  }-*/;

  native static HTMLTextAreaElement MarshallingResponsePopupView_HTMLTextAreaElement_clipboardElement(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::clipboardElement;
  }-*/;

  native static void MarshallingResponsePopupView_HTMLTextAreaElement_clipboardElement(MarshallingResponsePopupView instance, HTMLTextAreaElement value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::clipboardElement = value;
  }-*/;

  native static Button MarshallingResponsePopupView_Button_cancelButton(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::cancelButton;
  }-*/;

  native static void MarshallingResponsePopupView_Button_cancelButton(MarshallingResponsePopupView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::cancelButton = value;
  }-*/;

  native static ClientTranslationService MarshallingResponsePopupView_ClientTranslationService_translationService(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::translationService;
  }-*/;

  native static void MarshallingResponsePopupView_ClientTranslationService_translationService(MarshallingResponsePopupView instance, ClientTranslationService value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::translationService = value;
  }-*/;

  native static Clipboard MarshallingResponsePopupView_Clipboard_clipboard(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::clipboard;
  }-*/;

  native static void MarshallingResponsePopupView_Clipboard_clipboard(MarshallingResponsePopupView instance, Clipboard value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::clipboard = value;
  }-*/;

  native static Modal MarshallingResponsePopupView_Modal_modal(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::modal;
  }-*/;

  native static void MarshallingResponsePopupView_Modal_modal(MarshallingResponsePopupView instance, Modal value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::modal = value;
  }-*/;

  native static HTMLElement MarshallingResponsePopupView_HTMLElement_popupTitle(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::popupTitle;
  }-*/;

  native static void MarshallingResponsePopupView_HTMLElement_popupTitle(MarshallingResponsePopupView instance, HTMLElement value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::popupTitle = value;
  }-*/;

  native static InlineNotification MarshallingResponsePopupView_InlineNotification_popupInlineNotification(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::popupInlineNotification;
  }-*/;

  native static void MarshallingResponsePopupView_InlineNotification_popupInlineNotification(MarshallingResponsePopupView instance, InlineNotification value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::popupInlineNotification = value;
  }-*/;

  native static Button MarshallingResponsePopupView_Button_okButton(MarshallingResponsePopupView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::okButton;
  }-*/;

  native static void MarshallingResponsePopupView_Button_okButton(MarshallingResponsePopupView instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::okButton = value;
  }-*/;

  public native static void MarshallingResponsePopupView_init(MarshallingResponsePopupView instance) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView::init()();
  }-*/;
}