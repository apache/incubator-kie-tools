module.exports = {
  collectCoverageFrom: ['src/*.{ts,tsx}'],
  moduleNameMapper: {
    '\\.(css|less)$': '<rootDir>/__mocks__/styleMock.js',
    '^meteor/([^:]*):(.*)$':
      '<rootDir>/packages/uniforms/__mocks__/meteor/$1_$2.ts',
    '^meteor/([^:]*)$': '<rootDir>/packages/uniforms/__mocks__/meteor/$1.ts',
    '^simpl-schema$': '<rootDir>/node_modules/simpl-schema'
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