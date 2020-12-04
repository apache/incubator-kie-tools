package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreen.View;
import org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView;

public class Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorExplorerScreenView> { public interface o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/kogito/client/screens/DiagramEditorExplorerScreenView.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/kogito/client/screens/DiagramEditorExplorerScreenView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramEditorExplorerScreenView.class, "Type_factory__o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramEditorExplorerScreenView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenViewTemplateResource) GWT.create(o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public DiagramEditorExplorerScreenView createInstance(final ContextManager contextManager) {
    final DiagramEditorExplorerScreenView instance = new DiagramEditorExplorerScreenView();
    setIncompleteInstance(instance);
    final FlowPanel DiagramEditorExplorerScreenView_explorerPanelBody = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DiagramEditorExplorerScreenView_explorerPanelBody);
    DiagramEditorExplorerScreenView_FlowPanel_explorerPanelBody(instance, DiagramEditorExplorerScreenView_explorerPanelBody);
    final FlowPanel DiagramEditorExplorerScreenView_previewPanelBody = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DiagramEditorExplorerScreenView_previewPanelBody);
    DiagramEditorExplorerScreenView_FlowPanel_previewPanelBody(instance, DiagramEditorExplorerScreenView_previewPanelBody);
    o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenViewTemplateResource templateForDiagramEditorExplorerScreenView = GWT.create(o_k_w_c_s_k_c_s_DiagramEditorExplorerScreenViewTemplateResource.class);
    Element parentElementForTemplateOfDiagramEditorExplorerScreenView = TemplateUtil.getRootTemplateParentElement(templateForDiagramEditorExplorerScreenView.getContents().getText(), "org/kie/workbench/common/stunner/kogito/client/screens/DiagramEditorExplorerScreenView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/kogito/client/screens/DiagramEditorExplorerScreenView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorExplorerScreenView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorExplorerScreenView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("previewPanelBody", new DataFieldMeta());
    dataFieldMetas.put("explorerPanelBody", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView", "org/kie/workbench/common/stunner/kogito/client/screens/DiagramEditorExplorerScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return DiagramEditorExplorerScreenView_FlowPanel_previewPanelBody(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "previewPanelBody");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView", "org/kie/workbench/common/stunner/kogito/client/screens/DiagramEditorExplorerScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return DiagramEditorExplorerScreenView_FlowPanel_explorerPanelBody(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "explorerPanelBody");
    templateFieldsMap.put("previewPanelBody", DiagramEditorExplorerScreenView_FlowPanel_previewPanelBody(instance).asWidget());
    templateFieldsMap.put("explorerPanelBody", DiagramEditorExplorerScreenView_FlowPanel_explorerPanelBody(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorExplorerScreenView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramEditorExplorerScreenView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramEditorExplorerScreenView instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupWidget(instance);
  }

  native static FlowPanel DiagramEditorExplorerScreenView_FlowPanel_previewPanelBody(DiagramEditorExplorerScreenView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView::previewPanelBody;
  }-*/;

  native static void DiagramEditorExplorerScreenView_FlowPanel_previewPanelBody(DiagramEditorExplorerScreenView instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView::previewPanelBody = value;
  }-*/;

  native static FlowPanel DiagramEditorExplorerScreenView_FlowPanel_explorerPanelBody(DiagramEditorExplorerScreenView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView::explorerPanelBody;
  }-*/;

  native static void DiagramEditorExplorerScreenView_FlowPanel_explorerPanelBody(DiagramEditorExplorerScreenView instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.screens.DiagramEditorExplorerScreenView::explorerPanelBody = value;
  }-*/;
}