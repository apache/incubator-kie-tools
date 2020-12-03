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
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorDividerItemView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorDividerItemViewImpl;

public class Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ListSelectorDividerItemViewImpl> { public interface o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImplTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorDividerItemViewImpl.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListSelectorDividerItemViewImpl.class, "Type_factory__o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListSelectorDividerItemViewImpl.class, Object.class, ListSelectorDividerItemView.class, IsElement.class, org.jboss.errai.common.client.api.IsElement.class });
  }

  public ListSelectorDividerItemViewImpl createInstance(final ContextManager contextManager) {
    final ListSelectorDividerItemViewImpl instance = new ListSelectorDividerItemViewImpl();
    setIncompleteInstance(instance);
    o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImplTemplateResource templateForListSelectorDividerItemViewImpl = GWT.create(o_k_w_c_d_c_w_g_c_l_ListSelectorDividerItemViewImplTemplateResource.class);
    Element parentElementForTemplateOfListSelectorDividerItemViewImpl = TemplateUtil.getRootTemplateParentElement(templateForListSelectorDividerItemViewImpl.getContents().getText(), "org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorDividerItemViewImpl.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/widgets/grid/controls/list/ListSelectorDividerItemViewImpl.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorDividerItemViewImpl));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorDividerItemViewImpl));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(0);
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfListSelectorDividerItemViewImpl), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ListSelectorDividerItemViewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ListSelectorDividerItemViewImpl instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }
}