function org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime(){
  var $wnd_0 = window;
  var $doc_0 = document;
  sendStats('bootstrap', 'begin');
  function isHostedMode(){
    var query = $wnd_0.location.search;
    return query.indexOf('gwt.codesvr.org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime=') != -1 || query.indexOf('gwt.codesvr=') != -1;
  }

  function sendStats(evtGroupString, typeString){
    if ($wnd_0.__gwtStatsEvent) {
      $wnd_0.__gwtStatsEvent({moduleName:'org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime', sessionId:$wnd_0.__gwtStatsSessionId, subSystem:'startup', evtGroup:evtGroupString, millis:(new Date).getTime(), type:typeString});
    }
  }

  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__sendStats = sendStats;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__moduleName = 'org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime';
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__errFn = null;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__moduleBase = 'DUMMY';
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__softPermutationId = 0;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__computePropValue = null;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__getPropMap = null;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__installRunAsyncCode = function(){
  }
  ;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__gwtStartLoadingFragment = function(){
    return null;
  }
  ;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__gwt_isKnownPropertyValue = function(){
    return false;
  }
  ;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__gwt_getMetaProperty = function(){
    return null;
  }
  ;
  var __propertyErrorFunction = null;
  var activeModules = $wnd_0.__gwt_activeModules = $wnd_0.__gwt_activeModules || {};
  activeModules['org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'] = {moduleName:'org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'};
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__moduleStartupDone = function(permProps){
    var oldBindings = activeModules['org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'].bindings;
    activeModules['org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'].bindings = function(){
      var props = oldBindings?oldBindings():{};
      var embeddedProps = permProps[org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__softPermutationId];
      for (var i = 0; i < embeddedProps.length; i++) {
        var pair = embeddedProps[i];
        props[pair[0]] = pair[1];
      }
      return props;
    }
    ;
  }
  ;
  var frameDoc;
  function getInstallLocationDoc(){
    setupInstallLocation();
    return frameDoc;
  }

  function setupInstallLocation(){
    if (frameDoc) {
      return;
    }
    var scriptFrame = $doc_0.createElement('iframe');
    scriptFrame.id = 'org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime';
    scriptFrame.style.cssText = 'position:absolute; width:0; height:0; border:none; left: -1000px;' + ' top: -1000px;';
    scriptFrame.tabIndex = -1;
    $doc_0.body.appendChild(scriptFrame);
    frameDoc = scriptFrame.contentWindow.document;
    frameDoc.open();
    var doctype = document.compatMode == 'CSS1Compat'?'<!doctype html>':'';
    frameDoc.write(doctype + '<html><head><\/head><body><\/body><\/html>');
    frameDoc.close();
  }

  function installScript(filename){
    function setupWaitForBodyLoad(callback){
      function isBodyLoaded(){
        if (typeof $doc_0.readyState == 'undefined') {
          return typeof $doc_0.body != 'undefined' && $doc_0.body != null;
        }
        return /loaded|complete/.test($doc_0.readyState);
      }

      var bodyDone = isBodyLoaded();
      if (bodyDone) {
        callback();
        return;
      }
      function checkBodyDone(){
        if (!bodyDone) {
          if (!isBodyLoaded()) {
            return;
          }
          bodyDone = true;
          callback();
          if ($doc_0.removeEventListener) {
            $doc_0.removeEventListener('readystatechange', checkBodyDone, false);
          }
          if (onBodyDoneTimerId) {
            clearInterval(onBodyDoneTimerId);
          }
        }
      }

      if ($doc_0.addEventListener) {
        $doc_0.addEventListener('readystatechange', checkBodyDone, false);
      }
      var onBodyDoneTimerId = setInterval(function(){
        checkBodyDone();
      }
      , 10);
    }

    function installCode(code_0){
      var doc = getInstallLocationDoc();
      var docbody = doc.body;
      var script = doc.createElement('script');
      script.language = 'javascript';
      script.src = code_0;
      if (org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__errFn) {
        script.onerror = function(){
          org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__errFn('org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime', new Error('Failed to load ' + code_0));
        }
        ;
      }
      docbody.appendChild(script);
      sendStats('moduleStartup', 'scriptTagAdded');
    }

    sendStats('moduleStartup', 'moduleRequested');
    setupWaitForBodyLoad(function(){
      installCode(filename);
    }
    );
  }

  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__startLoadingFragment = function(fragmentFile){
    return computeUrlForResource(fragmentFile);
  }
  ;
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__installRunAsyncCode = function(code_0){
    var doc = getInstallLocationDoc();
    var docbody = doc.body;
    var script = doc.createElement('script');
    script.language = 'javascript';
    script.text = code_0;
    docbody.appendChild(script);
  }
  ;
  function processMetas(){
    var metaProps = {};
    var propertyErrorFunc;
    var onLoadErrorFunc;
    var metas = $doc_0.getElementsByTagName('meta');
    for (var i = 0, n = metas.length; i < n; ++i) {
      var meta = metas[i], name_1 = meta.getAttribute('name'), content_0;
      if (name_1) {
        name_1 = name_1.replace('org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime::', '');
        if (name_1.indexOf('::') >= 0) {
          continue;
        }
        if (name_1 == 'gwt:property') {
          content_0 = meta.getAttribute('content');
          if (content_0) {
            var value_1, eq = content_0.indexOf('=');
            if (eq >= 0) {
              name_1 = content_0.substring(0, eq);
              value_1 = content_0.substring(eq + 1);
            }
             else {
              name_1 = content_0;
              value_1 = '';
            }
            metaProps[name_1] = value_1;
          }
        }
         else if (name_1 == 'gwt:onPropertyErrorFn') {
          content_0 = meta.getAttribute('content');
          if (content_0) {
            try {
              propertyErrorFunc = eval(content_0);
            }
             catch (e) {
              alert('Bad handler "' + content_0 + '" for "gwt:onPropertyErrorFn"');
            }
          }
        }
         else if (name_1 == 'gwt:onLoadErrorFn') {
          content_0 = meta.getAttribute('content');
          if (content_0) {
            try {
              onLoadErrorFunc = eval(content_0);
            }
             catch (e) {
              alert('Bad handler "' + content_0 + '" for "gwt:onLoadErrorFn"');
            }
          }
        }
      }
    }
    __gwt_getMetaProperty = function(name_0){
      var value_0 = metaProps[name_0];
      return value_0 == null?null:value_0;
    }
    ;
    __propertyErrorFunction = propertyErrorFunc;
    org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__errFn = onLoadErrorFunc;
  }

  function computeScriptBase(){
    function getDirectoryOfFile(path){
      var hashIndex = path.lastIndexOf('#');
      if (hashIndex == -1) {
        hashIndex = path.length;
      }
      var queryIndex = path.indexOf('?');
      if (queryIndex == -1) {
        queryIndex = path.length;
      }
      var slashIndex = path.lastIndexOf('/', Math.min(queryIndex, hashIndex));
      return slashIndex >= 0?path.substring(0, slashIndex + 1):'';
    }

    function ensureAbsoluteUrl(url_0){
      if (url_0.match(/^\w+:\/\//)) {
      }
       else {
        var img = $doc_0.createElement('img');
        img.src = url_0 + 'clear.cache.gif';
        url_0 = getDirectoryOfFile(img.src);
      }
      return url_0;
    }

    function tryMetaTag(){
      var metaVal = __gwt_getMetaProperty('baseUrl');
      if (metaVal != null) {
        return metaVal;
      }
      return '';
    }

    function tryNocacheJsTag(){
      var scriptTags = $doc_0.getElementsByTagName('script');
      for (var i = 0; i < scriptTags.length; ++i) {
        if (scriptTags[i].src.indexOf('org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime.nocache.js') != -1) {
          return getDirectoryOfFile(scriptTags[i].src);
        }
      }
      return '';
    }

    function tryBaseTag(){
      var baseElements = $doc_0.getElementsByTagName('base');
      if (baseElements.length > 0) {
        return baseElements[baseElements.length - 1].href;
      }
      return '';
    }

    function isLocationOk(){
      var loc = $doc_0.location;
      return loc.href == loc.protocol + '//' + loc.host + loc.pathname + loc.search + loc.hash;
    }

    var tempBase = tryMetaTag();
    if (tempBase == '') {
      tempBase = tryNocacheJsTag();
    }
    if (tempBase == '') {
      tempBase = tryBaseTag();
    }
    if (tempBase == '' && isLocationOk()) {
      tempBase = getDirectoryOfFile($doc_0.location.href);
    }
    tempBase = ensureAbsoluteUrl(tempBase);
    return tempBase;
  }

  function computeUrlForResource(resource){
    if (resource.match(/^\//)) {
      return resource;
    }
    if (resource.match(/^[a-zA-Z]+:\/\//)) {
      return resource;
    }
    return org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__moduleBase + resource;
  }

  function getCompiledCodeFilename(){
    var answers = [];
    var softPermutationId = 0;
    var values = [];
    var providers = [];
    function computePropValue(propName){
      var value_0 = providers[propName](), allowedValuesMap = values[propName];
      if (value_0 in allowedValuesMap) {
        return value_0;
      }
      var allowedValuesList = [];
      for (var k in allowedValuesMap) {
        allowedValuesList[allowedValuesMap[k]] = k;
      }
      if (__propertyErrorFunction) {
        __propertyErrorFunction(propName, allowedValuesList, value_0);
      }
      throw null;
    }

    __gwt_isKnownPropertyValue = function(propName, propValue){
      return propValue in values[propName];
    }
    ;
    org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__getPropMap = function(){
      var result = {};
      for (var key in values) {
        if (values.hasOwnProperty(key)) {
          result[key] = computePropValue(key);
        }
      }
      return result;
    }
    ;
    org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__computePropValue = computePropValue;
    $wnd_0.__gwt_activeModules['org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'].bindings = org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__getPropMap;
    sendStats('bootstrap', 'selectingPermutation');
    if (isHostedMode()) {
      return computeUrlForResource('org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime.devmode.js');
    }
    var strongName;
    try {
      strongName = '8573742993984FD08F42CA6D9F03BB49';
      var idx = strongName.indexOf(':');
      if (idx != -1) {
        softPermutationId = parseInt(strongName.substring(idx + 1), 10);
        strongName = strongName.substring(0, idx);
      }
    }
     catch (e) {
    }
    org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__softPermutationId = softPermutationId;
    return computeUrlForResource(strongName + '.cache.js');
  }

  function loadExternalStylesheets(){
    if (!$wnd_0.__gwt_stylesLoaded) {
      $wnd_0.__gwt_stylesLoaded = {};
    }
    function installOneStylesheet(stylesheetUrl){
      if (!__gwt_stylesLoaded[stylesheetUrl]) {
        var l = $doc_0.createElement('link');
        l.setAttribute('rel', 'stylesheet');
        l.setAttribute('href', computeUrlForResource(stylesheetUrl));
        $doc_0.getElementsByTagName('head')[0].appendChild(l);
        __gwt_stylesLoaded[stylesheetUrl] = true;
      }
    }

    sendStats('loadExternalRefs', 'begin');
    installOneStylesheet('jquery-ui/jquery-ui.min.css');
    installOneStylesheet('bootstrap-daterangepicker/daterangepicker.css');
    installOneStylesheet('bootstrap-select/css/bootstrap-select.min.css');
    installOneStylesheet('prettify/bin/prettify.min.css');
    installOneStylesheet('uberfire-patternfly.css');
    installOneStylesheet('monaco-editor/dev/vs/editor/editor.main.css');
    installOneStylesheet('css/patternfly.min.css');
    installOneStylesheet('css/patternfly-additions.min.css');
    installOneStylesheet('css/bootstrap-datepicker3-1.6.4.min.cache.css');
    sendStats('loadExternalRefs', 'end');
  }

  processMetas();
  org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__moduleBase = computeScriptBase();
  activeModules['org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'].moduleBase = org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.__moduleBase;
  var filename_0 = getCompiledCodeFilename();
  if ($wnd_0) {
    var devModePermitted = !!($wnd_0.location.protocol == 'http:' || $wnd_0.location.protocol == 'file:');
    $wnd_0.__gwt_activeModules['org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime'].canRedirect = devModePermitted;
    function supportsSessionStorage(){
      var key = '_gwt_dummy_';
      try {
        $wnd_0.sessionStorage.setItem(key, key);
        $wnd_0.sessionStorage.removeItem(key);
        return true;
      }
       catch (e) {
        return false;
      }
    }

    if (devModePermitted && supportsSessionStorage()) {
      var devModeKey = '__gwtDevModeHook:org.drools.workbench.screens.scenariosimulation.webapp.DroolsWorkbenchScenarioSimulationKogitoRuntime';
      var devModeUrl = $wnd_0.sessionStorage[devModeKey];
      if (!/^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/.*$/.test(devModeUrl)) {
        if (devModeUrl && (window.console && console.log)) {
          console.log('Ignoring non-whitelisted Dev Mode URL: ' + devModeUrl);
        }
        devModeUrl = '';
      }
      if (devModeUrl && !$wnd_0[devModeKey]) {
        $wnd_0[devModeKey] = true;
        $wnd_0[devModeKey + ':moduleBase'] = computeScriptBase();
        var devModeScript = $doc_0.createElement('script');
        devModeScript.src = devModeUrl;
        var head = $doc_0.getElementsByTagName('head')[0];
        head.insertBefore(devModeScript, head.firstElementChild || head.children[0]);
        return false;
      }
    }
  }
  loadExternalStylesheets();
  sendStats('bootstrap', 'end');
  installScript(filename_0);
  return true;
}

org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime.succeeded = org_drools_workbench_screens_scenariosimulation_webapp_DroolsWorkbenchScenarioSimulationKogitoRuntime();
