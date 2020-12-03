package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLLIElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter.View;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView;

public class Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultBreadcrumbsView> { public interface o_u_e_w_c_c_b_w_DefaultBreadcrumbsViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/breadcrumbs/widget/DefaultBreadcrumbsView.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/breadcrumbs/widget/DefaultBreadcrumbsView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultBreadcrumbsView.class, "Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultBreadcrumbsView.class, Object.class, UberElemental.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_b_w_DefaultBreadcrumbsViewTemplateResource) GWT.create(o_u_e_w_c_c_b_w_DefaultBreadcrumbsViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public DefaultBreadcrumbsView createInstance(final ContextManager contextManager) {
    final DefaultBreadcrumbsView instance = new DefaultBreadcrumbsView();
    setIncompleteInstance(instance);
    final HTMLAnchorElement DefaultBreadcrumbsView_breadcrumbLink = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefaultBreadcrumbsView_breadcrumbLink);
    DefaultBreadcrumbsView_HTMLAnchorElement_breadcrumbLink(instance, DefaultBreadcrumbsView_breadcrumbLink);
    final HTMLLIElement DefaultBreadcrumbsView_breadcrumb = (HTMLLIElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLIElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefaultBreadcrumbsView_breadcrumb);
    DefaultBreadcrumbsView_HTMLLIElement_breadcrumb(instance, DefaultBreadcrumbsView_breadcrumb);
    o_u_e_w_c_c_b_w_DefaultBreadcrumbsViewTemplateResource templateForDefaultBreadcrumbsView = GWT.create(o_u_e_w_c_c_b_w_DefaultBreadcrumbsViewTemplateResource.class);
    Element parentElementForTemplateOfDefaultBreadcrumbsView = TemplateUtil.getRootTemplateParentElement(templateForDefaultBreadcrumbsView.getContents().getText(), "org/uberfire/ext/widgets/common/client/breadcrumbs/widget/DefaultBreadcrumbsView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/breadcrumbs/widget/DefaultBreadcrumbsView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultBreadcrumbsView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultBreadcrumbsView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("breadcrumb", new DataFieldMeta());
    dataFieldMetas.put("breadcrumbLink", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView", "org/uberfire/ext/widgets/common/client/breadcrumbs/widget/DefaultBreadcrumbsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefaultBreadcrumbsView_HTMLLIElement_breadcrumb(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "breadcrumb");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView", "org/uberfire/ext/widgets/common/client/breadcrumbs/widget/DefaultBreadcrumbsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefaultBreadcrumbsView_HTMLAnchorElement_breadcrumbLink(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "breadcrumbLink");
    templateFieldsMap.put("breadcrumb", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefaultBreadcrumbsView_HTMLLIElement_breadcrumb(instance))));
    templateFieldsMap.put("breadcrumbLink", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefaultBreadcrumbsView_HTMLAnchorElement_breadcrumbLink(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultBreadcrumbsView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultBreadcrumbsView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultBreadcrumbsView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLLIElement DefaultBreadcrumbsView_HTMLLIElement_breadcrumb(DefaultBreadcrumbsView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView::breadcrumb;
  }-*/;

  native static void DefaultBreadcrumbsView_HTMLLIElement_breadcrumb(DefaultBreadcrumbsView instance, HTMLLIElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView::breadcrumb = value;
  }-*/;

  native static HTMLAnchorElement DefaultBreadcrumbsView_HTMLAnchorElement_breadcrumbLink(DefaultBreadcrumbsView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView::breadcrumbLink;
  }-*/;

  native static void DefaultBreadcrumbsView_HTMLAnchorElement_breadcrumbLink(DefaultBreadcrumbsView instance, HTMLAnchorElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView::breadcrumbLink = value;
  }-*/;
}