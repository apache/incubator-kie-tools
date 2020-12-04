package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.guvnor.messageconsole.client.console.widget.MessageTableWidget;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupView;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_w_c_p_v_ValidationPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ValidationPopupViewImpl> { public interface o_k_w_c_w_c_p_v_ValidationPopupViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_w_c_p_v_ValidationPopupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ValidationPopupViewImpl.class, "Type_factory__o_k_w_c_w_c_p_v_ValidationPopupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ValidationPopupViewImpl.class, Object.class, ValidationPopupView.class, UberElement.class, IsElement.class, HasPresenter.class });
  }

  public ValidationPopupViewImpl createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final Div _view_0 = (Div) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Div__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Button _cancelButton_2 = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Button _yesButton_1 = (Button) contextManager.getInstance("ExtensionProvided_factory__o_j_e_c_c_d_Button__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ValidationPopupViewImpl instance = new ValidationPopupViewImpl(_view_0, _yesButton_1, _cancelButton_2, _translationService_3);
    registerDependentScopedReference(instance, _translationService_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _cancelButton_2);
    registerDependentScopedReference(instance, _yesButton_1);
    setIncompleteInstance(instance);
    o_k_w_c_w_c_p_v_ValidationPopupViewImplTemplateResource templateForValidationPopupViewImpl = GWT.create(o_k_w_c_w_c_p_v_ValidationPopupViewImplTemplateResource.class);
    Element parentElementForTemplateOfValidationPopupViewImpl = TemplateUtil.getRootTemplateParentElement(templateForValidationPopupViewImpl.getContents().getText(), "org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfValidationPopupViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfValidationPopupViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(4);
    dataFieldMetas.put("view", new DataFieldMeta());
    dataFieldMetas.put("validationTable", new DataFieldMeta());
    dataFieldMetas.put("yesButton", new DataFieldMeta());
    dataFieldMetas.put("cancelButton", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl", "org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValidationPopupViewImpl_Div_view(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "view");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl", "org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ValidationPopupViewImpl_MessageTableWidget_validationTable(instance).asWidget();
      }
    }, dataFieldElements, dataFieldMetas, "validationTable");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl", "org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValidationPopupViewImpl_Button_yesButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "yesButton");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl", "org/kie/workbench/common/widgets/client/popups/validation/ValidationPopupViewImpl.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValidationPopupViewImpl_Button_cancelButton(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "cancelButton");
    templateFieldsMap.put("view", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValidationPopupViewImpl_Div_view(instance))));
    templateFieldsMap.put("validationTable", ValidationPopupViewImpl_MessageTableWidget_validationTable(instance).asWidget());
    templateFieldsMap.put("yesButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValidationPopupViewImpl_Button_yesButton(instance))));
    templateFieldsMap.put("cancelButton", ElementWrapperWidget.getWidget(TemplateUtil.asElement(ValidationPopupViewImpl_Button_cancelButton(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfValidationPopupViewImpl), templateFieldsMap.values());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("cancelButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.cancelButtonClicked(event);
      }
    }, ClickEvent.getType());
    TemplateUtil.setupWrappedElementEventHandler(templateFieldsMap.get("yesButton"), new ClickHandler() {
      public void onClick(ClickEvent event) {
        instance.yesButtonClicked(event);
      }
    }, ClickEvent.getType());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ValidationPopupViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ValidationPopupViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static Div ValidationPopupViewImpl_Div_view(ValidationPopupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::view;
  }-*/;

  native static void ValidationPopupViewImpl_Div_view(ValidationPopupViewImpl instance, Div value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::view = value;
  }-*/;

  native static MessageTableWidget ValidationPopupViewImpl_MessageTableWidget_validationTable(ValidationPopupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::validationTable;
  }-*/;

  native static void ValidationPopupViewImpl_MessageTableWidget_validationTable(ValidationPopupViewImpl instance, MessageTableWidget<ValidationMessage> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::validationTable = value;
  }-*/;

  native static Button ValidationPopupViewImpl_Button_cancelButton(ValidationPopupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::cancelButton;
  }-*/;

  native static void ValidationPopupViewImpl_Button_cancelButton(ValidationPopupViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::cancelButton = value;
  }-*/;

  native static Button ValidationPopupViewImpl_Button_yesButton(ValidationPopupViewImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::yesButton;
  }-*/;

  native static void ValidationPopupViewImpl_Button_yesButton(ValidationPopupViewImpl instance, Button value) /*-{
    instance.@org.kie.workbench.common.widgets.client.popups.validation.ValidationPopupViewImpl::yesButton = value;
  }-*/;
}