package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<BS3PaletteWidgetViewImpl> { public interface o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BS3PaletteWidgetViewImpl.class, "Type_factory__o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BS3PaletteWidgetViewImpl.class, Object.class, BS3PaletteWidgetView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource) GWT.create(o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public BS3PaletteWidgetViewImpl createInstance(final ContextManager contextManager) {
    final BS3PaletteWidgetViewImpl instance = new BS3PaletteWidgetViewImpl();
    setIncompleteInstance(instance);
    final UnorderedList BS3PaletteWidgetViewImpl_ul = (UnorderedList) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_UnorderedList__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, BS3PaletteWidgetViewImpl_ul);
    BS3PaletteWidgetViewImpl_UnorderedList_ul(instance, BS3PaletteWidgetViewImpl_ul);
    final Div BS3PaletteWidgetViewImpl_palette = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, BS3PaletteWidgetViewImpl_palette);
    BS3PaletteWidgetViewImpl_Div_palette(instance, BS3PaletteWidgetViewImpl_palette);
    o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource templateForBS3PaletteWidgetViewImpl = GWT.create(o_k_w_c_s_c_w_p_BS3PaletteWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfBS3PaletteWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForBS3PaletteWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBS3PaletteWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBS3PaletteWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("kie-palette", new DataFieldMeta());
    dataFieldMetas.put("list-group", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BS3PaletteWidgetViewImpl_Div_palette(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "kie-palette");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/BS3PaletteWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(BS3PaletteWidgetViewImpl_UnorderedList_ul(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "list-group");
    templateFieldsMap.put("kie-palette", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BS3PaletteWidgetViewImpl_Div_palette(instance))));
    templateFieldsMap.put("list-group", ElementWrapperWidget.getWidget(TemplateUtil.asElement(BS3PaletteWidgetViewImpl_UnorderedList_ul(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfBS3PaletteWidgetViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((BS3PaletteWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final BS3PaletteWidgetViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div BS3PaletteWidgetViewImpl_Div_palette(BS3PaletteWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl::palette;
  }-*/;

  native static void BS3PaletteWidgetViewImpl_Div_palette(BS3PaletteWidgetViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl::palette = value;
  }-*/;

  native static UnorderedList BS3PaletteWidgetViewImpl_UnorderedList_ul(BS3PaletteWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl::ul;
  }-*/;

  native static void BS3PaletteWidgetViewImpl_UnorderedList_ul(BS3PaletteWidgetViewImpl instance, UnorderedList value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.BS3PaletteWidgetViewImpl::ul = value;
  }-*/;
}