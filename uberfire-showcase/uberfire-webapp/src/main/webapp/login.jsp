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
  <title>Login - UberFire Showcase</title>
  <link rel="stylesheet" href="org.uberfire.UberfireShowcase/patternfly/dist/css/patternfly.min.css">
</head>

<body>
<span id="badge">
  <img src="org.uberfire.UberfireShowcase/images/uf_logo.png" alt="UberFire Logo" title="Powered By Uberfire"/>
</span>
<div class="container">
  <div class="row">
    <div class="col-md-12">
      <div id="brand">
        <img src="org.uberfire.UberfireShowcase/images/uf.png" alt="Uberfire UI Framework">
      </div>
    </div>
    <div class="col-md-4 login">
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
          <label for="uf_username" class="col-md-2 control-label">Username</label>
          <div class="col-md-10">
            <input type="text" class="form-control" id="uf_username" name="uf_username" placeholder="admin" tabindex="1" autofocus>
          </div>
        </div>
        <div class="form-group">
          <label for="uf_password" class="col-md-2 control-label">Password</label>
          <div class="col-md-10">
            <input type="password" class="form-control" id="uf_password" name="uf_password" placeholder="admin" tabindex="2">
          </div>
        </div>
        <div class="form-group">
          <div class="col-md-offset-2 col-md-7">
            <div class="checkbox">
              <label>
                <input id="nosplash" name="nosplash" type="checkbox" tabindex="3"> Deactivate Splash Screen
              </label>
            </div>
          </div>
          <div class="col-md-3 submit">
            <button type="submit" class="btn btn-primary btn-lg" tabindex="4">Log In</button>
          </div>
        </div>
      </form>
    </div>
    <div class="col-md-8 details">
      <p><strong>Welcome to UberFire Showcase!</strong></p>
    </div>
  </div>
</div>

</body>
</html>
