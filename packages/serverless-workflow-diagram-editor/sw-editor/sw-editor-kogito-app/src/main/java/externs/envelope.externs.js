/**
 * @externs
 */

/**
 * @constructor
 */
var envelope = function () {};

/**
 * @constructor
 */
var editorContext = function () {};

/**
 * @type {editorContext}
 */
envelope.prototype.editorContext;

/**
 * @type {string}
 */
editorContext.prototype.channel;

/**
 * @type {string}
 */
editorContext.prototype.readOnly;

/**
 * @type {string}
 */
editorContext.prototype.operatingSystem;

/**
 * @constructor
 */
var keyboardShortcutsService = function () {};

/**
 * @type {keyboardShortcutsService}
 */
envelope.prototype.keyboardShortcutsService;

/**
 *
 * @param {string} combination
 * @param {string} label
 * @param {Function} onKeyDown
 * @param {Object} opts
 * @return {number}
 */
keyboardShortcutsService.prototype.registerKeyPress = function (combination, label, onKeyDown, opts) {};

/**
 *
 * @param {string} combination
 * @param {string} label
 * @param {Function} onKeyDown
 * @param {Function} onKeyUp
 * @param {Object} opts
 * @return {number}
 */
keyboardShortcutsService.prototype.registerKeyDownThenUp = function (combination, label, onKeyDown, onKeyUp, opts) {};

/**
 * @param {number} id
 */
keyboardShortcutsService.prototype.deregister = function (id) {};

/**
 * @constructor
 */
var diagramService = function () {};

/**
 * @type {diagramService}
 */
envelope.prototype.diagramService;

/**
 * @param {string} node
 */
diagramService.prototype.onNodeSelected = function (node) {};

diagramService.prototype.setContentSuccess = function () {};

/**
 * @constructor
 */
var i18nService = function () {};

/**
 * @type {i18nService}
 */
envelope.prototype.i18nService;

/**
 * @return {Promise<string>}
 */
i18nService.prototype.getLocale = function () {};

/**
 *
 * @param {Function} callback
 */
i18nService.prototype.onLocaleChange = function (callback) {};

/**
 * @constructor
 */
var notificationsApi = function () {};

/**
 * @type {notificationsApi}
 */
envelope.prototype.notificationsApi;

/**
 *
 * @param {Object} notification
 */
notificationsApi.prototype.createNotification = function (notification) {};

/**
 *
 * @param {string} path
 * @param {Array<Object>} notifications
 */
notificationsApi.prototype.createNotifications = function (path, notifications) {};

/**
 *
 * @param {string} path
 */
notificationsApi.prototype.removeNotifications = function (path) {};

/**
 * @constructor
 */
var resourceContentEditorService = function () {};

/**
 * @type {resourceContentEditorService}
 */
envelope.prototype.resourceContentEditorService;

/**
 * @param {string} uri
 * @param {Object=} options
 * @return {Promise<string>}
 */
resourceContentEditorService.prototype.get = function (uri, options) {};

/**
 * @param {string} pattern
 * @param {Object=} options
 * @return {Promise<Array<string>>}
 */
resourceContentEditorService.prototype.list = function (pattern, options) {};

/**
 * @constructor
 */
var workspaceService = function () {};

/**
 * @type {workspaceService}
 */
envelope.prototype.workspaceService;

/**
 * @param {string} path
 */
workspaceService.prototype.openFile = function (path) {};

/**
 * @constructor
 */
var gwt = function () {};

/**
 *
 * @param {Function} func
 */
gwt.prototype.setUndoCommand = function (func) {};

/**
 *
 * @param {Function} func
 */
gwt.prototype.setRedoCommand = function (func) {};
