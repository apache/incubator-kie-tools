#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>UF Showcase</title>

  <style type="text/css">
    * {
      font-family: Helvetica, Arial, sans-serif;
    }

    body {
      margin: 0;
      padding: 0;
      color: ${symbol_pound}fff;
      background: url('<%=request.getContextPath()%>/${package}.${capitalizedRootArtifactId}Showcase/images/bg-login.png') repeat ${symbol_pound}1b1b1b;
      font-size: 14px;
      text-shadow: ${symbol_pound}050505 0 -1px 0;
      font-weight: bold;
    }

    li {
      list-style: none;
    }

    ${symbol_pound}dummy {
      position: absolute;
      top: 0;
      left: 0;
      border-bottom: solid 3px ${symbol_pound}777973;
      height: 250px;
      width: 100%;
      background: url('<%=request.getContextPath()%>/${package}.${capitalizedRootArtifactId}Showcase/images/bg-login-top.png') repeat ${symbol_pound}fff;
      z-index: 1;
    }

    ${symbol_pound}dummy2 {
      position: absolute;
      top: 0;
      left: 0;
      border-bottom: solid 2px ${symbol_pound}545551;
      height: 252px;
      width: 100%;
      background: transparent;
      z-index: 2;
    }

    ${symbol_pound}login-wrapper {
      margin: 0 0 0 -160px;
      width: 320px;
      text-align: center;
      z-index: 99;
      position: absolute;
      top: 0;
      left: 50%;
    }

    ${symbol_pound}login-top {
      height: 120px;
      width: 401px;
      padding-top: 20px;
      text-align: center;
    }

    ${symbol_pound}login-content {
      margin-top: 120px;
    }

    label {
      width: 70px;
      float: left;
      padding: 8px;
      line-height: 14px;
      margin-top: -4px;
    }

    input.text-input {
      width: 200px;
      float: right;
      -moz-border-radius: 4px;
      -webkit-border-radius: 4px;
      border-radius: 4px;
      background: ${symbol_pound}fff;
      border: solid 1px transparent;
      color: ${symbol_pound}555;
      padding: 8px;
      font-size: 13px;
    }

    input.button {
      float: right;
      padding: 6px 10px;
      color: ${symbol_pound}fff;
      font-size: 14px;
      background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(${symbol_pound}a4d04a), to(${symbol_pound}459300));
      text-shadow: ${symbol_pound}050505 0 -1px 0;
      background-color: ${symbol_pound}459300;
      -moz-border-radius: 4px;
      -webkit-border-radius: 4px;
      border-radius: 4px;
      border: solid 1px transparent;
      font-weight: bold;
      cursor: pointer;
      letter-spacing: 1px;
    }

    input.button:hover {
      background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(${symbol_pound}a4d04a), to(${symbol_pound}a4d04a), color-stop(80%, ${symbol_pound}76b226));
      text-shadow: ${symbol_pound}050505 0 -1px 2px;
      background-color: ${symbol_pound}a4d04a;
      color: ${symbol_pound}fff;
    }

    div.error {
      padding: 8px;
      background: rgba(52, 4, 0, 0.4);
      -moz-border-radius: 8px;
      -webkit-border-radius: 8px;
      border-radius: 8px;
      border: solid 1px transparent;
      margin: 6px 0;
    }
  </style>
</head>

<body id="login">

<div id="login-wrapper" class="png_bg">
  <div id="login-top">
    <img src="<%=request.getContextPath()%>/${package}.${capitalizedRootArtifactId}Showcase/images/uf_logo.png" alt="Logo" title="Powered By Uberfire"/>
  </div>

  <div id="login-content">
    <form action="uf_security_check" method="post">
      <p>
        <label>Username</label>
        <input value="" name="uf_username" class="text-input" type="text"/>
      </p>
      <br style="clear: both;"/>

      <p>
        <label>Password</label>
        <input name="uf_password" class="text-input" type="password"/>
      </p>
      <br style="clear: both;"/>

      <p>
        <input class="button" type="submit" value="Sign In"/>
      </p>
    </form>
  </div>
</div>
<div id="dummy"></div>
<div id="dummy2"></div>
</body>
</html>
