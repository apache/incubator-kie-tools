package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.VideoElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
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
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Image;
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
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetView;
import org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl;

public class Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureWidgetViewImpl> { public interface o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PictureWidgetViewImpl.class, "Type_factory__o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PictureWidgetViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, PictureWidgetView.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource) GWT.create(o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public PictureWidgetViewImpl createInstance(final ContextManager contextManager) {
    final PictureWidgetViewImpl instance = new PictureWidgetViewImpl();
    setIncompleteInstance(instance);
    final Button PictureWidgetViewImpl_takeAnotherPicture = (Button) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PictureWidgetViewImpl_takeAnotherPicture);
    PictureWidgetViewImpl_Button_takeAnotherPicture(instance, PictureWidgetViewImpl_takeAnotherPicture);
    final Button PictureWidgetViewImpl_takePicture = (Button) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PictureWidgetViewImpl_takePicture);
    PictureWidgetViewImpl_Button_takePicture(instance, PictureWidgetViewImpl_takePicture);
    o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource templateForPictureWidgetViewImpl = GWT.create(o_k_w_c_f_c_r_c_w_p_w_PictureWidgetViewImplTemplateResource.class);
    Element parentElementForTemplateOfPictureWidgetViewImpl = TemplateUtil.getRootTemplateParentElement(templateForPictureWidgetViewImpl.getContents().getText(), "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPictureWidgetViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPictureWidgetViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("videoContainer", new DataFieldMeta());
    dataFieldMetas.put("videoElement", new DataFieldMeta());
    dataFieldMetas.put("takePicture", new DataFieldMeta());
    dataFieldMetas.put("imageContainer", new DataFieldMeta());
    dataFieldMetas.put("imageElement", new DataFieldMeta());
    dataFieldMetas.put("takeAnotherPicture", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(PictureWidgetViewImpl_Element_videoContainer(instance));
      }
    }, dataFieldElements, dataFieldMetas, "videoContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(PictureWidgetViewImpl_VideoElement_videoElement(instance));
      }
    }, dataFieldElements, dataFieldMetas, "videoElement");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return PictureWidgetViewImpl_Button_takePicture(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "takePicture");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(PictureWidgetViewImpl_Element_imageContainer(instance));
      }
    }, dataFieldElements, dataFieldMetas, "imageContainer");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return PictureWidgetViewImpl_Image_imageElement(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "imageElement");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl", "org/kie/workbench/common/forms/common/rendering/client/widgets/picture/widget/PictureWidgetViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return PictureWidgetViewImpl_Button_takeAnotherPicture(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "takeAnotherPicture");
    templateFieldsMap.put("videoContainer", ElementWrapperWidget.getWidget(PictureWidgetViewImpl_Element_videoContainer(instance)));
    templateFieldsMap.put("videoElement", ElementWrapperWidget.getWidget(PictureWidgetViewImpl_VideoElement_videoElement(instance)));
    templateFieldsMap.put("takePicture", PictureWidgetViewImpl_Button_takePicture(instance).asWidget());
    templateFieldsMap.put("imageContainer", ElementWrapperWidget.getWidget(PictureWidgetViewImpl_Element_imageContainer(instance)));
    templateFieldsMap.put("imageElement", PictureWidgetViewImpl_Image_imageElement(instance).asWidget());
    templateFieldsMap.put("takeAnotherPicture", PictureWidgetViewImpl_Button_takeAnotherPicture(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPictureWidgetViewImpl), templateFieldsMap.values());
    ((HasClickHandlers) templateFieldsMap.get("takeAnotherPicture")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.takeAnoterPicture(event);
      }
    });
    ((HasClickHandlers) templateFieldsMap.get("takePicture")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.takePicture(event);
      }
    });
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PictureWidgetViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final PictureWidgetViewImpl instance, final ContextManager contextManager) {
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(PictureWidgetViewImpl_Element_videoContainer(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(PictureWidgetViewImpl_VideoElement_videoElement(instance)));
    ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(PictureWidgetViewImpl_Element_imageContainer(instance)));
    TemplateUtil.cleanupWidget(instance);
  }

  public void invokePostConstructs(final PictureWidgetViewImpl instance) {
    PictureWidgetViewImpl_doInit(instance);
  }

  native static Button PictureWidgetViewImpl_Button_takeAnotherPicture(PictureWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::takeAnotherPicture;
  }-*/;

  native static void PictureWidgetViewImpl_Button_takeAnotherPicture(PictureWidgetViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::takeAnotherPicture = value;
  }-*/;

  native static Button PictureWidgetViewImpl_Button_takePicture(PictureWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::takePicture;
  }-*/;

  native static void PictureWidgetViewImpl_Button_takePicture(PictureWidgetViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::takePicture = value;
  }-*/;

  native static Element PictureWidgetViewImpl_Element_imageContainer(PictureWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::imageContainer;
  }-*/;

  native static void PictureWidgetViewImpl_Element_imageContainer(PictureWidgetViewImpl instance, Element value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::imageContainer = value;
  }-*/;

  native static Image PictureWidgetViewImpl_Image_imageElement(PictureWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::imageElement;
  }-*/;

  native static void PictureWidgetViewImpl_Image_imageElement(PictureWidgetViewImpl instance, Image value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::imageElement = value;
  }-*/;

  native static VideoElement PictureWidgetViewImpl_VideoElement_videoElement(PictureWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::videoElement;
  }-*/;

  native static void PictureWidgetViewImpl_VideoElement_videoElement(PictureWidgetViewImpl instance, VideoElement value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::videoElement = value;
  }-*/;

  native static Element PictureWidgetViewImpl_Element_videoContainer(PictureWidgetViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::videoContainer;
  }-*/;

  native static void PictureWidgetViewImpl_Element_videoContainer(PictureWidgetViewImpl instance, Element value) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::videoContainer = value;
  }-*/;

  public native static void PictureWidgetViewImpl_doInit(PictureWidgetViewImpl instance) /*-{
    instance.@org.kie.workbench.common.forms.common.rendering.client.widgets.picture.widget.PictureWidgetViewImpl::doInit()();
  }-*/;
}