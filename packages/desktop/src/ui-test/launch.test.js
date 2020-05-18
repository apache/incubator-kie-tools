var helpers = require('./global-setup')
var path = require('path')
var assert = require('assert')

var describe = global.describe
var it = global.it
var beforeEach = global.beforeEach
var afterEach = global.afterEach

const KOGITO_EDITOR_LOCATOR = "//iframe[@class = \'kogito--editor\']";
const UNSAVED_FILE_H3_LOCATOR = "//h3[text() = 'unsaved file']"
const BMP_TITLE= "Business Modeler Preview"

describe('Application Startup', function () {
  helpers.setupTimeout(this)

  var app = null

  beforeEach(function () {
      return helpers.startApplication({
        args: [path.join(__dirname, '../..')],
        port: 3000,
        startTimeout: 25000
      }).then(function (startedApp) { 
        app = startedApp
      })
  })

  afterEach(function () {
    return helpers.stopApplication(app)
  })

  it('opens window', function () {
    return app.client.waitUntilWindowLoaded()
      .browserWindow.focus()
      .getWindowCount().should.eventually.equal(1)
      .browserWindow.isMinimized().should.eventually.be.false
      .browserWindow.isDevToolsOpened().should.eventually.be.false
      .browserWindow.isVisible().should.eventually.be.true
      .browserWindow.isFocused().should.eventually.be.true
      .browserWindow.getBounds().should.eventually.have.property('width').and.be.above(0)
      .browserWindow.getBounds().should.eventually.have.property('height').and.be.above(0)
      .getTitle().should.eventually.equal(BMP_TITLE)
  })

  describe('Editors can be opened', function() {
    it('opens BPMN editor - new file', function() {
      return app.client.waitUntilWindowLoaded()
      .browserWindow.focus().click('//article[@data-ouia-component-id = \'create-new-bpmn\']')
      .waitUntilWindowLoaded()
      .isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      .waitUntilWindowLoaded()
      .isVisible("canvas")
      .getText(UNSAVED_FILE_H3_LOCATOR)
    })

    it('opens DMN editor - new file', function() {
      return app.client.waitUntilWindowLoaded()
      .browserWindow.focus().click('//article[@data-ouia-component-id = \'create-new-dmn\']')
      .waitUntilWindowLoaded()
      .isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      .waitUntilWindowLoaded()
      .isVisible("canvas")
      .getText(UNSAVED_FILE_H3_LOCATOR)
    })

    it('opens BPMN editor - sample file', function() {
      return app.client.waitUntilWindowLoaded()
      .browserWindow.focus().click('//article[@data-ouia-component-id = \'open-sample-bpmn\']')
      .waitUntilWindowLoaded()
      .isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      .waitUntilWindowLoaded()
      .isVisible("//canvas")
      .getText(UNSAVED_FILE_H3_LOCATOR)
    })

    it('opens DMN editor - sample file', function() {
      return app.client.waitUntilWindowLoaded()
      .browserWindow.focus().click('//article[@data-ouia-component-id = \'open-sample-dmn\']')
      .waitUntilWindowLoaded()
      .isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      .waitUntilWindowLoaded()
      .isVisible("//canvas")
      .getText(UNSAVED_FILE_H3_LOCATOR)
    })
  })
})