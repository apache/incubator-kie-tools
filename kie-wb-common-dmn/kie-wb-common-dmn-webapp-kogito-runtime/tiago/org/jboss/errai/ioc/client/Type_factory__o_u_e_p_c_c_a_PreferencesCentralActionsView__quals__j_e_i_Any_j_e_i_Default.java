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
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsScreen.View;
import org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView;
import org.uberfire.ext.preferences.client.central.hierarchy.HierarchyStructureView;

public class Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsView__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesCentralActionsView> { public interface o_u_e_p_c_c_a_PreferencesCentralActionsViewTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/uberfire/ext/preferences/client/central/actions/PreferencesCentralActionsView.html") public TextResource getContents();
  @Source("org/uberfire/ext/preferences/client/central/actions/PreferencesCentralActionsView.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesCentralActionsView.class, "Type_factory__o_u_e_p_c_c_a_PreferencesCentralActionsView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesCentralActionsView.class, Object.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class, View.class, HierarchyStructureView.class, UberElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    ((o_u_e_p_c_c_a_PreferencesCentralActionsViewTemplateResource) GWT.create(o_u_e_p_c_c_a_PreferencesCentralActionsViewTemplateResource.class)).getStyle().ensureInjected();
  }

  public PreferencesCentralActionsView createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final PreferencesCentralActionsView instance = new PreferencesCentralActionsView(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    final Button PreferencesCentralActionsView_cancelButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, PreferencesCentralActionsView_cancelButton);
    PreferencesCentralActionsView_Button_cancelButton(instance, PreferencesCentralActionsView_cancelButton);
    final Button PreferencesCentralActionsView_saveButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    registerDependentScopedReference(instance, PreferencesCentralActionsView_saveButton);
    PreferencesCentralActionsView_Button_saveButton(instance, PreferencesCentralActionsView_saveButton);
    o_u_e_p_c_c_a_PreferencesCentralActionsViewTemplateResource templateForPreferencesCentralActionsView = GWT.create(o_u_e_p_c_c_a_PreferencesCentralActionsViewTemplateResource.class);
    Element parentElementForTemplateOfPreferencesCentralActionsView = TemplateUtil.getRootTemplateParentElement(templateForPreferencesCentralActionsView.getContents().getText(), "org/uberfire/ext/preferences/client/central/actions/PreferencesCentralActionsView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/central/actions/PreferencesCentralActionsView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreferencesCentralActionsView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreferencesCentralActionsView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("preference-actions-save", new DataFieldMeta());
    dataFieldMetas.put("preference-actions-cancel", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView", "org/uberfire/ext/preferences/client/central/actions/PreferencesCentralActionsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesCentralActionsView_Button_saveButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preference-actions-save");
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView", "org/uberfire/ext/preferences/client/central/actions/PreferencesCentralActionsView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesCentralActionsView_Button_cancelButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "preference-actions-cancel");
    templateFieldsMap.put("preference-actions-save", ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesCentralActionsView_Button_saveButton(instance))));
    templateFieldsMap.put("preference-actions-cancel", ElementWrapperWidget.getWidget(TemplateUtil.asElement(PreferencesCentralActionsView_Button_cancelButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfPreferencesCentralActionsView), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-actions-cancel"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.undo(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("preference-actions-save"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.save(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PreferencesCentralActionsView) instance, contextManager);
  }

  public void destroyInstanceHelper(final PreferencesCentralActionsView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Button PreferencesCentralActionsView_Button_cancelButton(PreferencesCentralActionsView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView::cancelButton;
  }-*/;

  native static void PreferencesCentralActionsView_Button_cancelButton(PreferencesCentralActionsView instance, Button value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView::cancelButton = value;
  }-*/;

  native static Button PreferencesCentralActionsView_Button_saveButton(PreferencesCentralActionsView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView::saveButton;
  }-*/;

  native static void PreferencesCentralActionsView_Button_saveButton(PreferencesCentralActionsView instance, Button value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.actions.PreferencesCentralActionsView::saveButton = value;
  }-*/;
}