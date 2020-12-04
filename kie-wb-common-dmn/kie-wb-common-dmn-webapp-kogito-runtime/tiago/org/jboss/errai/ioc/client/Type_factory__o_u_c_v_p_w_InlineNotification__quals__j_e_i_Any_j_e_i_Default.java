package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.views.pfly.widgets.InlineNotification;

public class Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default extends Factory<InlineNotification> { public interface o_u_c_v_p_w_InlineNotificationTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/client/views/pfly/widgets/InlineNotification.html") public TextResource getContents();
  @Source("org/uberfire/client/views/pfly/widgets/InlineNotification.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(InlineNotification.class, "Type_factory__o_u_c_v_p_w_InlineNotification__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { InlineNotification.class, Object.class, IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_c_v_p_w_InlineNotificationTemplateResource) GWT.create(o_u_c_v_p_w_InlineNotificationTemplateResource.class)).getStyle().ensureInjected();
  }

  public InlineNotification createInstance(final ContextManager contextManager) {
    final InlineNotification instance = new InlineNotification();
    setIncompleteInstance(instance);
    final Button InlineNotification_dismiss = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, InlineNotification_dismiss);
    InlineNotification_Button_dismiss(instance, InlineNotification_dismiss);
    final Div InlineNotification_alert = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, InlineNotification_alert);
    InlineNotification_Div_alert(instance, InlineNotification_alert);
    final Span InlineNotification_message = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, InlineNotification_message);
    InlineNotification_Span_message(instance, InlineNotification_message);
    final Document InlineNotification_document = (Document) contextManager.getInstance("Producer_factory__o_j_e_c_c_d_Document__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, InlineNotification_document);
    InlineNotification_Document_document(instance, InlineNotification_document);
    final Span InlineNotification_icon = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, InlineNotification_icon);
    InlineNotification_Span_icon(instance, InlineNotification_icon);
    o_u_c_v_p_w_InlineNotificationTemplateResource templateForInlineNotification = GWT.create(o_u_c_v_p_w_InlineNotificationTemplateResource.class);
    Element parentElementForTemplateOfInlineNotification = TemplateUtil.getRootTemplateParentElement(templateForInlineNotification.getContents().getText(), "org/uberfire/client/views/pfly/widgets/InlineNotification.html", "");
    TemplateUtil.translateTemplate("org/uberfire/client/views/pfly/widgets/InlineNotification.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInlineNotification));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInlineNotification));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("alert", new DataFieldMeta());
    dataFieldMetas.put("message", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    dataFieldMetas.put("dismiss", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.InlineNotification", "org/uberfire/client/views/pfly/widgets/InlineNotification.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Div_alert(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "alert");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.InlineNotification", "org/uberfire/client/views/pfly/widgets/InlineNotification.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Span_message(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "message");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.InlineNotification", "org/uberfire/client/views/pfly/widgets/InlineNotification.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Span_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    TemplateUtil.compositeComponentReplace("org.uberfire.client.views.pfly.widgets.InlineNotification", "org/uberfire/client/views/pfly/widgets/InlineNotification.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Button_dismiss(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "dismiss");
    templateFieldsMap.put("alert", ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Div_alert(instance))));
    templateFieldsMap.put("message", ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Span_message(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Span_icon(instance))));
    templateFieldsMap.put("dismiss", ElementWrapperWidget.getWidget(TemplateUtil.asElement(InlineNotification_Button_dismiss(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfInlineNotification), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((InlineNotification) instance, contextManager);
  }

  public void destroyInstanceHelper(final InlineNotification instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Document InlineNotification_Document_document(InlineNotification instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::document;
  }-*/;

  native static void InlineNotification_Document_document(InlineNotification instance, Document value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::document = value;
  }-*/;

  native static Span InlineNotification_Span_message(InlineNotification instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::message;
  }-*/;

  native static void InlineNotification_Span_message(InlineNotification instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::message = value;
  }-*/;

  native static Span InlineNotification_Span_icon(InlineNotification instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::icon;
  }-*/;

  native static void InlineNotification_Span_icon(InlineNotification instance, Span value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::icon = value;
  }-*/;

  native static Div InlineNotification_Div_alert(InlineNotification instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::alert;
  }-*/;

  native static void InlineNotification_Div_alert(InlineNotification instance, Div value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::alert = value;
  }-*/;

  native static Button InlineNotification_Button_dismiss(InlineNotification instance) /*-{
    return instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::dismiss;
  }-*/;

  native static void InlineNotification_Button_dismiss(InlineNotification instance, Button value) /*-{
    instance.@org.uberfire.client.views.pfly.widgets.InlineNotification::dismiss = value;
  }-*/;
}