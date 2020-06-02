<%--
~ Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.getSession().invalidate();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="login-pf" lang="en">
<head>
    <title>Dashbuilder Runtime</title>
    <link rel="icon" href="favicon.ico" type="image/x-icon" />
    <link rel="stylesheet" href="org.dashbuilder.DashbuilderRuntime/css/patternfly.min.css" />
    <link rel="stylesheet" href="org.dashbuilder.DashbuilderRuntime/css/patternfly-additions.min.css" />
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-sm-6 col-md-5 col-lg-4 login">
            <p><strong>Login failed: Not Authorized</strong></p>
            <form class="form-horizontal" role="form" action="<%= request.getContextPath() %>/dashbuilder.html" method="post">
                <div class="form-group">
                    <div class="col-xs-4 col-sm-4 col-md-4 submit">
                        <button type="submit" class="btn btn-primary btn-lg" tabindex="4">
                            Login as another user
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>