<%--
  ~ Copyright 2012 JBoss Inc
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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page import="org.jboss.errai.security.server.FormAuthenticationScheme"%>
<html xmlns="http://www.w3.org/1999/xhtml" class="login-pf">
<head>
  <title>Login - Kie Workbench Showcase</title>
  <link rel="stylesheet" href="org.kie.workbench.common.Showcase/patternfly/dist/css/patternfly.min.css">
  <link rel="stylesheet" href="org.kie.workbench.common.Showcase/patternfly/dist/css/patternfly-additions.min.css">
</head>

<body>
<div class="container">
  <div class="row">
    <div class="col-sm-12">
    </div>
    <div class="col-sm-7 col-md-6 col-lg-5 login">
      <% if (request.getParameter( FormAuthenticationScheme.LOGIN_ERROR_QUERY_PARAM ) != null) { %>
        <div class="alert alert-danger">
        <span class="pficon-layered">
          <span class="pficon pficon-error-octagon"></span>
          <span class="pficon pficon-error-exclamation"></span>
        </span>
          Login failed. Please try again.
        </div>
      <% } %>
      <form class="form-horizontal" role="form" action="uf_security_check" method="post">
        <div class="form-group">
          <label for="uf_username" class="col-sm-2 col-md-2 control-label">Username</label>
          <div class="col-sm-10 col-md-10">
            <input type="text" class="form-control" id="uf_username" name="uf_username" placeholder="admin" tabindex="1" autofocus>
          </div>
        </div>
        <div class="form-group">
          <label for="uf_password" class="col-sm-2 col-md-2 control-label">Password</label>
          <div class="col-sm-10 col-md-10">
            <input type="password" class="form-control" id="uf_password" name="uf_password" placeholder="admin" tabindex="2">
          </div>
        </div>
        <div class="form-group">
          <div class="col-xs-offset-8 col-xs-4 col-sm-offset-8 col-sm-4 col-md-offset-8 col-md-4 submit">
            <button type="submit" class="btn btn-primary btn-lg" tabindex="4">Log In</button>
          </div>
        </div>
      </form>
    </div>
    <div class="col-sm-5 col-md-6 col-lg-7 details">
      <p><strong>Welcome to Showcase!</strong></p>
    </div>
  </div>
</div>

</body>
</html>
