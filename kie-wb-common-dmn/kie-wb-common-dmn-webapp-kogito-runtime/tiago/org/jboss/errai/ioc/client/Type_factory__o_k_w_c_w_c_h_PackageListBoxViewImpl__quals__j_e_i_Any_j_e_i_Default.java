package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.handlers.PackageListBoxView;
import org.kie.workbench.common.widgets.client.handlers.PackageListBoxViewImpl;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;

public class Type_factory__o_k_w_c_w_c_h_PackageListBoxViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PackageListBoxViewImpl> { public interface o_k_w_c_w_c_h_PackageListBoxViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/handlers/PackageListBoxViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_h_PackageListBoxViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PackageListBoxViewImpl.class, "Type_factory__o_k_w_c_w_c_h_PackageListBoxViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PackageListBoxViewImpl.class, Object.class, PackageListBoxView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public PackageListBoxViewImpl createInstance(final ContextManager contextManager) {
    final KieSelectElement _kieSelectElement_0 = (KieSelectElement) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default");
    final PackageListBoxViewImpl instance = new PackageListBoxViewImpl(_kieSelectElement_0);
    registerDependentScopedReference(instance, _kieSelectElement_0);
    setIncompleteInstance(instance);
    final KieSelectElement PackageListBoxViewImpl_kieSelectElement = (KieSelectElement) contextManager.getInstance("Type_factory__o_k_w_c_w_c_w_KieSelectElement__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PackageListBoxViewImpl_kieSelectElement);
    PackageListBoxViewImpl_KieSelectElement_kieSelectElement(instance, PackageListBoxViewImpl_kieSelectElement);
    o_k_w_c_w_c_h_PackageListBoxViewImplTemplateResource templateForPackageListBoxViewImpl = GWT.create(o_k_w_c_w_c_h_PackageListBoxViewImplTemplateResource.class);
    Element parentElementForTemplateOfPackageListBoxViewImpl = TemplateUtil.getRootTemplateParentElement(templateForPackageListBoxViewImpl.getContents().getText(), "org/kie/workbench/common/widgets/client/handlers/PackageListBoxViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/handlers/PackageListBoxViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPackageListBoxViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPackageListBoxViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("kieSelectElement", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.handlers.PackageListBoxViewImpl", "org/kie/workbench/common/widgets/client/handlers/PackageListBoxViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(PackageListBoxViewImpl_KieSelectElement_kieSelectElement(instance).getElement(), null);
      }
    }, dataFieldElements, dataFieldMetas, "kieSelectElement");
    templateFieldsMap.put("kieSelectElement", ElementWrapperWidget.getWidget(PackageListBoxViewImpl_KieSelectElement_kieSelectElement(instance).getElement(), null));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPackageListBoxViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PackageListBoxViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final PackageListBoxViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static KieSelectElement PackageListBoxViewImpl_KieSelectElement_kieSelectElement(PackageListBoxViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.PackageListBoxViewImpl::kieSelectElement;
  }-*/;

  native static void PackageListBoxViewImpl_KieSelectElement_kieSelectElement(PackageListBoxViewImpl instance, KieSelectElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.PackageListBoxViewImpl::kieSelectElement = value;
  }-*/;
}