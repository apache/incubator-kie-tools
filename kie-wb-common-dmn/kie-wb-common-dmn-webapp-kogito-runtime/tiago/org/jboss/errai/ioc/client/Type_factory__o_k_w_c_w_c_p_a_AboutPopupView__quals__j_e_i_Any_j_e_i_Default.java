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
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Image;
import org.jboss.errai.common.client.dom.Span;
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
import org.kie.workbench.common.widgets.client.popups.about.AboutPopup.View;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopupView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.Modal;

public class Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default extends Factory<AboutPopupView> { public interface o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html") public TextResource getContents();
  @Source("org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AboutPopupView.class, "Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AboutPopupView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource) GWT.create(o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public AboutPopupView createInstance(final ContextManager contextManager) {
    final AboutPopupView instance = new AboutPopupView();
    setIncompleteInstance(instance);
    final Div AboutPopupView_modalContent = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AboutPopupView_modalContent);
    AboutPopupView_Div_modalContent(instance, AboutPopupView_modalContent);
    final Span AboutPopupView_trademarkProductName = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AboutPopupView_trademarkProductName);
    AboutPopupView_Span_trademarkProductName(instance, AboutPopupView_trademarkProductName);
    final Modal AboutPopupView_modal = (Modal) contextManager.getInstance("Type_factory__o_u_c_v_p_w_Modal__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AboutPopupView_modal);
    AboutPopupView_Modal_modal(instance, AboutPopupView_modal);
    final TranslationService AboutPopupView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, AboutPopupView_translationService);
    AboutPopupView_TranslationService_translationService(instance, AboutPopupView_translationService);
    final Span AboutPopupView_version = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AboutPopupView_version);
    AboutPopupView_Span_version(instance, AboutPopupView_version);
    final Span AboutPopupView_trademark2 = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AboutPopupView_trademark2);
    AboutPopupView_Span_trademark2(instance, AboutPopupView_trademark2);
    final Image AboutPopupView_productImage = (Image) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Image__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, AboutPopupView_productImage);
    AboutPopupView_Image_productImage(instance, AboutPopupView_productImage);
    o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource templateForAboutPopupView = GWT.create(o_k_w_c_w_c_p_a_AboutPopupViewTemplateResource.class);
    Element parentElementForTemplateOfAboutPopupView = TemplateUtil.getRootTemplateParentElement(templateForAboutPopupView.getContents().getText(), "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAboutPopupView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAboutPopupView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("about", new DataFieldMeta());
    dataFieldMetas.put("modal-content", new DataFieldMeta());
    dataFieldMetas.put("product-image", new DataFieldMeta());
    dataFieldMetas.put("version", new DataFieldMeta());
    dataFieldMetas.put("trademark-product-name", new DataFieldMeta());
    dataFieldMetas.put("trademark2", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.about.AboutPopupView", "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(AboutPopupView_Modal_modal(instance).getElement());
      }
    }, dataFieldElements, dataFieldMetas, "about");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.about.AboutPopupView", "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Div_modalContent(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "modal-content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.about.AboutPopupView", "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Image_productImage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "product-image");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.about.AboutPopupView", "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Span_version(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "version");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.about.AboutPopupView", "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Span_trademarkProductName(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "trademark-product-name");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.about.AboutPopupView", "org/kie/workbench/common/widgets/client/popups/about/AboutPopupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Span_trademark2(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "trademark2");
    templateFieldsMap.put("about", ElementWrapperWidget.getWidget(AboutPopupView_Modal_modal(instance).getElement()));
    templateFieldsMap.put("modal-content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Div_modalContent(instance))));
    templateFieldsMap.put("product-image", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Image_productImage(instance))));
    templateFieldsMap.put("version", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Span_version(instance))));
    templateFieldsMap.put("trademark-product-name", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Span_trademarkProductName(instance))));
    templateFieldsMap.put("trademark2", ElementWrapperWidget.getWidget(TemplateUtil.asElement(AboutPopupView_Span_trademark2(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfAboutPopupView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((AboutPopupView) instance, contextManager);
  }

  public void destroyInstanceHelper(final AboutPopupView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Span AboutPopupView_Span_version(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::version;
  }-*/;

  native static void AboutPopupView_Span_version(AboutPopupView instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::version = value;
  }-*/;

  native static Span AboutPopupView_Span_trademarkProductName(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::trademarkProductName;
  }-*/;

  native static void AboutPopupView_Span_trademarkProductName(AboutPopupView instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::trademarkProductName = value;
  }-*/;

  native static Image AboutPopupView_Image_productImage(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::productImage;
  }-*/;

  native static void AboutPopupView_Image_productImage(AboutPopupView instance, Image value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::productImage = value;
  }-*/;

  native static TranslationService AboutPopupView_TranslationService_translationService(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::translationService;
  }-*/;

  native static void AboutPopupView_TranslationService_translationService(AboutPopupView instance, TranslationService value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::translationService = value;
  }-*/;

  native static Div AboutPopupView_Div_modalContent(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::modalContent;
  }-*/;

  native static void AboutPopupView_Div_modalContent(AboutPopupView instance, Div value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::modalContent = value;
  }-*/;

  native static Modal AboutPopupView_Modal_modal(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::modal;
  }-*/;

  native static void AboutPopupView_Modal_modal(AboutPopupView instance, Modal value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::modal = value;
  }-*/;

  native static Span AboutPopupView_Span_trademark2(AboutPopupView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::trademark2;
  }-*/;

  native static void AboutPopupView_Span_trademark2(AboutPopupView instance, Span value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.about.AboutPopupView::trademark2 = value;
  }-*/;
}