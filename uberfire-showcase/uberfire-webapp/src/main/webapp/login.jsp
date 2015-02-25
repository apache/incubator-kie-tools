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
<html xmlns="http://www.w3.org/1999/xhtml"><head>

  <title>Uberfire Showcase</title>

  <style type="text/css">
    * {
      font-family: Helvetica, Arial, sans-serif;
    }
    html {
        position: relative;
        height: 100%;
        width: 100%;
    }
    body {
      margin: 0;
      padding: 0;
      color: #fff;
      background: url('<%=request.getContextPath()%>/org.uberfire.UberfireShowcase/images/bgr.png') no-repeat #464141;
        background-size: auto 100%;
        background-position: top left;
      font-size: 14px;
      text-shadow: #050505 0 -1px 0;
        position: relative;
        height: 100%;
        width: 100%;
    }

    li {
      list-style: none;
    }

    #login-wrapper {
      position: absolute;
      top: 50%;
      left: 50%;
      margin-top: -105px;
    }

    #login-top {
      height: 70px;
      opacity: 0;
          -webkit-transition: opacity .6s ease-out;
      -o-transition: opacity .6s ease-in-out;
      transition: opacity .6s ease-in-out;
    }
      
    .show-login  #login-top {
        opacity: 1;
    }

    #login-content {
         -webkit-transition: opacity .6s ease-out;
      -o-transition: opacity .6s ease-in-out;
      transition: opacity .6s ease-in-out;
        transition-delay: .3s;
        -webkit-transition-delay: .3s;
        opacity: 0;
    }
    .show-login  #login-content {
          opacity: 1;
    }

    label {
      width: 70px;
      float: left;
      padding: 8px;
      line-height: 14px;
      margin-top: -4px;
    }

    input.text-input {
      width: 230px;
      background: #fff;
      border: none;
      font-size: 15px;
      color: #464040;
      padding: 0 10px;
        height: 35px;
        line-height: 35px;
        margin-bottom: 2px;
        position: relative;
        
    }

    input.button {
      width: 250px;
      background-color: #EA5B36 ;
      border: none;
      color: #FFF;
      font-size: 15px;
      text-align: center;
      height: 35px;
      line-height: 32px;
      margin-bottom: 2px;   
      -webkit-transition: all .3s ease-out;
      -o-transition: all .3s ease-out;
      transition: all .3s ease-out;
      cursor: pointer;
    }

    input.button:hover {
       background-color: #EDCB5F  ;
       color: #464040;
    }
    .input-container {
        position: relative;
    }

    .error::before {
      width: 0;
        height: 0;
        border-style: solid;
        border-width: 10px 10px 0 0;
        border-color: #e93b50 transparent transparent transparent;
        content: "";
        position: absolute;
        left: 1px;
        top: 1px;
        z-index: 10;
    }
      
    .error {
       display: inline-block;
      font-size: 10px;
       margin: 15px 0;
       text-transform: uppercase;
       opacity:.5;
    }
   
    .sr-only {
        position: absolute;
        width: 1px;
        height: 1px;
        padding: 0;
        margin: -1px;
        overflow: hidden;
        clip: rect(0,0,0,0);
        border: 0;
    }
  </style>
</head>

<body id="login">

<div id="login-wrapper">
  <div id="login-top">
    <img src="images/uf.png" alt="UberFire Logo" title="Powered By Uberfire" height="44">
  </div>

  <div id="login-content">
    <form action="uf_security_check" method="post">
      
        <label class="sr-only">Username</label>
        <div class="input-container ">
        <input name="uf_username" class="text-input" type="text" placeholder="Username">
        </div>
      
        <label class="sr-only">Password</label>
        <div class="input-container">
        <input name="uf_password" class="text-input" type="password" placeholder="Password">
        </div>
        
        
        
        
        <input class="button" value="Sign In" type="submit"><br/>

    </form>
  </div>
</div>

<script>
    function showLogin() {
     var loginElement = document.getElementById('login-wrapper');
      if(loginElement) {

        loginElement.className += 'show-login';
      }
    }
    window.onload=showLogin;
</script>

</body>


</html>