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
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;

public class Type_factory__o_k_w_c_s_c_d_DefaultDiagramDocumentationView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultDiagramDocumentationView> { public interface o_k_w_c_s_c_d_DefaultDiagramDocumentationViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/core/documentation/DefaultDiagramDocumentationView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_d_DefaultDiagramDocumentationView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultDiagramDocumentationView.class, "Type_factory__o_k_w_c_s_c_d_DefaultDiagramDocumentationView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultDiagramDocumentationView.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, DocumentationView.class });
  }

  public DefaultDiagramDocumentationView createInstance(final ContextManager contextManager) {
    final DefaultDiagramDocumentationView instance = new DefaultDiagramDocumentationView();
    setIncompleteInstance(instance);
    o_k_w_c_s_c_d_DefaultDiagramDocumentationViewTemplateResource templateForDefaultDiagramDocumentationView = GWT.create(o_k_w_c_s_c_d_DefaultDiagramDocumentationViewTemplateResource.class);
    Element parentElementForTemplateOfDefaultDiagramDocumentationView = TemplateUtil.getRootTemplateParentElement(templateForDefaultDiagramDocumentationView.getContents().getText(), "org/kie/workbench/common/stunner/core/documentation/DefaultDiagramDocumentationView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/core/documentation/DefaultDiagramDocumentationView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultDiagramDocumentationView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultDiagramDocumentationView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultDiagramDocumentationView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultDiagramDocumentationView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultDiagramDocumentationView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }
}