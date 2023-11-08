/**
 * @externs
 */

/** @const */
var monaco = {};

/**
 * @constructor
 */
monaco.editor = function () {};

/**
 * @param {Element} elm
 * @param {Object} options
 * @return {monaco.editor}
 */
monaco.editor.create = function (elm, options) {};

monaco.editor.prototype.focus = function () {};

monaco.editor.prototype.dispose = function () {};

/**
 * @param {string} value
 */
monaco.editor.prototype.setValue = function (value) {};

/**
 * @return {string}
 */
monaco.editor.prototype.getValue = function () {};

/**
 * @param {Object} value
 */
monaco.editor.prototype.layout = function (value) {};

/**
 * @param {string} source
 * @param {string} handlerId
 */
monaco.editor.prototype.trigger = function (source, handlerId) {};

/**
 * @param {function(Object)} callback
 */
monaco.editor.prototype.onKeyDown = function (callback) {};

/**
 * @param {function(Object)} callback
 */
monaco.editor.prototype.onDidBlurEditorWidget = function (callback) {};
