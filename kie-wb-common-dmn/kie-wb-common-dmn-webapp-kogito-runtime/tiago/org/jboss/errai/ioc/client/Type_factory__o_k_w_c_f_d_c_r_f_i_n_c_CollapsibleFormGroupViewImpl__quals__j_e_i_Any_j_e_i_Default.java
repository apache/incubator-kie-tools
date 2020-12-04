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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.FormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelp;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequired;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.impl.FormsElementWrapperWidgetUtilImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CollapsibleFormGroupViewImpl> { public interface o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CollapsibleFormGroupViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CollapsibleFormGroupViewImpl.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, CollapsibleFormGroupView.class, FormGroupView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource) GWT.create(o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public CollapsibleFormGroupViewImpl createInstance(final ContextManager contextManager) {
    final CollapsibleFormGroupViewImpl instance = new CollapsibleFormGroupViewImpl();
    setIncompleteInstance(instance);
    final Div CollapsibleFormGroupViewImpl_formGroup = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_formGroup);
    CollapsibleFormGroupViewImpl_Div_formGroup(instance, CollapsibleFormGroupViewImpl_formGroup);
    final Anchor CollapsibleFormGroupViewImpl_anchor = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_anchor);
    CollapsibleFormGroupViewImpl_Anchor_anchor(instance, CollapsibleFormGroupViewImpl_anchor);
    final Div CollapsibleFormGroupViewImpl_panel = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_panel);
    CollapsibleFormGroupViewImpl_Div_panel(instance, CollapsibleFormGroupViewImpl_panel);
    final Div CollapsibleFormGroupViewImpl_helpBlock = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_helpBlock);
    CollapsibleFormGroupViewImpl_Div_helpBlock(instance, CollapsibleFormGroupViewImpl_helpBlock);
    final FieldHelp CollapsibleFormGroupViewImpl_fieldHelp = (FieldHelp) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelp__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_fieldHelp);
    CollapsibleFormGroupViewImpl_FieldHelp_fieldHelp(instance, CollapsibleFormGroupViewImpl_fieldHelp);
    final FormsElementWrapperWidgetUtilImpl CollapsibleFormGroupViewImpl_wrapperWidgetUtil = (FormsElementWrapperWidgetUtilImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_u_i_FormsElementWrapperWidgetUtilImpl__quals__j_e_i_Any_j_e_i_Default");
    CollapsibleFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(instance, CollapsibleFormGroupViewImpl_wrapperWidgetUtil);
    final Span CollapsibleFormGroupViewImpl_anchorText = (Span) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Span__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_anchorText);
    CollapsibleFormGroupViewImpl_Span_anchorText(instance, CollapsibleFormGroupViewImpl_anchorText);
    final FieldRequired CollapsibleFormGroupViewImpl_fieldRequired = (FieldRequired) contextManager.getInstance("Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequired__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, CollapsibleFormGroupViewImpl_fieldRequired);
    CollapsibleFormGroupViewImpl_FieldRequired_fieldRequired(instance, CollapsibleFormGroupViewImpl_fieldRequired);
    o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource templateForCollapsibleFormGroupViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_i_n_c_CollapsibleFormGroupViewImplTemplateResource.class);
    Element parentElementForTemplateOfCollapsibleFormGroupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForCollapsibleFormGroupViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCollapsibleFormGroupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCollapsibleFormGroupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(6);
    dataFieldMetas.put("anchor", new DataFieldMeta());
    dataFieldMetas.put("anchorText", new DataFieldMeta());
    dataFieldMetas.put("panel", new DataFieldMeta());
    dataFieldMetas.put("container", new DataFieldMeta());
    dataFieldMetas.put("formGroup", new DataFieldMeta());
    dataFieldMetas.put("helpBlock", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Anchor_anchor(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "anchor");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Span_anchorText(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "anchorText");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Div_panel(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "panel");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return CollapsibleFormGroupViewImpl_SimplePanel_container(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "container");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Div_formGroup(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "formGroup");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/impl/nestedForm/collapse/CollapsibleFormGroupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Div_helpBlock(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "helpBlock");
    templateFieldsMap.put("anchor", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Anchor_anchor(instance))));
    templateFieldsMap.put("anchorText", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Span_anchorText(instance))));
    templateFieldsMap.put("panel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Div_panel(instance))));
    templateFieldsMap.put("container", CollapsibleFormGroupViewImpl_SimplePanel_container(instance).asWidget());
    templateFieldsMap.put("formGroup", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Div_formGroup(instance))));
    templateFieldsMap.put("helpBlock", ElementWrapperWidget.getWidget(TemplateUtil.asElement(CollapsibleFormGroupViewImpl_Div_helpBlock(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfCollapsibleFormGroupViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("anchor"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClick(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((CollapsibleFormGroupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final CollapsibleFormGroupViewImpl instance, final ContextManager contextManager) {
    instance.destroy();
    TemplateUtil.cleanupTemplated(instance);
  }

  native static FieldRequired CollapsibleFormGroupViewImpl_FieldRequired_fieldRequired(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::fieldRequired;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_FieldRequired_fieldRequired(CollapsibleFormGroupViewImpl instance, FieldRequired value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::fieldRequired = value;
  }-*/;

  native static Anchor CollapsibleFormGroupViewImpl_Anchor_anchor(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::anchor;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_Anchor_anchor(CollapsibleFormGroupViewImpl instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::anchor = value;
  }-*/;

  native static SimplePanel CollapsibleFormGroupViewImpl_SimplePanel_container(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::container;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_SimplePanel_container(CollapsibleFormGroupViewImpl instance, SimplePanel value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::container = value;
  }-*/;

  native static FormsElementWrapperWidgetUtil CollapsibleFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::wrapperWidgetUtil;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_FormsElementWrapperWidgetUtil_wrapperWidgetUtil(CollapsibleFormGroupViewImpl instance, FormsElementWrapperWidgetUtil value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::wrapperWidgetUtil = value;
  }-*/;

  native static Div CollapsibleFormGroupViewImpl_Div_helpBlock(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::helpBlock;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_Div_helpBlock(CollapsibleFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::helpBlock = value;
  }-*/;

  native static Span CollapsibleFormGroupViewImpl_Span_anchorText(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::anchorText;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_Span_anchorText(CollapsibleFormGroupViewImpl instance, Span value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::anchorText = value;
  }-*/;

  native static Div CollapsibleFormGroupViewImpl_Div_panel(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::panel;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_Div_panel(CollapsibleFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::panel = value;
  }-*/;

  native static FieldHelp CollapsibleFormGroupViewImpl_FieldHelp_fieldHelp(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::fieldHelp;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_FieldHelp_fieldHelp(CollapsibleFormGroupViewImpl instance, FieldHelp value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::fieldHelp = value;
  }-*/;

  native static Div CollapsibleFormGroupViewImpl_Div_formGroup(CollapsibleFormGroupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::formGroup;
  }-*/;

  native static void CollapsibleFormGroupViewImpl_Div_formGroup(CollapsibleFormGroupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.nestedForm.collapse.CollapsibleFormGroupViewImpl::formGroup = value;
  }-*/;
}