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
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateStyleSheet;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequiredView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.labels.required.FieldRequiredViewImpl;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElement;

public class Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRequiredViewImpl> { public interface o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImplTemplateResource extends Template, TemplateStyleSheet, ClientBundle { @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/required/FieldRequiredViewImpl.html") public TextResource getContents();
  @Source("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/required/FieldRequiredViewImpl.css") @NotStrict public CssResource getStyle(); }
  public Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldRequiredViewImpl.class, "Type_factory__o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldRequiredViewImpl.class, Object.class, FieldRequiredView.class, UberElement.class, IsElement.class, HasPresenter.class, org.jboss.errai.ui.client.local.api.IsElement.class });
  }

  public void init(final Context context) {
    ((o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImplTemplateResource) GWT.create(o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImplTemplateResource.class)).getStyle().ensureInjected();
  }

  public FieldRequiredViewImpl createInstance(final ContextManager contextManager) {
    final FieldRequiredViewImpl instance = new FieldRequiredViewImpl();
    setIncompleteInstance(instance);
    o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImplTemplateResource templateForFieldRequiredViewImpl = GWT.create(o_k_w_c_f_d_c_r_f_l_r_FieldRequiredViewImplTemplateResource.class);
    Element parentElementForTemplateOfFieldRequiredViewImpl = TemplateUtil.getRootTemplateParentElement(templateForFieldRequiredViewImpl.getContents().getText(), "org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/required/FieldRequiredViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/forms/dynamic/client/rendering/formGroups/labels/required/FieldRequiredViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldRequiredViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldRequiredViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfFieldRequiredViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FieldRequiredViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FieldRequiredViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }
}