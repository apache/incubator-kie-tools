
$(function(){
    var containerConfig = {};
    containerConfig[osapi.container.ServiceConfig.API_PATH] = "/rpc";
    containerConfig[osapi.container.ContainerConfig.RENDER_DEBUG] = "1";
    var container = new osapi.container.Container(containerConfig);
    container.rpcRegister('resize_iframe', resizeIframe);
    container.rpcRegister('set_pref', setPref);
});


/*
 RPC Callback handlers
 */
/**
 * Resizes the iFrame when gadgets.window.adjustHeight is called
 *
 * @param args the RPC event args
 */
function resizeIframe(args) {
    var max = 0x7FFFFFFF;
    var height = args.a > max ? max : args.a;
    var elm = document.getElementById(args.f);
    elm.style.height = height + 'px';
}

/**
 * Saves a userPref for the widget
 *
 * @param args RPC event args
 * @param editToken this is an old deprecated parameter but still needs to be in the signature for proper binding
 * @param prefName the userpref name
 * @param prefValue the userpref value
 */
function setPref(args, editToken, prefName, prefValue) {
}


/**
 * This will be used for window maximization.
 */
function addOverlay(jqElm) {
    var overlay = $('<div></div>');
    var styleMap = {
        position: "absolute",
        height : jqElm.height(),
        width : jqElm.width(),
        'z-index': 10,
        opacity : 0.7,
        background : "#FFFFFF"
    };
    $(overlay).css(styleMap);
    $(overlay).addClass("added-overlay");
    jqElm.prepend(overlay[0]);
}



