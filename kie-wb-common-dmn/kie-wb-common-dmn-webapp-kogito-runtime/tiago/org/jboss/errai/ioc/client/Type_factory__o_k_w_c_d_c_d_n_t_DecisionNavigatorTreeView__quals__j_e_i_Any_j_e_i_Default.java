package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Widget;
import elemental2.dom.HTMLDivElement;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.elemental2.IsElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.shared.DataFieldMeta;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.style.StyleBindingsRegistry;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreePresenter.View;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView;
import org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView.TreeItem;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberElemental;

public class Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView__quals__j_e_i_Any_j_e_i_Default extends Factory<DecisionNavigatorTreeView> { public interface o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeViewTemplateResource extends Template, ClientBundle { @Source("org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html") public TextResource getContents(); }
  public Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DecisionNavigatorTreeView.class, "Type_factory__o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DecisionNavigatorTreeView.class, Object.class, View.class, UberElemental.class, IsElement.class, HasPresenter.class });
  }

  public void init(final Context context) {
    StyleInjector.inject("/*\n * Copyright 2018 Red Hat, Inc. and/or its affiliates.\n *\n * Licensed under the Apache License, Version 2.0 (the \"License\");\n * you may not use this file except in compliance with the License.\n * You may obtain a copy of the License at\n *\n *       http://www.apache.org/licenses/LICENSE-2.0\n *\n * Unless required by applicable law or agreed to in writing, software\n * distributed under the License is distributed on an \"AS IS\" BASIS,\n * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n * See the License for the specific language governing permissions and\n * limitations under the License.\n */\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] {\n  /* ~ Tree ~ */\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] [data-field=\"items\"] {\n  padding: .5em 1.5em;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul {\n  list-style: none;\n  padding: 0 0 0 1.5em;\n  line-height: 2em;\n  /* ~ Icons ~ */\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li {\n  white-space: nowrap;\n  overflow: hidden;\n  text-overflow: ellipsis;\n  cursor: default;\n  user-select: none;\n  padding-right: 1em;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li div {\n  padding: 0 0.4em;\n  line-height: 1.6em;\n  border-radius: 0.25em;\n  display: inline-block;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li div input[data-field=\"input-text\"] {\n  border: 1px solid #CCC;\n  background: #FFF;\n  border-radius: 3px;\n  border-bottom-color: #AAA;\n  display: none;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li div.selected,\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li div.selected:hover {\n  background: #ddf0f8;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li .fa.fa-check,\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li .fa.fa-pencil,\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li .fa.fa-trash {\n  margin-left: 2px;\n  width: 15px;\n  height: 10px;\n  display: none;\n  cursor: pointer;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li i.fa.fa-trash {\n  margin-top: -1px;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li:hover > div > [data-field=\"text-content\"],\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable:hover > div > [data-field=\"text-content\"] {\n  background: #EFEFEF;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable:hover > .fa.fa-pencil,\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable:hover > .fa.fa-trash {\n  display: inline-block;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable.editing > div > input[data-field=\"input-text\"] {\n  display: inline-block;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable.editing > div > [data-field=\"text-content\"] {\n  display: none;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable.editing > .fa.fa-pencil {\n  display: none;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable.editing > .fa.fa-trash,\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.editable.editing > .fa.fa-check {\n  display: inline-block;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul .parent-node ul {\n  display: block;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul .parent-node.closed ul {\n  display: none;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li:before,\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul [data-field=\"icon\"]:before {\n  font-family: FontAwesome;\n  -webkit-font-smoothing: antialiased;\n  padding-right: 0.5em;\n  position: absolute;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li:before {\n  margin: 0.1em 0 0 -2.25em;\n  cursor: default;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul [data-field=\"icon\"]:before {\n  margin: 0.1em 0 0 -1.5em;\n  cursor: pointer;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.parent-node:before {\n  content: \"\\f0d7\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.parent-node.closed:before {\n  content: \"\\f0da\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-root > div > [data-field=\"icon\"]:before {\n  content: \"\\f1e0\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-item > div > [data-field=\"icon\"]:before {\n  content: \"\\f111\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-context > div > [data-field=\"icon\"]:before {\n  content: \"\\f192\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-decision-table > div > [data-field=\"icon\"]:before {\n  content: \"\\f0ce\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-invocation > div > [data-field=\"icon\"]:before {\n  content: \"\\f0a1\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-list > div > [data-field=\"icon\"]:before {\n  content: \"\\f0ca\";\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-function-definition > div > [data-field=\"icon\"]:before {\n  content: \"f ( )\";\n  font-family: serif;\n  font-style: italic;\n  font-weight: bold;\n  font-size: 1.2em;\n  margin: -0.1em 0 0 -1.8em;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-literal-expression > div > [data-field=\"icon\"]:before {\n  content: \"Σ\";\n  font-family: sans-serif;\n  font-weight: bold;\n  font-size: 1.2em;\n  margin: 0 0 0 -1.15em;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-relation > div > [data-field=\"icon\"]:before {\n  content: \"\\f0c1\";\n  font-size: 1.2em;\n  transform: rotate(-45deg);\n  margin: -0.15em 0 0 -1.45em;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-text-annotation > div > [data-field=\"icon\"]:before {\n  width: 1.8em;\n  height: 1.8em;\n  content: url(\"data:image/svg+xml;base64, PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDIzLjAuNCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA0MjAgNDIwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MjAgNDIwOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+Cgkuc3Qwe2ZpbGw6IzRENTI1ODt9Cjwvc3R5bGU+CjxwYXRoIGlkPSJJY29uIiBjbGFzcz0ic3QwIiBkPSJNNDAsMTIwaDgwVjgwSDQwQzE3LjksODAsMCw5Ny45LDAsMTIwdjE4MGMwLDIyLjEsMTcuOSw0MCw0MCw0MGg4MHYtNDBINDBWMTIweiBNNDIwLDIyMGgtMjB2LTYwCgloLTQwdjQwaC0yMHYyMGgtMjB2LTYwaC00MHY2MGgtMjB2LTQwaC0zOS43djIwSDIwMHYtMjBoLTQwdjIwaC0yMHYyMGgtMjB2LTYwSDgwdjgwaDYwdjIwaDQwdi0yMGgyNDBWMjIweiIvPgo8L3N2Zz4K\");\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-business-knowledge-model > div > [data-field=\"icon\"]:before {\n  width: 1.8em;\n  height: 1.8em;\n  content: url(\"data:image/svg+xml;base64, PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDIxLjEuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA0MjAgNDIwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MjAgNDIwOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+Cgkuc3Qwe2ZpbGw6IzRENTI1ODt9Cjwvc3R5bGU+CjxwYXRoIGlkPSJJY29uXzFfIiBjbGFzcz0ic3QwIiBkPSJNMzgwLDEyMHYxNDMuNEwzNDMuNCwzMDBINDBWMTU2LjZMNzYuNiwxMjBIMzgwIE00MDAsODBINjguM2MtNS4zLDAtMTAuNCwyLjEtMTQuMSw1LjlMNS45LDEzNC4xCglDMi4xLDEzNy45LDAsMTQzLDAsMTQ4LjNWMzIwYzAsMTEsOSwyMCwyMCwyMGgzMzEuN2M1LjMsMCwxMC40LTIuMSwxNC4xLTUuOWw0OC4zLTQ4LjNjMy44LTMuOCw1LjktOC44LDUuOS0xNC4xVjEwMAoJQzQyMCw4OSw0MTEsODAsNDAwLDgwTDQwMCw4MHoiLz4KPC9zdmc+Cg==\");\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-input-data > div > [data-field=\"icon\"]:before {\n  width: 1.8em;\n  height: 1.8em;\n  content: url(\"data:image/svg+xml;base64, PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDIxLjEuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA0MjAgNDIwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MjAgNDIwOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+Cgkuc3Qwe2ZpbGw6IzRENTI1ODt9Cjwvc3R5bGU+CjxwYXRoIGlkPSJJY29uIiBjbGFzcz0ic3QwIiBkPSJNMjkyLDEyMGM0OC41LDAsODgsMzkuNSw4OCw4OHY0YzAsNDguNS0zOS41LDg4LTg4LDg4SDEyOGMtNDguNSwwLTg4LTM5LjUtODgtODh2LTQKCWMwLTQ4LjUsMzkuNS04OCw4OC04OEgyOTIgTTI5Miw4MEgxMjhDNTcuMyw4MCwwLDEzNy4zLDAsMjA4djRjMCw3MC43LDU3LjMsMTI4LDEyOCwxMjhoMTY0YzcwLjcsMCwxMjgtNTcuMywxMjgtMTI4di00CglDNDIwLDEzNy4zLDM2Mi43LDgwLDI5Miw4MEwyOTIsODB6Ii8+Cjwvc3ZnPgo=\");\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-decision-service > div > [data-field=\"icon\"]:before {\n  width: 1.8em;\n  height: 1.8em;\n  content: url(\"data:image/svg+xml;base64, PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDIyLjEuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA0MjAgNDIwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MjAgNDIwOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+Cgkuc3Qwe2ZpbGw6IzRENTI1ODt9Cjwvc3R5bGU+CjxwYXRoIGNsYXNzPSJzdDAiIGQ9Ik0zNTAsODBINzBjLTM4LjcsMC03MCwzMS4zLTcwLDcwdjEyMGMwLDM4LjcsMzEuMyw3MCw3MCw3MGgyODBjMzguNywwLDcwLTMxLjMsNzAtNzBWMTUwCglDNDIwLDExMS4zLDM4OC43LDgwLDM1MCw4MHogTTc4LDExOS45aDI2NGMyMSwwLDM4LDE3LDM4LDM4VjIwMEg0MHYtNDIuMUM0MCwxMzYuOSw1NywxMTkuOSw3OCwxMTkuOXogTTM0MiwyOTkuOUg3OAoJYy0yMSwwLTM4LTE3LTM4LTM4VjIyMGgzNDB2NDEuOUMzODAsMjgyLjksMzYzLDI5OS45LDM0MiwyOTkuOXoiLz4KPC9zdmc+Cg==\");\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-knowledge-source > div > [data-field=\"icon\"]:before {\n  width: 1.8em;\n  height: 1.8em;\n  content: url(\"data:image/svg+xml;base64, PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDIxLjEuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA0MjAgNDIwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MjAgNDIwOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+Cgkuc3Qwe2ZpbGw6IzRENTI1ODt9Cjwvc3R5bGU+CjxwYXRoIGlkPSJJY29uIiBjbGFzcz0ic3QwIiBkPSJNMzgwLjMsMTIwLjF2MTA5LjRjLTI0LjYtOS41LTQ5LjEtMTQuMy03My0xNC4zYy0yMiwwLTQzLjIsNC4xLTYzLjMsMTIuMmMtMjIuNCw5LjEtNDMuMiwyMy4yLTYyLDQyCgljLTIwLjgsMjAuOC00My4yLDMwLjgtNjguNywzMC44Yy0yOS42LDAtNTcuMS0xMy43LTczLTIzLjRWMTIwLjFIMzgwLjMgTTQwMC4zLDgwLjFoLTM4MGMtMTEsMC0yMCw5LTIwLDIwdjE5NS4yCgljMCwxLjUsMC43LDMsMS45LDMuOWM5LjgsNy42LDU2LDQxLDExMS4xLDQxYzMxLjEsMCw2NS4xLTEwLjYsOTctNDIuNmMzMS45LTMxLjksNjUuOS00Mi42LDk3LTQyLjZjNDcuNiwwLDg4LjYsMjQuOSwxMDUuMSwzNi42CgljMC45LDAuNiwxLjksMC45LDIuOSwwLjljMi42LDAsNS0yLjEsNS01VjEwMC4xQzQyMC4zLDg5LDQxMS4zLDgwLjEsNDAwLjMsODAuMUw0MDAuMyw4MC4xeiIvPgo8L3N2Zz4K\");\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-decision > div > [data-field=\"icon\"]:before {\n  width: 1.8em;\n  height: 1.8em;\n  content: url(\"data:image/svg+xml;base64, PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBHZW5lcmF0b3I6IEFkb2JlIElsbHVzdHJhdG9yIDIyLjEuMCwgU1ZHIEV4cG9ydCBQbHVnLUluIC4gU1ZHIFZlcnNpb246IDYuMDAgQnVpbGQgMCkgIC0tPgo8c3ZnIHZlcnNpb249IjEuMSIgaWQ9IkxheWVyXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IgoJIHZpZXdCb3g9IjAgMCA0MjAgNDIwIiBzdHlsZT0iZW5hYmxlLWJhY2tncm91bmQ6bmV3IDAgMCA0MjAgNDIwOyIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+CjxzdHlsZSB0eXBlPSJ0ZXh0L2NzcyI+Cgkuc3Qwe2ZpbGw6IzRENTI1ODt9Cjwvc3R5bGU+CjxwYXRoIGlkPSJJY29uIiBjbGFzcz0ic3QwIiBkPSJNMzk1LDgwSDI1QzExLjIsODAsMCw5MS4yLDAsMTA1djIxMGMwLDEzLjgsMTEuMiwyNSwyNSwyNWgzNzBjMTMuOCwwLDI1LTExLjIsMjUtMjVWMTA1CglDNDIwLDkxLjIsNDA4LjgsODAsMzk1LDgweiBNNDAsMzAwVjEyMGgzNDB2MTgwSDQweiIvPgo8L3N2Zz4K\");\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-separator {\n  margin-left: -28px;\n  font-weight: bold;\n  color: #888;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-separator div:hover {\n  background: none;\n}\n[data-i18n-prefix=\"DecisionNavigatorTreeView.\"] ul li.kie-separator[title=\"DRDs\"] {\n  border-top: 1px solid #CECECE;\n  margin-top: 15px;\n  padding-top: 10px;\n}\n\n");
  }

  public DecisionNavigatorTreeView createInstance(final ContextManager contextManager) {
    final HTMLDivElement _items_1 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final ManagedInstance<TreeItem> _managedInstance_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { TreeItem.class }, new Annotation[] { });
    final HTMLDivElement _view_0 = (HTMLDivElement) contextManager.getInstance("ExtensionProvided_factory__e_d_HTMLDivElement__quals__j_e_i_Any_j_e_i_Default_j_i_Named");
    final Elemental2DomUtil _util_3 = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    final DecisionNavigatorTreeView instance = new DecisionNavigatorTreeView(_view_0, _items_1, _managedInstance_2, _util_3);
    registerDependentScopedReference(instance, _items_1);
    registerDependentScopedReference(instance, _managedInstance_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _util_3);
    setIncompleteInstance(instance);
    o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeViewTemplateResource templateForDecisionNavigatorTreeView = GWT.create(o_k_w_c_d_c_d_n_t_DecisionNavigatorTreeViewTemplateResource.class);
    Element parentElementForTemplateOfDecisionNavigatorTreeView = TemplateUtil.getRootTemplateParentElement(templateForDecisionNavigatorTreeView.getContents().getText(), "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", "");
    TemplateUtil.translateTemplate("org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionNavigatorTreeView));
    Map<String, Element> dataFieldElements = TemplateUtil.getDataFieldElements(TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionNavigatorTreeView));
    final Map<String, DataFieldMeta> dataFieldMetas = new HashMap<String, DataFieldMeta>(2);
    dataFieldMetas.put("view", new DataFieldMeta());
    dataFieldMetas.put("items", new DataFieldMeta());
    Map<String, Widget> templateFieldsMap = new LinkedHashMap<String, Widget>();
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorTreeView_HTMLDivElement_view(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "view");
    TemplateUtil.compositeComponentReplace("org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView", "org/kie/workbench/common/dmn/client/docks/navigator/tree/DecisionNavigatorTreeView.html", new Supplier<Widget>() {
      public Widget get() {
        return ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorTreeView_HTMLDivElement_items(instance)));
      }
    }, dataFieldElements, dataFieldMetas, "items");
    templateFieldsMap.put("view", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorTreeView_HTMLDivElement_view(instance))));
    templateFieldsMap.put("items", ElementWrapperWidget.getWidget(TemplateUtil.asElement(DecisionNavigatorTreeView_HTMLDivElement_items(instance))));
    TemplateUtil.initTemplated(instance, TemplateUtil.getRootTemplateElement(parentElementForTemplateOfDecisionNavigatorTreeView), templateFieldsMap.values());
    StyleBindingsRegistry.get().updateStyles(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DecisionNavigatorTreeView) instance, contextManager);
  }

  public void destroyInstanceHelper(final DecisionNavigatorTreeView instance, final ContextManager contextManager) {
    TemplateUtil.cleanupTemplated(instance);
  }

  native static HTMLDivElement DecisionNavigatorTreeView_HTMLDivElement_view(DecisionNavigatorTreeView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView::view;
  }-*/;

  native static void DecisionNavigatorTreeView_HTMLDivElement_view(DecisionNavigatorTreeView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView::view = value;
  }-*/;

  native static HTMLDivElement DecisionNavigatorTreeView_HTMLDivElement_items(DecisionNavigatorTreeView instance) /*-{
    return instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView::items;
  }-*/;

  native static void DecisionNavigatorTreeView_HTMLDivElement_items(DecisionNavigatorTreeView instance, HTMLDivElement value) /*-{
    instance.@org.kie.workbench.common.dmn.client.docks.navigator.tree.DecisionNavigatorTreeView::items = value;
  }-*/;
}