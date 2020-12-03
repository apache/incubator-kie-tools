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
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructureView;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructurePresenter.View;
import org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructureView;

public class Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructureView__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeHierarchyStructureView> { public interface o_u_e_p_c_c_t_TreeHierarchyStructureViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyStructureView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyStructureView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructureView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeHierarchyStructureView.class, "Type_factory__o_u_e_p_c_c_t_TreeHierarchyStructureView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeHierarchyStructureView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, HierarchyStructureView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_c_t_TreeHierarchyStructureViewTemplateResource) GWT.create(o_u_e_p_c_c_t_TreeHierarchyStructureViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public TreeHierarchyStructureView createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final TreeHierarchyStructureView instance = new TreeHierarchyStructureView(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Div TreeHierarchyStructureView_tree = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, TreeHierarchyStructureView_tree);
    TreeHierarchyStructureView_Div_tree(instance, TreeHierarchyStructureView_tree);
    o_u_e_p_c_c_t_TreeHierarchyStructureViewTemplateResource templateForTreeHierarchyStructureView = GWT.create(o_u_e_p_c_c_t_TreeHierarchyStructureViewTemplateResource.class);
    Element parentElementForTemplateOfTreeHierarchyStructureView = TemplateUtil.getRootTemplateParentElement(templateForTreeHierarchyStructureView.getContents().getText(), "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyStructureView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/central/tree/TreeHierarchyStructureView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyStructureView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyStructureView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("preference-tree", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructureView", "org/uberfire/ext/preferences/client/central/tree/TreeHierarchyStructureView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyStructureView_Div_tree(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preference-tree");
    templateFieldsMap.put("preference-tree", ElementWrapperWidget.getWidget(TemplateUtil.asElement(TreeHierarchyStructureView_Div_tree(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfTreeHierarchyStructureView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeHierarchyStructureView) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeHierarchyStructureView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div TreeHierarchyStructureView_Div_tree(TreeHierarchyStructureView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructureView::tree;
  }-*/;

  native static void TreeHierarchyStructureView_Div_tree(TreeHierarchyStructureView instance, Div value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.tree.TreeHierarchyStructureView::tree = value;
  }-*/;
}