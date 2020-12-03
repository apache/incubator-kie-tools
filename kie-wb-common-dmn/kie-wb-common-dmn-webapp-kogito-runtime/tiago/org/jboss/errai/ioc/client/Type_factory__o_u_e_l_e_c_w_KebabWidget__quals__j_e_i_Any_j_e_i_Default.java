package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.ext.layout.editor.client.widgets.KebabWidget;

public class Type_factory__o_u_e_l_e_c_w_KebabWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<KebabWidget> { public interface o_u_e_l_e_c_w_KebabWidgetTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/layout/editor/client/widgets/KebabWidget.html") public TextResource getContents(); }
  public Type_factory__o_u_e_l_e_c_w_KebabWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KebabWidget.class, "Type_factory__o_u_e_l_e_c_w_KebabWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KebabWidget.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public KebabWidget createInstance(final ContextManager contextManager) {
    final KebabWidget instance = new KebabWidget();
    setIncompleteInstance(instance);
    final Anchor KebabWidget_remove = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabWidget_remove);
    KebabWidget_Anchor_remove(instance, KebabWidget_remove);
    final Anchor KebabWidget_edit = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabWidget_edit);
    KebabWidget_Anchor_edit(instance, KebabWidget_edit);
    final Div KebabWidget_leKebab = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, KebabWidget_leKebab);
    KebabWidget_Div_leKebab(instance, KebabWidget_leKebab);
    o_u_e_l_e_c_w_KebabWidgetTemplateResource templateForKebabWidget = GWT.create(o_u_e_l_e_c_w_KebabWidgetTemplateResource.class);
    Element parentElementForTemplateOfKebabWidget = TemplateUtil.getRootTemplateParentElement(templateForKebabWidget.getContents().getText(), "org/uberfire/ext/layout/editor/client/widgets/KebabWidget.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/layout/editor/client/widgets/KebabWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(3);
    dataFieldMetas.put("remove", new DataFieldMeta());
    dataFieldMetas.put("edit", new DataFieldMeta());
    dataFieldMetas.put("le-kebab", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.KebabWidget", "org/uberfire/ext/layout/editor/client/widgets/KebabWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabWidget_Anchor_remove(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "remove");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.KebabWidget", "org/uberfire/ext/layout/editor/client/widgets/KebabWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabWidget_Anchor_edit(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "edit");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.layout.editor.client.widgets.KebabWidget", "org/uberfire/ext/layout/editor/client/widgets/KebabWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabWidget_Div_leKebab(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "le-kebab");
    templateFieldsMap.put("remove", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabWidget_Anchor_remove(instance))));
    templateFieldsMap.put("edit", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabWidget_Anchor_edit(instance))));
    templateFieldsMap.put("le-kebab", ElementWrapperWidget.getWidget(TemplateUtil.asElement(KebabWidget_Div_leKebab(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfKebabWidget), templateFieldsMap.values());
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("edit"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.editClick(event);
      }
    }, 1);
    TemplateUtil.setupNativeEventListener(instance, (ElementWrapperWidget) templateFieldsMap.get("remove"), new EventListener() {
      public void onBrowserEvent(Event event) {
        instance.removeClick(event);
      }
    }, 1);
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KebabWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final KebabWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div KebabWidget_Div_leKebab(KebabWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.KebabWidget::leKebab;
  }-*/;

  native static void KebabWidget_Div_leKebab(KebabWidget instance, Div value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.KebabWidget::leKebab = value;
  }-*/;

  native static Anchor KebabWidget_Anchor_edit(KebabWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.KebabWidget::edit;
  }-*/;

  native static void KebabWidget_Anchor_edit(KebabWidget instance, Anchor value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.KebabWidget::edit = value;
  }-*/;

  native static Anchor KebabWidget_Anchor_remove(KebabWidget instance) /*-{
    return instance.@org.uberfire.ext.layout.editor.client.widgets.KebabWidget::remove;
  }-*/;

  native static void KebabWidget_Anchor_remove(KebabWidget instance, Anchor value) /*-{
    instance.@org.uberfire.ext.layout.editor.client.widgets.KebabWidget::remove = value;
  }-*/;
}