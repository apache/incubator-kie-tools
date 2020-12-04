package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
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
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter.View;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentWidget;

public class Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupView__quals__j_e_i_Any_j_e_i_Default extends Factory<LayoutDragComponentGroupView> { public interface o_u_e_l_e_c_w_LayoutDragComponentGroupViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.html") public TextResource getContents();
  @Source("org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LayoutDragComponentGroupView.class, "Type_factory__o_u_e_l_e_c_w_LayoutDragComponentGroupView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LayoutDragComponentGroupView.class, Object.class, UberElement.class, IsElement.class, HasPresenter.class, View.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_l_e_c_w_LayoutDragComponentGroupViewTemplateResource) GWT.create(o_u_e_l_e_c_w_LayoutDragComponentGroupViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public LayoutDragComponentGroupView createInstance(final ContextManager contextManager) {
    final LayoutDragComponentGroupView instance = new LayoutDragComponentGroupView();
    setIncompleteInstance(instance);
    final Div LayoutDragComponentGroupView_components = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutDragComponentGroupView_components);
    LayoutDragComponentGroupView_Div_components(instance, LayoutDragComponentGroupView_components);
    final Anchor LayoutDragComponentGroupView_groupTitle = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutDragComponentGroupView_groupTitle);
    LayoutDragComponentGroupView_Anchor_groupTitle(instance, LayoutDragComponentGroupView_groupTitle);
    final Div LayoutDragComponentGroupView_collapse = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LayoutDragComponentGroupView_collapse);
    LayoutDragComponentGroupView_Div_collapse(instance, LayoutDragComponentGroupView_collapse);
    final ManagedInstance LayoutDragComponentGroupView_layoutDragComponentWidgets = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LayoutDragComponentWidget.class }, new Annotation[] { });
    registerDependentScopedReference(instance, LayoutDragComponentGroupView_layoutDragComponentWidgets);
    LayoutDragComponentGroupView_ManagedInstance_layoutDragComponentWidgets(instance, LayoutDragComponentGroupView_layoutDragComponentWidgets);
    o_u_e_l_e_c_w_LayoutDragComponentGroupViewTemplateResource templateForLayoutDragComponentGroupView = GWT.create(o_u_e_l_e_c_w_LayoutDragComponentGroupViewTemplateResource.class);
    Element parentElementForTemplateOfLayoutDragComponentGroupView = TemplateUtil.getRootTemplateParentElement(templateForLayoutDragComponentGroupView.getContents().getText(), "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutDragComponentGroupView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutDragComponentGroupView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("group-title", new DataFieldMeta());
    dataFieldMetas.put("components", new DataFieldMeta());
    dataFieldMetas.put("collapse", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView", "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentGroupView_Anchor_groupTitle(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "group-title");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView", "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentGroupView_Div_components(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "components");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView", "org/uberfire/ext/layout/editor/client/widgets/LayoutDragComponentGroupView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentGroupView_Div_collapse(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "collapse");
    templateFieldsMap.put("group-title", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentGroupView_Anchor_groupTitle(instance))));
    templateFieldsMap.put("components", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentGroupView_Div_components(instance))));
    templateFieldsMap.put("collapse", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LayoutDragComponentGroupView_Div_collapse(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLayoutDragComponentGroupView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LayoutDragComponentGroupView) instance, contextManager);
  }

  public void destroyInstanceHelper(final LayoutDragComponentGroupView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div LayoutDragComponentGroupView_Div_components(LayoutDragComponentGroupView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::components;
  }-*/;

  native static void LayoutDragComponentGroupView_Div_components(LayoutDragComponentGroupView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::components = value;
  }-*/;

  native static Div LayoutDragComponentGroupView_Div_collapse(LayoutDragComponentGroupView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::collapse;
  }-*/;

  native static void LayoutDragComponentGroupView_Div_collapse(LayoutDragComponentGroupView instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::collapse = value;
  }-*/;

  native static ManagedInstance LayoutDragComponentGroupView_ManagedInstance_layoutDragComponentWidgets(LayoutDragComponentGroupView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::layoutDragComponentWidgets;
  }-*/;

  native static void LayoutDragComponentGroupView_ManagedInstance_layoutDragComponentWidgets(LayoutDragComponentGroupView instance, ManagedInstance<LayoutDragComponentWidget> value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::layoutDragComponentWidgets = value;
  }-*/;

  native static Anchor LayoutDragComponentGroupView_Anchor_groupTitle(LayoutDragComponentGroupView instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::groupTitle;
  }-*/;

  native static void LayoutDragComponentGroupView_Anchor_groupTitle(LayoutDragComponentGroupView instance, Anchor value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupView::groupTitle = value;
  }-*/;
}