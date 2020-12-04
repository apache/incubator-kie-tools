package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLLIElement;
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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.select.SelectOptionComponent.View;
import org.uberfire.ext.widgets.common.client.select.SelectOptionView;

public class Type_factory__o_u_e_w_c_c_s_SelectOptionView__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectOptionView> { public interface o_u_e_w_c_c_s_SelectOptionViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/select/SelectOptionView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_w_c_c_s_SelectOptionView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SelectOptionView.class, "Type_factory__o_u_e_w_c_c_s_SelectOptionView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SelectOptionView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public SelectOptionView createInstance(final ContextManager contextManager) {
    final SelectOptionView instance = new SelectOptionView();
    setIncompleteInstance(instance);
    final HTMLLIElement SelectOptionView_selector = (HTMLLIElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLIElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SelectOptionView_selector);
    SelectOptionView_HTMLLIElement_selector(instance, SelectOptionView_selector);
    final HTMLAnchorElement SelectOptionView_option = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, SelectOptionView_option);
    SelectOptionView_HTMLAnchorElement_option(instance, SelectOptionView_option);
    o_u_e_w_c_c_s_SelectOptionViewTemplateResource templateForSelectOptionView = GWT.create(o_u_e_w_c_c_s_SelectOptionViewTemplateResource.class);
    Element parentElementForTemplateOfSelectOptionView = TemplateUtil.getRootTemplateParentElement(templateForSelectOptionView.getContents().getText(), "org/uberfire/ext/widgets/common/client/select/SelectOptionView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/select/SelectOptionView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectOptionView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectOptionView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("selector", new DataFieldMeta());
    dataFieldMetas.put("option", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.select.SelectOptionView", "org/uberfire/ext/widgets/common/client/select/SelectOptionView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectOptionView_HTMLLIElement_selector(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "selector");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.select.SelectOptionView", "org/uberfire/ext/widgets/common/client/select/SelectOptionView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectOptionView_HTMLAnchorElement_option(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "option");
    templateFieldsMap.put("selector", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectOptionView_HTMLLIElement_selector(instance))));
    templateFieldsMap.put("option", ElementWrapperWidget.getWidget(TemplateUtil.asElement(SelectOptionView_HTMLAnchorElement_option(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfSelectOptionView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("option"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SelectOptionView) instance, contextManager);
  }

  public void destroyInstanceHelper(final SelectOptionView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLLIElement SelectOptionView_HTMLLIElement_selector(SelectOptionView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.select.SelectOptionView::selector;
  }-*/;

  native static void SelectOptionView_HTMLLIElement_selector(SelectOptionView instance, HTMLLIElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.select.SelectOptionView::selector = value;
  }-*/;

  native static HTMLAnchorElement SelectOptionView_HTMLAnchorElement_option(SelectOptionView instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.select.SelectOptionView::option;
  }-*/;

  native static void SelectOptionView_HTMLAnchorElement_option(SelectOptionView instance, HTMLAnchorElement value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.select.SelectOptionView::option = value;
  }-*/;
}