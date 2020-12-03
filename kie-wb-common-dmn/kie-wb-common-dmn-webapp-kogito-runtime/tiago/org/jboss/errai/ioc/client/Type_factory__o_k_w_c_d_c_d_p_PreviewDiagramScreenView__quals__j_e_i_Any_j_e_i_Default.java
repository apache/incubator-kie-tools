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
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreen.View;
import org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenView;

public class Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenView__quals__j_e_i_Any_j_e_i_Default extends Factory<PreviewDiagramScreenView> { public interface o_k_w_c_d_c_d_p_PreviewDiagramScreenViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/dmn/client/docks/preview/PreviewDiagramScreenView.html") public TextResource getContents();
  @Source("org/kie/workbench/common/dmn/client/docks/preview/PreviewDiagramScreenView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreviewDiagramScreenView.class, "Type_factory__o_k_w_c_d_c_d_p_PreviewDiagramScreenView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreviewDiagramScreenView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, View.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_d_c_d_p_PreviewDiagramScreenViewTemplateResource) GWT.create(o_k_w_c_d_c_d_p_PreviewDiagramScreenViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public PreviewDiagramScreenView createInstance(final ContextManager contextManager) {
    final PreviewDiagramScreenView instance = new PreviewDiagramScreenView();
    setIncompleteInstance(instance);
    final FlowPanel PreviewDiagramScreenView_previewPanelBody = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_g_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PreviewDiagramScreenView_previewPanelBody);
    PreviewDiagramScreenView_FlowPanel_previewPanelBody(instance, PreviewDiagramScreenView_previewPanelBody);
    o_k_w_c_d_c_d_p_PreviewDiagramScreenViewTemplateResource templateForPreviewDiagramScreenView = GWT.create(o_k_w_c_d_c_d_p_PreviewDiagramScreenViewTemplateResource.class);
    Element parentElementForTemplateOfPreviewDiagramScreenView = TemplateUtil.getRootTemplateParentElement(templateForPreviewDiagramScreenView.getContents().getText(), "org/kie/workbench/common/dmn/client/docks/preview/PreviewDiagramScreenView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/docks/preview/PreviewDiagramScreenView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreviewDiagramScreenView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreviewDiagramScreenView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("previewPanelBody", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenView", "org/kie/workbench/common/dmn/client/docks/preview/PreviewDiagramScreenView.html", new Supplier<Widget>() {
      public Widget get() {
        return PreviewDiagramScreenView_FlowPanel_previewPanelBody(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "previewPanelBody");
    templateFieldsMap.put("previewPanelBody", PreviewDiagramScreenView_FlowPanel_previewPanelBody(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreviewDiagramScreenView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreviewDiagramScreenView) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreviewDiagramScreenView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static FlowPanel PreviewDiagramScreenView_FlowPanel_previewPanelBody(PreviewDiagramScreenView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenView::previewPanelBody;
  }-*/;

  native static void PreviewDiagramScreenView_FlowPanel_previewPanelBody(PreviewDiagramScreenView instance, FlowPanel value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.preview.PreviewDiagramScreenView::previewPanelBody = value;
  }-*/;
}