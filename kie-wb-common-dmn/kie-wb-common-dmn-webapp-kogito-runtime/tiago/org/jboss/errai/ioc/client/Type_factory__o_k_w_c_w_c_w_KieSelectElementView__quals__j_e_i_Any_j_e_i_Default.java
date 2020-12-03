package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLSelectElement;
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
import org.kie.workbench.common.widgets.client.widget.KieSelectElement.View;
import org.kie.workbench.common.widgets.client.widget.KieSelectElementView;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_w_c_w_KieSelectElementView__quals__j_e_i_Any_j_e_i_Default extends Factory<KieSelectElementView> { public interface o_k_w_c_w_c_w_KieSelectElementViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_w_KieSelectElementView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KieSelectElementView.class, "Type_factory__o_k_w_c_w_c_w_KieSelectElementView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KieSelectElementView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public KieSelectElementView createInstance(final ContextManager contextManager) {
    final KieSelectElementView instance = new KieSelectElementView();
    setIncompleteInstance(instance);
    final HTMLSelectElement KieSelectElementView_select = (HTMLSelectElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLSelectElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KieSelectElementView_select);
    KieSelectElementView_HTMLSelectElement_select(instance, KieSelectElementView_select);
    o_k_w_c_w_c_w_KieSelectElementViewTemplateResource templateForKieSelectElementView = GWT.create(o_k_w_c_w_c_w_KieSelectElementViewTemplateResource.class);
    Element parentElementForTemplateOfKieSelectElementView = TemplateUtil.getRootTemplateParentElement(templateForKieSelectElementView.getContents().getText(), "org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieSelectElementView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieSelectElementView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("select", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.widget.KieSelectElementView", "org/kie/workbench/common/widgets/client/widget/KieSelectElementView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieSelectElementView_HTMLSelectElement_select(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "select");
    templateFieldsMap.put("select", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KieSelectElementView_HTMLSelectElement_select(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKieSelectElementView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("select"), new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        KieSelectElementView_onSelectChanged_ChangeEvent(instance, event);
      }
    }, ChangeEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KieSelectElementView) instance, contextManager);
  }

  public void destroyInstanceHelper(final KieSelectElementView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLSelectElement KieSelectElementView_HTMLSelectElement_select(KieSelectElementView instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.widget.KieSelectElementView::select;
  }-*/;

  native static void KieSelectElementView_HTMLSelectElement_select(KieSelectElementView instance, HTMLSelectElement value) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KieSelectElementView::select = value;
  }-*/;

  public native static void KieSelectElementView_onSelectChanged_ChangeEvent(KieSelectElementView instance, ChangeEvent a0) /*-{
    instance.@org.kie.workbench.common.widgets.client.widget.KieSelectElementView::onSelectChanged(Lcom/google/gwt/event/dom/client/ChangeEvent;)(a0);
  }-*/;
}