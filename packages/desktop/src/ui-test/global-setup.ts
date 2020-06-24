import { ApplicationSettings, SpectronClient } from "spectron";

const Application = require('spectron').Application
const assert = require('assert');
const path = require('path');
const chaiActual = require('chai')
const chaiAsPromised = require('chai-as-promised')
const chaiRoughly = require('chai-roughly')

global.before(() => {
  chaiActual.should()
  chaiActual.use(chaiAsPromised)
  chaiActual.use(chaiRoughly)
})

exports.getElectronPath = () => {
  let electronPath = path.join(__dirname, '../../..', 'node_modules', '.bin', 'electron')
  if (process.platform === 'win32') {
    electronPath += '.cmd'
  }
  return electronPath;
}

exports.setupTimeout = (test: Mocha) => {
  test.timeout(30000);
}

exports.startApplication = (options: ApplicationSettings) => {
  options.path = exports.getElectronPath()
  options.waitTimeout = 25000
  if (process.env.CI) {
    options.startTimeout = 30000
  }

  const app = new Application(options)
  return app.start().then(() => {
    assert.strictEqual(app.isRunning(), true)
    chaiAsPromised.transferPromiseness = app.transferPromiseness
    return app;
  })
}

exports.stopApplication = async (app) => {
  if (!app || !app.isRunning()) {
    return
  }

  await app.stop();
  assert.strictEqual(app.isRunning(), false);
}