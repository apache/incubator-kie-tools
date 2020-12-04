package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyItemView;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemPresenter.View;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyInternalItemView> { public interface o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeHierarchyInternalItemView.class, "Type_factory__o_u_e_p_c_c_t_TreeHierarchyInternalItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeHierarchyInternalItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, HierarchyItemView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource) GWT.create(o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public TreeHierarchyInternalItemView createInstance(final ContextManager contextManager) {
    final TreeHierarchyInternalItemView instance = new TreeHierarchyInternalItemView();
    setIncompleteInstance(instance);
    final Div TreeHierarchyInternalItemView_children = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, TreeHierarchyInternalItemView_children);
    TreeHierarchyInternalItemView_Div_children(instance, TreeHierarchyInternalItemView_children);
    final Div TreeHierarchyInternalItemView_treeNode = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, TreeHierarchyInternalItemView_treeNode);
    TreeHierarchyInternalItemView_Div_treeNode(instance, TreeHierarchyInternalItemView_treeNode);
    final TranslationService TreeHierarchyInternalItemView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, TreeHierarchyInternalItemView_translationService);
    TreeHierarchyInternalItemView_TranslationService_translationService(instance, TreeHierarchyInternalItemView_translationService);
    o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource templateForTreeHierarchyInternalItemView = GWT.create(o_u_e_p_c_c_t_TreeHierarchyInternalItemViewTemplateResource.class);
    Element parentElementForTemplateOfTreeHierarchyInternalItemView = TemplateUtil.getRootTemplateParentElement(templateForTreeHierarchyInternalItemView.getContents().getText(), "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyInternalItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyInternalItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("preference-tree-internal-item-label", new DataFieldMeta());
    dataFieldMetas.put("preference-tree-internal-item-node", new DataFieldMeta());
    dataFieldMetas.put("preference-tree-internal-item-children", new DataFieldMeta());
    dataFieldMetas.put("preference-tree-internal-item-expand-icon", new DataFieldMeta());
    dataFieldMetas.put("preference-tree-internal-item-contract-icon", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_label(instance));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-internal-item-label");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyInternalItemView_Div_treeNode(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-internal-item-node");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyInternalItemView_Div_children(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-internal-item-children");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_expandIcon(instance));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-internal-item-expand-icon");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyInternalItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_contractIcon(instance));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-internal-item-contract-icon");
    templateFieldsMap.put("preference-tree-internal-item-label", ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_label(instance)));
    templateFieldsMap.put("preference-tree-internal-item-node", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyInternalItemView_Div_treeNode(instance))));
    templateFieldsMap.put("preference-tree-internal-item-children", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyInternalItemView_Div_children(instance))));
    templateFieldsMap.put("preference-tree-internal-item-expand-icon", ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_expandIcon(instance)));
    templateFieldsMap.put("preference-tree-internal-item-contract-icon", ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_contractIcon(instance)));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyInternalItemView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-tree-internal-item-expand-icon"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.expand(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-tree-internal-item-label"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.select(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-tree-internal-item-contract-icon"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.contract(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-tree-internal-item-label"), new DoubleClickHandler() {
      public void onDoubleClick(DoubleClickEvent event) {
        instance.contractExpand(event);
      }
    }, DoubleClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeHierarchyInternalItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeHierarchyInternalItemView instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_label(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_expandIcon(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(TreeHierarchyInternalItemView_Element_contractIcon(instance)));
    TemplateUtil.cleanupTemplated(instance);
  }

  native static TranslationService TreeHierarchyInternalItemView_TranslationService_translationService(TreeHierarchyInternalItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::translationService;
  }-*/;

  native static void TreeHierarchyInternalItemView_TranslationService_translationService(TreeHierarchyInternalItemView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::translationService = value;
  }-*/;

  native static Div TreeHierarchyInternalItemView_Div_children(TreeHierarchyInternalItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::children;
  }-*/;

  native static void TreeHierarchyInternalItemView_Div_children(TreeHierarchyInternalItemView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::children = value;
  }-*/;

  native static Element TreeHierarchyInternalItemView_Element_contractIcon(TreeHierarchyInternalItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::contractIcon;
  }-*/;

  native static void TreeHierarchyInternalItemView_Element_contractIcon(TreeHierarchyInternalItemView instance, Element value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::contractIcon = value;
  }-*/;

  native static Div TreeHierarchyInternalItemView_Div_treeNode(TreeHierarchyInternalItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::treeNode;
  }-*/;

  native static void TreeHierarchyInternalItemView_Div_treeNode(TreeHierarchyInternalItemView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::treeNode = value;
  }-*/;

  native static Element TreeHierarchyInternalItemView_Element_label(TreeHierarchyInternalItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::label;
  }-*/;

  native static void TreeHierarchyInternalItemView_Element_label(TreeHierarchyInternalItemView instance, Element value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::label = value;
  }-*/;

  native static Element TreeHierarchyInternalItemView_Element_expandIcon(TreeHierarchyInternalItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::expandIcon;
  }-*/;

  native static void TreeHierarchyInternalItemView_Element_expandIcon(TreeHierarchyInternalItemView instance, Element value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyInternalItemView::expandIcon = value;
  }-*/;
}