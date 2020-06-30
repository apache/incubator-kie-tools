import { ApplicationSettings, Application } from "spectron";
import * as path from 'path';
import * as assert from 'assert';
import * as chai from 'chai';
import * as chaiAsPromised from 'chai-as-promised';
import * as chaiRoughly from 'chai-roughly';

before(() => {
  chai.should()
  chai.use(chaiAsPromised)
  chai.use(chaiRoughly)
})

exports.getElectronPath = (): string => {
  let electronPath: string = path.join(__dirname, '../../..', 'node_modules', '.bin', 'electron')
  if (process.platform === 'win32') {
    electronPath += '.cmd'
  }
  return electronPath;
}

exports.setupTimeout = (test: Mocha): void => {
  test.timeout(30000);
}

exports.startApplication = (options: ApplicationSettings): Promise<Application> => {
  options.path = exports.getElectronPath()
  if (process.env.CI) {
    options.startTimeout = 30000
  }

  const app = new Application(options)
  return app.start().then(() => {
    assert.strictEqual(app.isRunning(), true);
    return app;
  })
}

exports.stopApplication = async (app: Application): Promise<void> => {
  if (!app || !app.isRunning()) {
    return;
  }

  await app.stop();
  assert.strictEqual(app.isRunning(), false);
}