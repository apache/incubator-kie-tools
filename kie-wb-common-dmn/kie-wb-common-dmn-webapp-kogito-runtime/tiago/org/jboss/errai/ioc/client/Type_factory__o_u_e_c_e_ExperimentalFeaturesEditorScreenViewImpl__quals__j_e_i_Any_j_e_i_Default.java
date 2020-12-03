package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
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
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenView;
import org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl;

public class Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExperimentalFeaturesEditorScreenViewImpl> { public interface o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/experimental/client/editor/ExperimentalFeaturesEditorScreenViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/experimental/client/editor/ExperimentalFeaturesEditorScreenViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExperimentalFeaturesEditorScreenViewImpl.class, "Type_factory__o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExperimentalFeaturesEditorScreenViewImpl.class, Object.class, ExperimentalFeaturesEditorScreenView.class, UberElemental.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.elemental2.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImplTemplateResource) GWT.create(o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public ExperimentalFeaturesEditorScreenViewImpl createInstance(final ContextManager contextManager) {
    final ExperimentalFeaturesEditorScreenViewImpl instance = new ExperimentalFeaturesEditorScreenViewImpl();
    setIncompleteInstance(instance);
    final Elemental2DomUtil ExperimentalFeaturesEditorScreenViewImpl_util = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ExperimentalFeaturesEditorScreenViewImpl_util);
    ExperimentalFeaturesEditorScreenViewImpl_Elemental2DomUtil_util(instance, ExperimentalFeaturesEditorScreenViewImpl_util);
    final HTMLDivElement ExperimentalFeaturesEditorScreenViewImpl_container = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, ExperimentalFeaturesEditorScreenViewImpl_container);
    ExperimentalFeaturesEditorScreenViewImpl_HTMLDivElement_container(instance, ExperimentalFeaturesEditorScreenViewImpl_container);
    o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImplTemplateResource templateForExperimentalFeaturesEditorScreenViewImpl = GWT.create(o_u_e_c_e_ExperimentalFeaturesEditorScreenViewImplTemplateResource.class);
    Element parentElementForTemplateOfExperimentalFeaturesEditorScreenViewImpl = TemplateUtil.getRootTemplateParentElement(templateForExperimentalFeaturesEditorScreenViewImpl.getContents().getText(), "org/uberfire/experimental/client/editor/ExperimentalFeaturesEditorScreenViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/experimental/client/editor/ExperimentalFeaturesEditorScreenViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeaturesEditorScreenViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeaturesEditorScreenViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("container", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl", "org/uberfire/experimental/client/editor/ExperimentalFeaturesEditorScreenViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesEditorScreenViewImpl_HTMLDivElement_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ExperimentalFeaturesEditorScreenViewImpl_HTMLDivElement_container(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfExperimentalFeaturesEditorScreenViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExperimentalFeaturesEditorScreenViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExperimentalFeaturesEditorScreenViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Elemental2DomUtil ExperimentalFeaturesEditorScreenViewImpl_Elemental2DomUtil_util(ExperimentalFeaturesEditorScreenViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl::util;
  }-*/;

  native static void ExperimentalFeaturesEditorScreenViewImpl_Elemental2DomUtil_util(ExperimentalFeaturesEditorScreenViewImpl instance, Elemental2DomUtil value) /*-{
    instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl::util = value;
  }-*/;

  native static HTMLDivElement ExperimentalFeaturesEditorScreenViewImpl_HTMLDivElement_container(ExperimentalFeaturesEditorScreenViewImpl instance) /*-{
    return instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl::container;
  }-*/;

  native static void ExperimentalFeaturesEditorScreenViewImpl_HTMLDivElement_container(ExperimentalFeaturesEditorScreenViewImpl instance, HTMLDivElement value) /*-{
    instance.@org.uberfire.experimental.client.editor.ExperimentalFeaturesEditorScreenViewImpl::container = value;
  }-*/;
}