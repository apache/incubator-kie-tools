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
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenter.View;
import org.uberfire.ext.preferences.client.admin.AdminPageView;

public class Type_factory__o_u_e_p_c_a_AdminPageView__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageView> { public interface o_u_e_p_c_a_AdminPageViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/admin/AdminPageView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/admin/AdminPageView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_a_AdminPageView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPageView.class, "Type_factory__o_u_e_p_c_a_AdminPageView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPageView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_a_AdminPageViewTemplateResource) GWT.create(o_u_e_p_c_a_AdminPageViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public AdminPageView createInstance(final ContextManager contextManager) {
    final AdminPageView instance = new AdminPageView();
    setIncompleteInstance(instance);
    final Div AdminPageView_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageView_content);
    AdminPageView_Div_content(instance, AdminPageView_content);
    final TranslationService AdminPageView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AdminPageView_translationService);
    AdminPageView_TranslationService_translationService(instance, AdminPageView_translationService);
    final Heading AdminPageView_title = (Heading) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Heading__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AdminPageView_title);
    AdminPageView_Heading_title(instance, AdminPageView_title);
    o_u_e_p_c_a_AdminPageViewTemplateResource templateForAdminPageView = GWT.create(o_u_e_p_c_a_AdminPageViewTemplateResource.class);
    Element parentElementForTemplateOfAdminPageView = TemplateUtil.getRootTemplateParentElement(templateForAdminPageView.getContents().getText(), "org/uberfire/ext/preferences/client/admin/AdminPageView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/admin/AdminPageView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("admin-page-content", new DataFieldMeta());
    dataFieldMetas.put("admin-page-title", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.AdminPageView", "org/uberfire/ext/preferences/client/admin/AdminPageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageView_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "admin-page-content");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.admin.AdminPageView", "org/uberfire/ext/preferences/client/admin/AdminPageView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageView_Heading_title(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "admin-page-title");
    templateFieldsMap.put("admin-page-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageView_Div_content(instance))));
    templateFieldsMap.put("admin-page-title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AdminPageView_Heading_title(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAdminPageView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AdminPageView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AdminPageView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static TranslationService AdminPageView_TranslationService_translationService(AdminPageView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPageView::translationService;
  }-*/;

  native static void AdminPageView_TranslationService_translationService(AdminPageView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPageView::translationService = value;
  }-*/;

  native static Heading AdminPageView_Heading_title(AdminPageView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPageView::title;
  }-*/;

  native static void AdminPageView_Heading_title(AdminPageView instance, Heading value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPageView::title = value;
  }-*/;

  native static Div AdminPageView_Div_content(AdminPageView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.admin.AdminPageView::content;
  }-*/;

  native static void AdminPageView_Div_content(AdminPageView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.admin.AdminPageView::content = value;
  }-*/;
}