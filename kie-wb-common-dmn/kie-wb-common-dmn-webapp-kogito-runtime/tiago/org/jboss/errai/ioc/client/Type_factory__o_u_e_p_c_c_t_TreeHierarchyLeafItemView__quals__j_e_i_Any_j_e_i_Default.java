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
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemPresenter.View;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyLeafItemView> { public interface o_u_e_p_c_c_t_TreeHierarchyLeafItemViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyLeafItemView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyLeafItemView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeHierarchyLeafItemView.class, "Type_factory__o_u_e_p_c_c_t_TreeHierarchyLeafItemView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeHierarchyLeafItemView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, HierarchyItemView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_c_t_TreeHierarchyLeafItemViewTemplateResource) GWT.create(o_u_e_p_c_c_t_TreeHierarchyLeafItemViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public TreeHierarchyLeafItemView createInstance(final ContextManager contextManager) {
    final TreeHierarchyLeafItemView instance = new TreeHierarchyLeafItemView();
    setIncompleteInstance(instance);
    final Div TreeHierarchyLeafItemView_treeNode = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, TreeHierarchyLeafItemView_treeNode);
    TreeHierarchyLeafItemView_Div_treeNode(instance, TreeHierarchyLeafItemView_treeNode);
    final TranslationService TreeHierarchyLeafItemView_translationService = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, TreeHierarchyLeafItemView_translationService);
    TreeHierarchyLeafItemView_TranslationService_translationService(instance, TreeHierarchyLeafItemView_translationService);
    o_u_e_p_c_c_t_TreeHierarchyLeafItemViewTemplateResource templateForTreeHierarchyLeafItemView = GWT.create(o_u_e_p_c_c_t_TreeHierarchyLeafItemViewTemplateResource.class);
    Element parentElementForTemplateOfTreeHierarchyLeafItemView = TemplateUtil.getRootTemplateParentElement(templateForTreeHierarchyLeafItemView.getContents().getText(), "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyLeafItemView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyLeafItemView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyLeafItemView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyLeafItemView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("preference-tree-leaf-item-node", new DataFieldMeta());
    dataFieldMetas.put("preference-tree-leaf-item-label", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyLeafItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyLeafItemView_Div_treeNode(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-leaf-item-node");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyLeafItemView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TreeHierarchyLeafItemView_Element_label(instance));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree-leaf-item-label");
    templateFieldsMap.put("preference-tree-leaf-item-node", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyLeafItemView_Div_treeNode(instance))));
    templateFieldsMap.put("preference-tree-leaf-item-label", ElementWrapperWidget.getWidget(TreeHierarchyLeafItemView_Element_label(instance)));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyLeafItemView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-tree-leaf-item-label"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.select(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeHierarchyLeafItemView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeHierarchyLeafItemView instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(TreeHierarchyLeafItemView_Element_label(instance)));
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Element TreeHierarchyLeafItemView_Element_label(TreeHierarchyLeafItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView::label;
  }-*/;

  native static void TreeHierarchyLeafItemView_Element_label(TreeHierarchyLeafItemView instance, Element value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView::label = value;
  }-*/;

  native static TranslationService TreeHierarchyLeafItemView_TranslationService_translationService(TreeHierarchyLeafItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView::translationService;
  }-*/;

  native static void TreeHierarchyLeafItemView_TranslationService_translationService(TreeHierarchyLeafItemView instance, TranslationService value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView::translationService = value;
  }-*/;

  native static Div TreeHierarchyLeafItemView_Div_treeNode(TreeHierarchyLeafItemView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView::treeNode;
  }-*/;

  native static void TreeHierarchyLeafItemView_Div_treeNode(TreeHierarchyLeafItemView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyLeafItemView::treeNode = value;
  }-*/;
}