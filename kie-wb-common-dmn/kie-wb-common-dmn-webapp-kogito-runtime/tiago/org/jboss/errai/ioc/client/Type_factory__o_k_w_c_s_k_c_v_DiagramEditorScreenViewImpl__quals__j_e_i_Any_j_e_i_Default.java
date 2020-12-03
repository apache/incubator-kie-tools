package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenView;
import org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenViewImpl;

public class Type_factory__o_k_w_c_s_k_c_v_DiagramEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DiagramEditorScreenViewImpl> { public interface o_k_w_c_s_k_c_v_DiagramEditorScreenViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/kogito/client/view/DiagramEditorScreenViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_k_c_v_DiagramEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DiagramEditorScreenViewImpl.class, "Type_factory__o_k_w_c_s_k_c_v_DiagramEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DiagramEditorScreenViewImpl.class, Object.class, DiagramEditorScreenView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public DiagramEditorScreenViewImpl createInstance(final ContextManager contextManager) {
    final FlowPanel _loadingPanel_0 = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    final FlowPanel _widgetPanel_1 = (FlowPanel) contextManager.getInstance("ExtensionProvided_factory__c_g_g_u_c_u_FlowPanel__quals__j_e_i_Any_j_e_i_Default");
    final DiagramEditorScreenViewImpl instance = new DiagramEditorScreenViewImpl(_loadingPanel_0, _widgetPanel_1);
    registerDependentScopedReference(instance, _loadingPanel_0);
    registerDependentScopedReference(instance, _widgetPanel_1);
    setIncompleteInstance(instance);
    o_k_w_c_s_k_c_v_DiagramEditorScreenViewImplTemplateResource templateForDiagramEditorScreenViewImpl = GWT.create(o_k_w_c_s_k_c_v_DiagramEditorScreenViewImplTemplateResource.class);
    Element parentElementForTemplateOfDiagramEditorScreenViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDiagramEditorScreenViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/kogito/client/view/DiagramEditorScreenViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/kogito/client/view/DiagramEditorScreenViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorScreenViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorScreenViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("loadingPanel", new DataFieldMeta());
    dataFieldMetas.put("widgetPanel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenViewImpl", "org/kie/workbench/common/stunner/kogito/client/view/DiagramEditorScreenViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return _loadingPanel_0.asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "loadingPanel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.kogito.client.view.DiagramEditorScreenViewImpl", "org/kie/workbench/common/stunner/kogito/client/view/DiagramEditorScreenViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return _widgetPanel_1.asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "widgetPanel");
    templateFieldsMap.put("loadingPanel", _loadingPanel_0.asWidget());
    templateFieldsMap.put("widgetPanel", _widgetPanel_1.asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDiagramEditorScreenViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DiagramEditorScreenViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DiagramEditorScreenViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }
}