module.exports = {
  collectCoverageFrom: ['src/*.{ts,tsx}'],
  moduleNameMapper: {
    '\\.(css|less)$': '<rootDir>/__mocks__/styleMock.js',
    '^uniforms$': '<rootDir>/node_modules/uniforms/src',
    '^uniforms-bridge-simple-schema-2$': '<rootDir>/node_modules/uniforms-bridge-simple-schema-2/src',
    '^uniforms-patternfly$': '<rootDir>/src',
  },
  setupFiles: ['<rootDir>/setupEnzyme.js'],
  testMatch: ['**/__tests__/**/!(_)*.{ts,tsx}', '!**/*.d.ts', '!**/helpers/*.ts'],
  moduleDirectories: [
    "node_modules",
    "<rootDir>/src"
  ],
  preset: "ts-jest",
  transformIgnorePatterns: ["node_modules/(?!uniforms)"],
  transform: {
    '^.+\\.(js|ts|tsx)$': './transform.js'
  }
};