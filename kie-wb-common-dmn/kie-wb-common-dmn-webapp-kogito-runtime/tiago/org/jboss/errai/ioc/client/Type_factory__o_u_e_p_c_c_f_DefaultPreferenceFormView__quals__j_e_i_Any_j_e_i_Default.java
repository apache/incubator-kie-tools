package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceForm.View;
import org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormView;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;

public class Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormView__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultPreferenceFormView> { public interface o_u_e_p_c_c_f_DefaultPreferenceFormViewTemplateResource extends Template, ClientBundle { @Source("org/uberfire/ext/preferences/client/central/form/DefaultPreferenceFormView.html") public TextResource getContents(); }
  public Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultPreferenceFormView.class, "Type_factory__o_u_e_p_c_c_f_DefaultPreferenceFormView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultPreferenceFormView.class, Object.class, View.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public DefaultPreferenceFormView createInstance(final ContextManager contextManager) {
    final DefaultPreferenceFormView instance = new DefaultPreferenceFormView();
    setIncompleteInstance(instance);
    final PropertyEditorWidget DefaultPreferenceFormView_propertiesEditorWidget = (PropertyEditorWidget) contextManager.getInstance("Type_factory__o_u_e_p_e_c_PropertyEditorWidget__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, DefaultPreferenceFormView_propertiesEditorWidget);
    DefaultPreferenceFormView_PropertyEditorWidget_propertiesEditorWidget(instance, DefaultPreferenceFormView_propertiesEditorWidget);
    o_u_e_p_c_c_f_DefaultPreferenceFormViewTemplateResource templateForDefaultPreferenceFormView = GWT.create(o_u_e_p_c_c_f_DefaultPreferenceFormViewTemplateResource.class);
    Element parentElementForTemplateOfDefaultPreferenceFormView = TemplateUtil.getRootTemplateParentElement(templateForDefaultPreferenceFormView.getContents().getText(), "org/uberfire/ext/preferences/client/central/form/DefaultPreferenceFormView.html", "");
    TemplateUtil.translateTemplate("org/uberfire/ext/preferences/client/central/form/DefaultPreferenceFormView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultPreferenceFormView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultPreferenceFormView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(1);
    dataFieldMetas.put("properties-editor", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormView", "org/uberfire/ext/preferences/client/central/form/DefaultPreferenceFormView.html", new Supplier<Widget>() {
      public Widget get() {
        return DefaultPreferenceFormView_PropertyEditorWidget_propertiesEditorWidget(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "properties-editor");
    templateFieldsMap.put("properties-editor", DefaultPreferenceFormView_PropertyEditorWidget_propertiesEditorWidget(instance).asWidget());
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDefaultPreferenceFormView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DefaultPreferenceFormView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultPreferenceFormView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static PropertyEditorWidget DefaultPreferenceFormView_PropertyEditorWidget_propertiesEditorWidget(DefaultPreferenceFormView instance) /*-{
    return instance.@org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormView::propertiesEditorWidget;
  }-*/;

  native static void DefaultPreferenceFormView_PropertyEditorWidget_propertiesEditorWidget(DefaultPreferenceFormView instance, PropertyEditorWidget value) /*-{
    instance.@org.uberfire.ext.preferences.client.central.form.DefaultPreferenceFormView::propertiesEditorWidget = value;
  }-*/;
}