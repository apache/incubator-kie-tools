package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.Document;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLUListElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
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
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupView;
import org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl;

public class Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesGroupViewImpl> { public interface o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeaturesGroupViewImpl.class, "Type_factory__o_u_e_c_e_g_ExperimentalFeaturesGroupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesGroupViewImpl.class, Object.class, ExperimentalFeaturesGroupView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource) GWT.create(o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public ExperimentalFeaturesGroupViewImpl createInstance(final ContextManager contextManager) {
    final ExperimentalFeaturesGroupViewImpl instance = new ExperimentalFeaturesGroupViewImpl();
    setIncompleteInstance(instance);
    final HTMLDocument ExperimentalFeaturesGroupViewImpl_document = (HTMLDocument) contextManager.getInstance("Producer_factory__e_d_HTMLDocument__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_document);
    ExperimentalFeaturesGroupViewImpl_Document_document(instance, ExperimentalFeaturesGroupViewImpl_document);
    final HTMLUListElement ExperimentalFeaturesGroupViewImpl_featuresContainer = (HTMLUListElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLUListElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_featuresContainer);
    ExperimentalFeaturesGroupViewImpl_HTMLUListElement_featuresContainer(instance, ExperimentalFeaturesGroupViewImpl_featuresContainer);
    final HTMLDivElement ExperimentalFeaturesGroupViewImpl_panel = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_panel);
    ExperimentalFeaturesGroupViewImpl_HTMLDivElement_panel(instance, ExperimentalFeaturesGroupViewImpl_panel);
    final Elemental2DomUtil ExperimentalFeaturesGroupViewImpl_util = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_util);
    ExperimentalFeaturesGroupViewImpl_Elemental2DomUtil_util(instance, ExperimentalFeaturesGroupViewImpl_util);
    final HTMLElement ExperimentalFeaturesGroupViewImpl_caret = (HTMLElement) contextManager.getContextualInstance("ContextualProvider_factory__e_d_HTMLElement__quals__Universal", new Class[] { }, new Annotation[] { new Named() {
        public Class annotationType() {
          return Named.class;
        }
        public String toString() {
          return "@javax.inject.Named(value=span)";
        }
        public String value() {
          return "span";
        }
    } });
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_caret);
    ExperimentalFeaturesGroupViewImpl_HTMLElement_caret(instance, ExperimentalFeaturesGroupViewImpl_caret);
    final HTMLLabelElement ExperimentalFeaturesGroupViewImpl_header = (HTMLLabelElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLLabelElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_header);
    ExperimentalFeaturesGroupViewImpl_HTMLLabelElement_header(instance, ExperimentalFeaturesGroupViewImpl_header);
    final HTMLAnchorElement ExperimentalFeaturesGroupViewImpl_enableAll = (HTMLAnchorElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLAnchorElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeaturesGroupViewImpl_enableAll);
    ExperimentalFeaturesGroupViewImpl_HTMLAnchorElement_enableAll(instance, ExperimentalFeaturesGroupViewImpl_enableAll);
    o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource templateForExperimentalFeaturesGroupViewImpl = GWT.create(o_u_e_c_e_g_ExperimentalFeaturesGroupViewImplTemplateResource.class);
    Element parentElementForTemplateOfExperimentalFeaturesGroupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForExperimentalFeaturesGroupViewImpl.getContents().getText(), "org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeaturesGroupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeaturesGroupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(5);
    dataFieldMetas.put("panel", new DataFieldMeta());
    dataFieldMetas.put("caret", new DataFieldMeta());
    dataFieldMetas.put("header", new DataFieldMeta());
    dataFieldMetas.put("featuresContainer", new DataFieldMeta());
    dataFieldMetas.put("enableAll", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl", "org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLDivElement_panel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "panel");
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl", "org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLElement_caret(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "caret");
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl", "org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLLabelElement_header(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "header");
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl", "org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLUListElement_featuresContainer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "featuresContainer");
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl", "org/uberfire/experimental/client/editor/group/ExperimentalFeaturesGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLAnchorElement_enableAll(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "enableAll");
    templateFieldsMap.put("panel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLDivElement_panel(instance))));
    templateFieldsMap.put("caret", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLElement_caret(instance))));
    templateFieldsMap.put("header", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLLabelElement_header(instance))));
    templateFieldsMap.put("featuresContainer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLUListElement_featuresContainer(instance))));
    templateFieldsMap.put("enableAll", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesGroupViewImpl_HTMLAnchorElement_enableAll(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeaturesGroupViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("header"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onExpand(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("enableAll"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onEnableAll(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExperimentalFeaturesGroupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExperimentalFeaturesGroupViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  public void invokePostConstructs(final ExperimentalFeaturesGroupViewImpl instance) {
    instance.init();
  }

  native static HTMLLabelElement ExperimentalFeaturesGroupViewImpl_HTMLLabelElement_header(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::header;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_HTMLLabelElement_header(ExperimentalFeaturesGroupViewImpl instance, HTMLLabelElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::header = value;
  }-*/;

  native static HTMLUListElement ExperimentalFeaturesGroupViewImpl_HTMLUListElement_featuresContainer(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::featuresContainer;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_HTMLUListElement_featuresContainer(ExperimentalFeaturesGroupViewImpl instance, HTMLUListElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::featuresContainer = value;
  }-*/;

  native static Elemental2DomUtil ExperimentalFeaturesGroupViewImpl_Elemental2DomUtil_util(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::util;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_Elemental2DomUtil_util(ExperimentalFeaturesGroupViewImpl instance, Elemental2DomUtil value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::util = value;
  }-*/;

  native static Document ExperimentalFeaturesGroupViewImpl_Document_document(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::document;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_Document_document(ExperimentalFeaturesGroupViewImpl instance, Document value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::document = value;
  }-*/;

  native static HTMLDivElement ExperimentalFeaturesGroupViewImpl_HTMLDivElement_panel(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::panel;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_HTMLDivElement_panel(ExperimentalFeaturesGroupViewImpl instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::panel = value;
  }-*/;

  native static HTMLElement ExperimentalFeaturesGroupViewImpl_HTMLElement_caret(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::caret;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_HTMLElement_caret(ExperimentalFeaturesGroupViewImpl instance, HTMLElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::caret = value;
  }-*/;

  native static HTMLAnchorElement ExperimentalFeaturesGroupViewImpl_HTMLAnchorElement_enableAll(ExperimentalFeaturesGroupViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::enableAll;
  }-*/;

  native static void ExperimentalFeaturesGroupViewImpl_HTMLAnchorElement_enableAll(ExperimentalFeaturesGroupViewImpl instance, HTMLAnchorElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.group.ExperimentalFeaturesGroupViewImpl::enableAll = value;
  }-*/;
}