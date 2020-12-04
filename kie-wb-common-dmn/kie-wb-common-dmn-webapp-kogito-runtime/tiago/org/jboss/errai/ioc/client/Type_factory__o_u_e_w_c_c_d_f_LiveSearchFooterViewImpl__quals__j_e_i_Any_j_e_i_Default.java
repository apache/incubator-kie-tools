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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
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
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterView;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl;

public class Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooterViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchFooterViewImpl> { public interface o_u_e_w_c_c_d_f_LiveSearchFooterViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html") public TextResource getContents();
  @Source("org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooterViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LiveSearchFooterViewImpl.class, "Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooterViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LiveSearchFooterViewImpl.class, Object.class, LiveSearchFooterView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_u_e_w_c_c_d_f_LiveSearchFooterViewImplTemplateResource) GWT.create(o_u_e_w_c_c_d_f_LiveSearchFooterViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public LiveSearchFooterViewImpl createInstance(final ContextManager contextManager) {
    final LiveSearchFooterViewImpl instance = new LiveSearchFooterViewImpl();
    setIncompleteInstance(instance);
    final Div LiveSearchFooterViewImpl_container = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchFooterViewImpl_container);
    LiveSearchFooterViewImpl_Div_container(instance, LiveSearchFooterViewImpl_container);
    final Span LiveSearchFooterViewImpl_resetAnchorLabel = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchFooterViewImpl_resetAnchorLabel);
    LiveSearchFooterViewImpl_Span_resetAnchorLabel(instance, LiveSearchFooterViewImpl_resetAnchorLabel);
    final Anchor LiveSearchFooterViewImpl_newEntryAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchFooterViewImpl_newEntryAnchor);
    LiveSearchFooterViewImpl_Anchor_newEntryAnchor(instance, LiveSearchFooterViewImpl_newEntryAnchor);
    final Span LiveSearchFooterViewImpl_newEntryAnchorLabel = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchFooterViewImpl_newEntryAnchorLabel);
    LiveSearchFooterViewImpl_Span_newEntryAnchorLabel(instance, LiveSearchFooterViewImpl_newEntryAnchorLabel);
    final Div LiveSearchFooterViewImpl_footer = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchFooterViewImpl_footer);
    LiveSearchFooterViewImpl_Div_footer(instance, LiveSearchFooterViewImpl_footer);
    final Anchor LiveSearchFooterViewImpl_resetAnchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, LiveSearchFooterViewImpl_resetAnchor);
    LiveSearchFooterViewImpl_Anchor_resetAnchor(instance, LiveSearchFooterViewImpl_resetAnchor);
    o_u_e_w_c_c_d_f_LiveSearchFooterViewImplTemplateResource templateForLiveSearchFooterViewImpl = GWT.create(o_u_e_w_c_c_d_f_LiveSearchFooterViewImplTemplateResource.class);
    Element parentElementForTemplateOfLiveSearchFooterViewImpl = TemplateUtil.getRootTemplateParentElement(templateForLiveSearchFooterViewImpl.getContents().getText(), "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchFooterViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchFooterViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("footer", new DataFieldMeta());
    dataFieldMetas.put("newEntryAnchor", new DataFieldMeta());
    dataFieldMetas.put("newEntryAnchorLabel", new DataFieldMeta());
    dataFieldMetas.put("resetAnchor", new DataFieldMeta());
    dataFieldMetas.put("resetAnchorLabel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Div_container(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Div_footer(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "footer");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Anchor_newEntryAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "newEntryAnchor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Span_newEntryAnchorLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "newEntryAnchorLabel");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Anchor_resetAnchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "resetAnchor");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl", "org/uberfire/ext/widgets/common/client/dropdown/footer/LiveSearchFooterViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Span_resetAnchorLabel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "resetAnchorLabel");
    templateFieldsMap.put("container", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Div_container(instance))));
    templateFieldsMap.put("footer", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Div_footer(instance))));
    templateFieldsMap.put("newEntryAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Anchor_newEntryAnchor(instance))));
    templateFieldsMap.put("newEntryAnchorLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Span_newEntryAnchorLabel(instance))));
    templateFieldsMap.put("resetAnchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Anchor_resetAnchor(instance))));
    templateFieldsMap.put("resetAnchorLabel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(LiveSearchFooterViewImpl_Span_resetAnchorLabel(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfLiveSearchFooterViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("newEntryAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onNewEntryPresed(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("resetAnchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onResetPressed(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LiveSearchFooterViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final LiveSearchFooterViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Anchor LiveSearchFooterViewImpl_Anchor_resetAnchor(LiveSearchFooterViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::resetAnchor;
  }-*/;

  native static void LiveSearchFooterViewImpl_Anchor_resetAnchor(LiveSearchFooterViewImpl instance, Anchor value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::resetAnchor = value;
  }-*/;

  native static Anchor LiveSearchFooterViewImpl_Anchor_newEntryAnchor(LiveSearchFooterViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::newEntryAnchor;
  }-*/;

  native static void LiveSearchFooterViewImpl_Anchor_newEntryAnchor(LiveSearchFooterViewImpl instance, Anchor value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::newEntryAnchor = value;
  }-*/;

  native static Span LiveSearchFooterViewImpl_Span_newEntryAnchorLabel(LiveSearchFooterViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::newEntryAnchorLabel;
  }-*/;

  native static void LiveSearchFooterViewImpl_Span_newEntryAnchorLabel(LiveSearchFooterViewImpl instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::newEntryAnchorLabel = value;
  }-*/;

  native static Span LiveSearchFooterViewImpl_Span_resetAnchorLabel(LiveSearchFooterViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::resetAnchorLabel;
  }-*/;

  native static void LiveSearchFooterViewImpl_Span_resetAnchorLabel(LiveSearchFooterViewImpl instance, Span value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::resetAnchorLabel = value;
  }-*/;

  native static Div LiveSearchFooterViewImpl_Div_footer(LiveSearchFooterViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::footer;
  }-*/;

  native static void LiveSearchFooterViewImpl_Div_footer(LiveSearchFooterViewImpl instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::footer = value;
  }-*/;

  native static Div LiveSearchFooterViewImpl_Div_container(LiveSearchFooterViewImpl instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::container;
  }-*/;

  native static void LiveSearchFooterViewImpl_Div_container(LiveSearchFooterViewImpl instance, Div value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl::container = value;
  }-*/;
}