package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLButtonElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter.View;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonView__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewHideAlertsButtonView> { public interface o_g_m_c_c_w_b_ViewHideAlertsButtonViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/guvnor/messageconsole/client/console/widget/button/ViewHideAlertsButtonView.html") public TextResource getContents();
  @Source("org/guvnor/messageconsole/client/console/widget/button/ViewHideAlertsButtonView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ViewHideAlertsButtonView.class, "Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ViewHideAlertsButtonView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    ((o_g_m_c_c_w_b_ViewHideAlertsButtonViewTemplateResource) GWT.create(o_g_m_c_c_w_b_ViewHideAlertsButtonViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public ViewHideAlertsButtonView createInstance(final ContextManager contextManager) {
    final ViewHideAlertsButtonView instance = new ViewHideAlertsButtonView();
    setIncompleteInstance(instance);
    final TranslationService ViewHideAlertsButtonView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ViewHideAlertsButtonView_translationService);
    ViewHideAlertsButtonView_TranslationService_translationService(instance, ViewHideAlertsButtonView_translationService);
    final HTMLButtonElement ViewHideAlertsButtonView_viewAlerts = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ViewHideAlertsButtonView_viewAlerts);
    ViewHideAlertsButtonView_HTMLButtonElement_viewAlerts(instance, ViewHideAlertsButtonView_viewAlerts);
    final HTMLButtonElement ViewHideAlertsButtonView_hideAlerts = (HTMLButtonElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLButtonElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ViewHideAlertsButtonView_hideAlerts);
    ViewHideAlertsButtonView_HTMLButtonElement_hideAlerts(instance, ViewHideAlertsButtonView_hideAlerts);
    o_g_m_c_c_w_b_ViewHideAlertsButtonViewTemplateResource templateForViewHideAlertsButtonView = GWT.create(o_g_m_c_c_w_b_ViewHideAlertsButtonViewTemplateResource.class);
    Element parentElementForTemplateOfViewHideAlertsButtonView = TemplateUtil.getRootTemplateParentElement(templateForViewHideAlertsButtonView.getContents().getText(), "org/guvnor/messageconsole/client/console/widget/button/ViewHideAlertsButtonView.html", "");
    TemplateUtil.translateTemplate("org/guvnor/messageconsole/client/console/widget/button/ViewHideAlertsButtonView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfViewHideAlertsButtonView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfViewHideAlertsButtonView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("view-alerts", new DataFieldMeta());
    dataFieldMetas.put("hide-alerts", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView", "org/guvnor/messageconsole/client/console/widget/button/ViewHideAlertsButtonView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ViewHideAlertsButtonView_HTMLButtonElement_viewAlerts(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "view-alerts");
    TemplateUtil.compositeComponentReplace("org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView", "org/guvnor/messageconsole/client/console/widget/button/ViewHideAlertsButtonView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ViewHideAlertsButtonView_HTMLButtonElement_hideAlerts(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "hide-alerts");
    templateFieldsMap.put("view-alerts", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ViewHideAlertsButtonView_HTMLButtonElement_viewAlerts(instance))));
    templateFieldsMap.put("hide-alerts", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ViewHideAlertsButtonView_HTMLButtonElement_hideAlerts(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfViewHideAlertsButtonView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("hide-alerts"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.hideAlerts(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("view-alerts"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.viewAlerts(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ViewHideAlertsButtonView) instance, contextManager);
  }

  public void destroyInstanceHelper(final ViewHideAlertsButtonView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLButtonElement ViewHideAlertsButtonView_HTMLButtonElement_viewAlerts(ViewHideAlertsButtonView instance) /*-{
    return instance.@org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView::viewAlerts;
  }-*/;

  native static void ViewHideAlertsButtonView_HTMLButtonElement_viewAlerts(ViewHideAlertsButtonView instance, HTMLButtonElement value) /*-{
    instance.@org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView::viewAlerts = value;
  }-*/;

  native static HTMLButtonElement ViewHideAlertsButtonView_HTMLButtonElement_hideAlerts(ViewHideAlertsButtonView instance) /*-{
    return instance.@org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView::hideAlerts;
  }-*/;

  native static void ViewHideAlertsButtonView_HTMLButtonElement_hideAlerts(ViewHideAlertsButtonView instance, HTMLButtonElement value) /*-{
    instance.@org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView::hideAlerts = value;
  }-*/;

  native static TranslationService ViewHideAlertsButtonView_TranslationService_translationService(ViewHideAlertsButtonView instance) /*-{
    return instance.@org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView::translationService;
  }-*/;

  native static void ViewHideAlertsButtonView_TranslationService_translationService(ViewHideAlertsButtonView instance, TranslationService value) /*-{
    instance.@org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView::translationService = value;
  }-*/;
}