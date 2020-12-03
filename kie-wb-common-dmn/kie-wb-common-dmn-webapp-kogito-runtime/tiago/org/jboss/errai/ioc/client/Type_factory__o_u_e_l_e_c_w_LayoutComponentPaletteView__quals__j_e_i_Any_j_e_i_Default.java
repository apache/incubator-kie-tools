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
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteView;

public class Type_factory__o_u_e_l_e_c_w_LayoutComponentPaletteView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutComponentPaletteView> { public interface o_u_e_l_e_c_w_LayoutComponentPaletteViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutComponentPaletteView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutComponentPaletteView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_w_LayoutComponentPaletteView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutComponentPaletteView.class, "Type_factory__o_u_e_l_e_c_w_LayoutComponentPaletteView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutComponentPaletteView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_w_LayoutComponentPaletteViewTemplateResource) GWT.create(o_u_e_l_e_c_w_LayoutComponentPaletteViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public LayoutComponentPaletteView createInstance(final ContextManager contextManager) {
    final LayoutComponentPaletteView instance = new LayoutComponentPaletteView();
    setIncompleteInstance(instance);
    final Div LayoutComponentPaletteView_components = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutComponentPaletteView_components);
    LayoutComponentPaletteView_Div_components(instance, LayoutComponentPaletteView_components);
    o_u_e_l_e_c_w_LayoutComponentPaletteViewTemplateResource templateForLayoutComponentPaletteView = GWT.create(o_u_e_l_e_c_w_LayoutComponentPaletteViewTemplateResource.class);
    Element parentElementForTemplateOfLayoutComponentPaletteView = TemplateUtil.getRootTemplateParentElement(templateForLayoutComponentPaletteView.getContents().getText(), "org/uberfire/ext/layout/editor/client/widgets/LayoutComponentPaletteView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/widgets/LayoutComponentPaletteView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutComponentPaletteView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutComponentPaletteView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("components", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteView", "org/uberfire/ext/layout/editor/client/widgets/LayoutComponentPaletteView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutComponentPaletteView_Div_components(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "components");
    templateFieldsMap.put("components", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutComponentPaletteView_Div_components(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutComponentPaletteView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutComponentPaletteView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutComponentPaletteView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div LayoutComponentPaletteView_Div_components(LayoutComponentPaletteView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteView::components;
  }-*/;

  native static void LayoutComponentPaletteView_Div_components(LayoutComponentPaletteView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteView::components = value;
  }-*/;
}