package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLOptionElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.widget.KieSelectOptionView;
import org.kie.workbench.common.widgets.client.widget.ListItemView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_w_KieSelectOptionView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectOptionView> { public interface o_k_w_c_w_c_w_KieSelectOptionViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_w_KieSelectOptionView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieSelectOptionView.class, "Type_factory__o_k_w_c_w_c_w_KieSelectOptionView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieSelectOptionView.class, Object.class, ListItemView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public KieSelectOptionView createInstance(final ContextManager contextManager) {
    final KieSelectOptionView instance = new KieSelectOptionView();
    setIncompleteInstance(instance);
    final HTMLOptionElement KieSelectOptionView_option = (HTMLOptionElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLOptionElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KieSelectOptionView_option);
    KieSelectOptionView_HTMLOptionElement_option(instance, KieSelectOptionView_option);
    o_k_w_c_w_c_w_KieSelectOptionViewTemplateResource templateForKieSelectOptionView = GWT.create(o_k_w_c_w_c_w_KieSelectOptionViewTemplateResource.class);
    Element parentElementForTemplateOfKieSelectOptionView = TemplateUtil.getRootTemplateParentElement(templateForKieSelectOptionView.getContents().getText(), "org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html", "option");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieSelectOptionView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieSelectOptionView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("option", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KieSelectOptionView", "org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieSelectOptionView_HTMLOptionElement_option(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "option");
    templateFieldsMap.put("option", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieSelectOptionView_HTMLOptionElement_option(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieSelectOptionView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KieSelectOptionView) instance, contextManager);
  }

  public void destroyInstanceHelper(final KieSelectOptionView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLOptionElement KieSelectOptionView_HTMLOptionElement_option(KieSelectOptionView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KieSelectOptionView::option;
  }-*/;

  native static void KieSelectOptionView_HTMLOptionElement_option(KieSelectOptionView instance, HTMLOptionElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KieSelectOptionView::option = value;
  }-*/;
}