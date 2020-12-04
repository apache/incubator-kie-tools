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
import org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl;
import org.kie.workbench.common.stunner.core.client.components.views.ImageElementRendererView;

public class Type_factory__o_k_w_c_s_c_w_v_ImageElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ImageElementRendererViewImpl> { public interface o_k_w_c_s_c_w_v_ImageElementRendererViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/stunner/client/widgets/views/ImageElementRendererViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/stunner/client/widgets/views/ImageElementRendererViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_s_c_w_v_ImageElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ImageElementRendererViewImpl.class, "Type_factory__o_k_w_c_s_c_w_v_ImageElementRendererViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ImageElementRendererViewImpl.class, Object.class, ImageElementRendererView.class, IsElement.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_s_c_w_v_ImageElementRendererViewImplTemplateResource) GWT.create(o_k_w_c_s_c_w_v_ImageElementRendererViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public ImageElementRendererViewImpl createInstance(final ContextManager contextManager) {
    final ImageElementRendererViewImpl instance = new ImageElementRendererViewImpl();
    setIncompleteInstance(instance);
    final Div ImageElementRendererViewImpl_content = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ImageElementRendererViewImpl_content);
    ImageElementRendererViewImpl_Div_content(instance, ImageElementRendererViewImpl_content);
    final Div ImageElementRendererViewImpl_icon = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ImageElementRendererViewImpl_icon);
    ImageElementRendererViewImpl_Div_icon(instance, ImageElementRendererViewImpl_icon);
    o_k_w_c_s_c_w_v_ImageElementRendererViewImplTemplateResource templateForImageElementRendererViewImpl = GWT.create(o_k_w_c_s_c_w_v_ImageElementRendererViewImplTemplateResource.class);
    Element parentElementForTemplateOfImageElementRendererViewImpl = TemplateUtil.getRootTemplateParentElement(templateForImageElementRendererViewImpl.getContents().getText(), "org/kie/workbench/common/stunner/client/widgets/views/ImageElementRendererViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/client/widgets/views/ImageElementRendererViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfImageElementRendererViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfImageElementRendererViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("content", new DataFieldMeta());
    dataFieldMetas.put("icon", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl", "org/kie/workbench/common/stunner/client/widgets/views/ImageElementRendererViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImageElementRendererViewImpl_Div_content(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "content");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl", "org/kie/workbench/common/stunner/client/widgets/views/ImageElementRendererViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImageElementRendererViewImpl_Div_icon(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "icon");
    templateFieldsMap.put("content", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImageElementRendererViewImpl_Div_content(instance))));
    templateFieldsMap.put("icon", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ImageElementRendererViewImpl_Div_icon(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfImageElementRendererViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ImageElementRendererViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ImageElementRendererViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div ImageElementRendererViewImpl_Div_icon(ImageElementRendererViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl::icon;
  }-*/;

  native static void ImageElementRendererViewImpl_Div_icon(ImageElementRendererViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl::icon = value;
  }-*/;

  native static Div ImageElementRendererViewImpl_Div_content(ImageElementRendererViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl::content;
  }-*/;

  native static void ImageElementRendererViewImpl_Div_content(ImageElementRendererViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.views.ImageElementRendererViewImpl::content = value;
  }-*/;
}