var helpers = require('./global-setup')
var path = require('path')
var assert = require('assert')

var describe = global.describe
var it = global.it
var beforeEach = global.beforeEach
var afterEach = global.afterEach

const UNSAVED_FILE = 'unsaved file';
const KOGITO_EDITOR_LOCATOR = '//iframe[@id = \'kogito-iframe\']';
const UNSAVED_FILE_H3_LOCATOR = '//h3[text() = \'unsaved file\']';
const GO_TO_HOMEPAGE_BUTTON_LOCATOR = '//button[@aria-label=\'Go to homepage\']';
const LOADING_SCREEN_LOCATOR = '#loading-screen';
const BMP_TITLE = "Business Modeler Preview";

describe('Application Startup', function () {
  helpers.setupTimeout(this)

  var app = null

  beforeEach(function () {
      return helpers.startApplication({
        args: [path.join(__dirname, '../..')],
        port: 3000,
        startTimeout: 25000,
        waitTimeout: 25000
      }).then(function (startedApp) { 
        app = startedApp
      })
  });

  afterEach(function () {
    return helpers.stopApplication(app)
  });

  it('opens application window', function () {
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

    it('opens BPMN editor - new file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click('//article[@data-ouia-component-id = \'create-new-bpmn\']')
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      await app.client.isVisible(LOADING_SCREEN_LOCATOR).should.eventually.be.false
      
      await app.client.getText(UNSAVED_FILE_H3_LOCATOR).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR)

      await app.client.waitUntilWindowLoaded()
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

    it('opens DMN editor - new file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click('//article[@data-ouia-component-id = \'create-new-dmn\']')
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      await app.client.isVisible(LOADING_SCREEN_LOCATOR).should.eventually.be.false
      
      await app.client.getText(UNSAVED_FILE_H3_LOCATOR).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR).waitUntilWindowLoaded()

      await app.client.waitUntilWindowLoaded()
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

    it('opens BPMN editor - sample file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click('//article[@data-ouia-component-id = \'open-sample-bpmn\']')
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      await app.client.isVisible('#loading-screen').should.eventually.be.false
      
      await app.client.getText(UNSAVED_FILE_H3_LOCATOR).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR)

      await app.client.waitUntilWindowLoaded()
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

    it('opens DMN editor - sample file', async () => {
      await app.client.waitUntilWindowLoaded();
      await app.client.click('//article[@data-ouia-component-id = \'open-sample-dmn\']')
      await app.client.waitUntilWindowLoaded().getWindowCount().should.eventually.equal(1)
      await app.client.isVisible(LOADING_SCREEN_LOCATOR).should.eventually.be.false
      
      await app.client.getText(UNSAVED_FILE_H3_LOCATOR).should.eventually.be.equal(UNSAVED_FILE)
      await app.client.isVisible(KOGITO_EDITOR_LOCATOR).should.eventually.be.true
      await app.client.browserWindow.focus().waitUntilWindowLoaded();
      await app.client.browserWindow.focus().click(GO_TO_HOMEPAGE_BUTTON_LOCATOR).waitUntilWindowLoaded()

      await app.client.waitUntilWindowLoaded()
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
  })
})