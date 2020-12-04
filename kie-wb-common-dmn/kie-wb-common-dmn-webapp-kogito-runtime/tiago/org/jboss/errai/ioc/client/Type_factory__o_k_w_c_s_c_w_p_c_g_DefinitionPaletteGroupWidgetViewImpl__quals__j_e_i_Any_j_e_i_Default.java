package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionPaletteGroupWidgetViewImpl> { public interface o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/palette/categories/group/DefinitionPaletteGroupWidgetViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionPaletteGroupWidgetViewImpl.class, "Type_factory__o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionPaletteGroupWidgetViewImpl.class, Object.class, DefinitionPaletteGroupWidgetView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public DefinitionPaletteGroupWidgetViewImpl createInstance(final ContextManager contextManager) {
    final DefinitionPaletteGroupWidgetViewImpl instance = new DefinitionPaletteGroupWidgetViewImpl();
    setIncompleteInstance(instance);
    final ListItem DefinitionPaletteGroupWidgetViewImpl_moreAnchor = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteGroupWidgetViewImpl_moreAnchor);
    DefinitionPaletteGroupWidgetViewImpl_ListItem_moreAnchor(instance, DefinitionPaletteGroupWidgetViewImpl_moreAnchor);
    final ListItem DefinitionPaletteGroupWidgetViewImpl_lessAnchor = (ListItem) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_ListItem__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, DefinitionPaletteGroupWidgetViewImpl_lessAnchor);
    DefinitionPaletteGroupWidgetViewImpl_ListItem_lessAnchor(instance, DefinitionPaletteGroupWidgetViewImpl_lessAnchor);
    o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource templateForDefinitionPaletteGroupWidgetViewImpl = GWT.create(o_k_w_c_s_c_w_p_c_g_DefinitionPaletteGroupWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfDefinitionPaletteGroupWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForDefinitionPaletteGroupWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/palette/categories/group/DefinitionPaletteGroupWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/palette/categories/group/DefinitionPaletteGroupWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteGroupWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteGroupWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("moreAnchor", new DataFieldMeta());
    dataFieldMetas.put("lessAnchor", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/group/DefinitionPaletteGroupWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteGroupWidgetViewImpl_ListItem_moreAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "moreAnchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/categories/group/DefinitionPaletteGroupWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteGroupWidgetViewImpl_ListItem_lessAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "lessAnchor");
    templateFieldsMap.put("moreAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteGroupWidgetViewImpl_ListItem_moreAnchor(instance))));
    templateFieldsMap.put("lessAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DefinitionPaletteGroupWidgetViewImpl_ListItem_lessAnchor(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefinitionPaletteGroupWidgetViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("moreAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.showMore(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("lessAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.showLess(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefinitionPaletteGroupWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefinitionPaletteGroupWidgetViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static ListItem DefinitionPaletteGroupWidgetViewImpl_ListItem_moreAnchor(DefinitionPaletteGroupWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl::moreAnchor;
  }-*/;

  native static void DefinitionPaletteGroupWidgetViewImpl_ListItem_moreAnchor(DefinitionPaletteGroupWidgetViewImpl instance, ListItem value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl::moreAnchor = value;
  }-*/;

  native static ListItem DefinitionPaletteGroupWidgetViewImpl_ListItem_lessAnchor(DefinitionPaletteGroupWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl::lessAnchor;
  }-*/;

  native static void DefinitionPaletteGroupWidgetViewImpl_ListItem_lessAnchor(DefinitionPaletteGroupWidgetViewImpl instance, ListItem value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.categories.group.DefinitionPaletteGroupWidgetViewImpl::lessAnchor = value;
  }-*/;
}