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
import org.jboss.errai.common.client.dom.Anchor;
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
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.views.pfly.widgets.JQueryProducer.JQuery;
import org.uberfire.client.views.pfly.widgets.Popover;

public class Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldHelpViewImpl> { public interface o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/help/FieldHelpViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/help/FieldHelpViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldHelpViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldHelpViewImpl.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, FieldHelpView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource) GWT.create(o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public FieldHelpViewImpl createInstance(final ContextManager contextManager) {
    final FieldHelpViewImpl instance = new FieldHelpViewImpl();
    setIncompleteInstance(instance);
    final Anchor FieldHelpViewImpl_helpMessage = (Anchor) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Anchor__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, FieldHelpViewImpl_helpMessage);
    FieldHelpViewImpl_Anchor_helpMessage(instance, FieldHelpViewImpl_helpMessage);
    final JQuery FieldHelpViewImpl_jQueryPopover = (JQuery) contextManager.getInstance("Producer_factory__o_u_c_v_p_w_JQueryProducer_JQuery__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, FieldHelpViewImpl_jQueryPopover);
    FieldHelpViewImpl_JQuery_jQueryPopover(instance, FieldHelpViewImpl_jQueryPopover);
    o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource templateForFieldHelpViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_l_h_FieldHelpViewImplTemplateResource.class);
    Element parentElementForTemplateOfFieldHelpViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFieldHelpViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/help/FieldHelpViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/help/FieldHelpViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldHelpViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldHelpViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("helpMessage", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl", "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/help/FieldHelpViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldHelpViewImpl_Anchor_helpMessage(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "helpMessage");
    templateFieldsMap.put("helpMessage", ElementWrapperWidget.getWidget(TemplateUtil.asElement(FieldHelpViewImpl_Anchor_helpMessage(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldHelpViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FieldHelpViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FieldHelpViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Anchor FieldHelpViewImpl_Anchor_helpMessage(FieldHelpViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl::helpMessage;
  }-*/;

  native static void FieldHelpViewImpl_Anchor_helpMessage(FieldHelpViewImpl instance, Anchor value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl::helpMessage = value;
  }-*/;

  native static JQuery FieldHelpViewImpl_JQuery_jQueryPopover(FieldHelpViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl::jQueryPopover;
  }-*/;

  native static void FieldHelpViewImpl_JQuery_jQueryPopover(FieldHelpViewImpl instance, JQuery<Popover> value) /*-{
    instance.@org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.help.FieldHelpViewImpl::jQueryPopover = value;
  }-*/;
}