package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
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
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget;

public class Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerWidget__quals__j_e_i_Any_j_e_i_Default extends Factory<ColorPickerWidget> { public interface o_k_w_c_s_f_c_f_c_ColorPickerWidgetTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/stunner/forms/client/fields/colorPicker/ColorPickerWidget.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerWidget__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ColorPickerWidget.class, "Type_factory__o_k_w_c_s_f_c_f_c_ColorPickerWidget__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ColorPickerWidget.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class });
  }

  public ColorPickerWidget createInstance(final ContextManager contextManager) {
    final ColorPickerWidget instance = new ColorPickerWidget();
    setIncompleteInstance(instance);
    final Button ColorPickerWidget_colorButton = (Button) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_Button__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ColorPickerWidget_colorButton);
    ColorPickerWidget_Button_colorButton(instance, ColorPickerWidget_colorButton);
    final TextBox ColorPickerWidget_colorTextBox = (TextBox) contextManager.getInstance("ExtensionProvided_factory__o_g_c_u_TextBox__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, ColorPickerWidget_colorTextBox);
    ColorPickerWidget_TextBox_colorTextBox(instance, ColorPickerWidget_colorTextBox);
    o_k_w_c_s_f_c_f_c_ColorPickerWidgetTemplateResource templateForColorPickerWidget = GWT.create(o_k_w_c_s_f_c_f_c_ColorPickerWidgetTemplateResource.class);
    Element parentElementForTemplateOfColorPickerWidget = TemplateUtil.getRootTemplateParentElement(templateForColorPickerWidget.getContents().getText(), "org/kie/workbench/common/stunner/forms/client/fields/colorPicker/ColorPickerWidget.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/stunner/forms/client/fields/colorPicker/ColorPickerWidget.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfColorPickerWidget));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfColorPickerWidget));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("colorButton", new DataFieldMeta());
    dataFieldMetas.put("colorTextBox", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget", "org/kie/workbench/common/stunner/forms/client/fields/colorPicker/ColorPickerWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ColorPickerWidget_Button_colorButton(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "colorButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget", "org/kie/workbench/common/stunner/forms/client/fields/colorPicker/ColorPickerWidget.html", new Supplier<Widget>() {
      public Widget get() {
        return ColorPickerWidget_TextBox_colorTextBox(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "colorTextBox");
    templateFieldsMap.put("colorButton", ColorPickerWidget_Button_colorButton(instance).asWidget());
    templateFieldsMap.put("colorTextBox", ColorPickerWidget_TextBox_colorTextBox(instance).asWidget());
    TemplateUtil.initWidget(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfColorPickerWidget), templateFieldsMap.values());
    ((HasClickHandlers) templateFieldsMap.get("colorTextBox")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickColorTextBox(event);
      }
    });
    ((HasClickHandlers) templateFieldsMap.get("colorButton")).addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.onClickColorButton(event);
      }
    });
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ColorPickerWidget) instance, contextManager);
  }

  public void destroyInstanceHelper(final ColorPickerWidget instance, final ContextManager contextManager) {
    TemplateUtil.cleanupWidget(instance);
  }

  native static Button ColorPickerWidget_Button_colorButton(ColorPickerWidget instance) /*-{
    return instance.@org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget::colorButton;
  }-*/;

  native static void ColorPickerWidget_Button_colorButton(ColorPickerWidget instance, Button value) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget::colorButton = value;
  }-*/;

  native static TextBox ColorPickerWidget_TextBox_colorTextBox(ColorPickerWidget instance) /*-{
    return instance.@org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget::colorTextBox;
  }-*/;

  native static void ColorPickerWidget_TextBox_colorTextBox(ColorPickerWidget instance, TextBox value) /*-{
    instance.@org.kie.workbench.common.stunner.forms.client.fields.colorPicker.ColorPickerWidget::colorTextBox = value;
  }-*/;
}