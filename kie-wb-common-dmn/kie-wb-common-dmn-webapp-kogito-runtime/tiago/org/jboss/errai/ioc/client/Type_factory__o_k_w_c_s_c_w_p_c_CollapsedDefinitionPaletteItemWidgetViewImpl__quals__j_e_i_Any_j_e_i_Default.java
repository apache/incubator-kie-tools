package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
import org.jboss.errai.common.client.dom.Button;
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
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetView;
import org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsedDefinitionPaletteItemWidgetViewImpl> { public interface o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/palette/collapsed/CollapsedDefinitionPaletteItemWidgetViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/widgets/palette/collapsed/CollapsedDefinitionPaletteItemWidgetViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CollapsedDefinitionPaletteItemWidgetViewImpl.class, "Type_factory__o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CollapsedDefinitionPaletteItemWidgetViewImpl.class, Object.class, CollapsedDefinitionPaletteItemWidgetView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImplTemplateResource) GWT.create(o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public CollapsedDefinitionPaletteItemWidgetViewImpl createInstance(final ContextManager contextManager) {
    final DOMGlyphRenderers _domGlyphRenderers_1 = (DOMGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final Button _icon_0 = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final CollapsedDefinitionPaletteItemWidgetViewImpl instance = new CollapsedDefinitionPaletteItemWidgetViewImpl(_icon_0, _domGlyphRenderers_1);
    registerDependentScopedReference(instance, _icon_0);
    setIncompleteInstance(instance);
    o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImplTemplateResource templateForCollapsedDefinitionPaletteItemWidgetViewImpl = GWT.create(o_k_w_c_s_c_w_p_c_CollapsedDefinitionPaletteItemWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfCollapsedDefinitionPaletteItemWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForCollapsedDefinitionPaletteItemWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/palette/collapsed/CollapsedDefinitionPaletteItemWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/palette/collapsed/CollapsedDefinitionPaletteItemWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCollapsedDefinitionPaletteItemWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCollapsedDefinitionPaletteItemWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("icon", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetViewImpl", "org/kie/workbench/common/stunner/client/widgets/palette/collapsed/CollapsedDefinitionPaletteItemWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsedDefinitionPaletteItemWidgetViewImpl_Button_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsedDefinitionPaletteItemWidgetViewImpl_Button_icon(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCollapsedDefinitionPaletteItemWidgetViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("icon"), new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        instance.onMouseDown(event);
      }
    }, MouseDownEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CollapsedDefinitionPaletteItemWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final CollapsedDefinitionPaletteItemWidgetViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Button CollapsedDefinitionPaletteItemWidgetViewImpl_Button_icon(CollapsedDefinitionPaletteItemWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetViewImpl::icon;
  }-*/;

  native static void CollapsedDefinitionPaletteItemWidgetViewImpl_Button_icon(CollapsedDefinitionPaletteItemWidgetViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.palette.collapsed.CollapsedDefinitionPaletteItemWidgetViewImpl::icon = value;
  }-*/;
}