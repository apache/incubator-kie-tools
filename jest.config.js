module.exports = {
  // collectCoverageFrom: ['src/*.{ts,tsx}'],
  // coverageReporters: ['html', 'lcovonly', 'text-summary'],
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
    '^.+\\.(js|ts|tsx)$': './scripts/transform.js'
  }
};

// module.exports = {
//   // Automatically clear mock calls and instances between every test
//   clearMocks: true,

//   // Indicates whether the coverage information should be collected while executing the test
//   collectCoverage: true,

//   // The directory where Jest should output its coverage files
//   coverageDirectory: 'coverage',

//   // An array of directory names to be searched recursively up from the requiring module's location
//   moduleDirectories: [
//     "node_modules",
//     "<rootDir>/src"
//   ],

//   // An array of file extensions your modules use
//   moduleFileExtensions: [
//     "ts",
//     "tsx",
//     "js"
//   ],

//   // A map from regular expressions to module names that allow to stub out resources with a single module
//   moduleNameMapper: {
//     'uniforms-unstyled': '<rootDir>/src/'
// //       '<rootDir>/packages/uniforms/__mocks__/meteor/$1_$2.ts',
//     // '\\.(css|less)$': '<rootDir>/__mocks__/styleMock.js',
//     // "\\.(jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2|mp4|webm|wav|mp3|m4a|aac|oga)$": "<rootDir>/__mocks__/fileMock.js",
//     // "@app/(.*)": '<rootDir>/src/app/$1'
//   },

//   // setupFiles: ['./scripts/setupEnzyme.js'],

//   // A preset that is used as a base for Jest's configuration
//   preset: "ts-jest/presets/js-with-ts",

//   // The path to a module that runs some code to configure or set up the testing framework before each test
//   setupFilesAfterEnv: ['<rootDir>/test-setup'],

//   // The test environment that will be used for testing.
//   testEnvironment: "jsdom",

//   // A list of paths to snapshot serializer modules Jest should use for snapshot testing
//   // snapshotSerializers: ['enzyme-to-json/serializer'],

//   // The glob patterns Jest uses to detect test files
//   testMatch: [
//     '**/__tests__/**/!(_)*.{ts,tsx}', '!**/*.d.ts'
//   ],

//   // A map from regular expressions to paths to transformers
//   transform: {
//     "^.+\\.(ts|tsx)$": "ts-jest"
//   }
// };