<%--
  ~ Copyright 2016 Red Hat, Inc. and/or its affiliates.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~       http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    Locale locale = null;
    try {
        locale = new Locale( request.getParameter( "locale" ) );
    } catch ( Exception e ) {
        locale = request.getLocale();
    }
%>
<i18n:bundle id="bundle" baseName="org.dashbuilder.client.resources.i18n.LoginConstants" locale='<%= locale %>' />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="login-pf" lang="en">
<head>
    <title>Dashbuilder</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="org.dashbuilder.DashbuilderShowcase/css/patternfly.min.css">
    <link rel="stylesheet" href="org.dashbuilder.DashbuilderShowcase/css/patternfly-additions.min.css">
</head>

<body>
<span id="badge">
  <img src="images/dashbuilder-logo.png" alt="Dashbuilder Logo" title="<i18n:message key='powered'>Powered By Dashbuilder</i18n:message>"/>
</span>
<div class="container">
    <div class="row">
        <div class="col-sm-6 col-md-5 col-lg-4 login">
            <p><strong><i18n:message key="welcome">Welcome to Dashbuilder !</i18n:message></strong></p>
            <c:if test="${param.message != null}">
                <div class="alert alert-danger">
                    <span class="pficon-layered">
                      <span class="pficon pficon-error-octagon"></span>
                      <span class="pficon pficon-error-exclamation"></span>
                    </span>
                    <%=request.getParameter("message")%>
                </div>
            </c:if>
            <form class="form-horizontal" role="form" action="j_security_check" method="post">
                <div class="form-group">
                    <label for="j_username" class="col-sm-2 col-md-2 control-label"><i18n:message key="userName">Username</i18n:message></label>
                    <div class="col-sm-10 col-md-10">
                        <input type="text" class="form-control" id="j_username" name="j_username" placeholder="admin" tabindex="1" autofocus>
                    </div>
                </div>
                <div class="form-group">
                    <label for="j_password" class="col-sm-2 col-md-2 control-label"><i18n:message key="password">Password</i18n:message></label>
                    <div class="col-sm-10 col-md-10">
                        <input type="password" class="form-control" id="j_password" name="j_password" placeholder="admin" tabindex="2">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-xs-8 col-sm-offset-2 col-sm-6 col-md-offset-2 col-md-6">
                        <%--
                                                <div class="checkbox">
                                                    <label>
                                                        <input id="nosplash" name="nosplash" type="checkbox" tabindex="3"> Deactivate Splash Screen
                                                    </label>
                                                </div>
                        --%>
                    </div>
                    <div class="col-xs-4 col-sm-4 col-md-4 submit">
                        <button type="submit" class="btn btn-primary btn-lg" tabindex="4"><i18n:message key="signIn">Sign In</i18n:message></button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>