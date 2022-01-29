/**
 * @fileoverview Externs for jQuery 1.12 & 2.2
 *
 * The jQuery API is identical for the 1.12.x+ and 2.2+ branches. In addition,
 * the API has not changed in releases since that date. These externs are valid
 * for all jQuery releases since 1.12 and 2.2.
 *
 * Note that some functions use different return types depending on the number
 * of parameters passed in. In these cases, you may need to annotate the type
 * of the result in your code, so the JSCompiler understands which type you're
 * expecting. For example:
 *    <code>var elt = /** @type {Element} * / (foo.get(0));</code>
 *
 * @see http://api.jquery.com/
 * @externs
 */

/** @param {...*} var_args */
jQuery.prototype.alert = function (var_args) {};

/**
 * @param {Object} param
 * @return {jQuery}
 */
jQuery.prototype.affix = function (param) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.selectpicker = function (var_args) {};

/**
 * @param {(string|Object)} fnName
 * @param {...?} fnArgs
 * @return {?}
 */
jQuery.prototype.datepicker = function (fnName, fnArgs) {};

/**
 * @param {(string|Object)} fnName
 * @param {...?} fnArgs
 * @return {?}
 */
jQuery.prototype.datetimepicker = function (fnName, fnArgs) {};

/**
 * @param {Object|Element|string|number} e
 * @param {number=} interval
 * @param {string=} pause
 * @param {boolean=} wrap
 * @return {?}
 */
jQuery.prototype.carousel = function (e, interval, pause, wrap) {};

/**
 * @param {(string)} param
 * @return {?}
 */
jQuery.prototype.collapse = function (param) {};

/**
 * @param {(string)} param
 * @return {?}
 */
jQuery.prototype.modal = function (param) {};

/**
 * @param {string=} param
 * @return {jQuery}
 */
jQuery.prototype.popover = function (param) {};

/**
 * @param {string|Object} param
 * @return {jQuery}
 */
jQuery.prototype.scrollspy = function (param) {};

/**
 * @param {string} param
 * @return {jQuery}
 */
jQuery.prototype.tab = function (param) {};

/**
 * @param {string=} param
 * @return {jQuery}
 */
jQuery.prototype.tooltip = function (param) {};

/**
 * @param {string?} param
 * @return {jQuery}
 */
jQuery.prototype.button = function (param) {};

/**
 * @param {string|Object?} value
 * @param {Object=} value1
 * @return {Object}
 */
jQuery.prototype.notify = function (value, value1) {};

/** @param {...*} var_args
 * @return (module$exports$org$gwtbootstrap3$extras$notify$client$ui$Notify$impl|null)
 */
jQuery.notify = function (var_args) {};

/** @param {(string)=} arg  */
jQuery.notifyClose = function (arg) {};

jQuery.notifyDefaults = function () {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.slider = function (var_args) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.bootstrapSwitch = function (var_args) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.tagsinput = function (var_args) {};

/**
 * @param {Object} args
 * @return {boolean}
 */
jQuery.prototype.isArray = function (args) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.summernote = function (var_args) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.bootstrapSlider = function (var_args) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.fullCalendar = function (var_args) {};

/**
 * @param {...*} var_args
 * @return {Object}
 */
jQuery.prototype.typeahead = function (var_args) {};

var bootbox = {
  alert: function () {},
  confirm: function () {},
  prompt: function () {},
  dialog: function () {},
  setDefaults: function () {},
  hideAll: function () {},
  addLocale: function () {},
  removeLocale: function () {},
  setLocale: function () {},
  init: function () {},
};
