package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorCore.View;
import org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.listbar.ResizeFlowPanel;
import org.uberfire.ext.editor.commons.client.BaseEditorView;
import org.uberfire.ext.editor.commons.client.BaseEditorViewImpl;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public class Type_factory__o_k_w_c_s_k_c_e_DiagramEditorView__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorView> { public interface o_k_w_c_s_k_c_e_DiagramEditorViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/kogito/client/editor/DiagramEditorView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_k_c_e_DiagramEditorView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramEditorView.class, "Type_factory__o_k_w_c_s_k_c_e_DiagramEditorView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramEditorView.class, BaseEditorViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, BaseEditorView.class, HasBusyIndicator.class, View.class, UberView.class, HasPresenter.class, RequiresResize.class, ProvidesResize.class });
  }

  public DiagramEditorView createInstance(final ContextManager contextManager) {
    final ResizeFlowPanel _editorPanel_0 = (ResizeFlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_u_c_w_w_l_ResizeFlowPanel__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorView instance = new DiagramEditorView(_editorPanel_0);
    registerDependentScopedReference(instance, _editorPanel_0);
    setIncompleteInstance(instance);
    o_k_w_c_s_k_c_e_DiagramEditorViewTemplateResource templateForDiagramEditorView = GWT.create(o_k_w_c_s_k_c_e_DiagramEditorViewTemplateResource.class);
    Element parentElementForTemplateOfDiagramEditorView = TemplateUtil.getRootTemplateParentElement(templateForDiagramEditorView.getContents().getText(), "org/kie/workbench/common/stunner/kogito/client/editor/DiagramEditorView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/kogito/client/editor/DiagramEditorView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("editorPanel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorView", "org/kie/workbench/common/stunner/kogito/client/editor/DiagramEditorView.html", new Supplier<Widget>() {
      public Widget get() {
        return DiagramEditorView_ResizeFlowPanel_editorPanel(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "editorPanel");
    templateFieldsMap.put("editorPanel", DiagramEditorView_ResizeFlowPanel_editorPanel(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramEditorView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramEditorView instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupWidget(instance);
  }

  native static ResizeFlowPanel DiagramEditorView_ResizeFlowPanel_editorPanel(DiagramEditorView instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorView::editorPanel;
  }-*/;

  native static void DiagramEditorView_ResizeFlowPanel_editorPanel(DiagramEditorView instance, ResizeFlowPanel value) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.client.editor.DiagramEditorView::editorPanel = value;
  }-*/;
}