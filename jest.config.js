module.exports = {
  collectCoverageFrom: ['src/*.{ts,tsx}'],
  moduleNameMapper: {
    '\\.(css|less)$': '<rootDir>/__mocks__/styleMock.js',
    '^simpl-schema$': '<rootDir>/node_modules/simpl-schema',
    '^uniforms-patternfly$': '<rootDir>/src',
  },
  setupFiles: ['<rootDir>/setupEnzyme.js'],
  testMatch: ['**/__tests__/**/!(_)*.{ts,tsx}', '!**/*.d.ts'],
  moduleDirectories: [
    "node_modules",
    "<rootDir>/src"
  ],
  transform: {
    '^.+\\.(js|ts|tsx)$': './transform.js'
  }
};