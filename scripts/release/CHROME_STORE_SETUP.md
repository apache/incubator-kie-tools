# Chrome Web Store Publishing Setup

## Overview

The `release-chrome-extensions.sh` script can now publish extensions to the Chrome Web Store using the Chrome Web Store API.

## Required Credentials

You need 5 environment variables:

### 1. OAuth Credentials

Get these from Google Cloud Console:

1. Go to https://console.cloud.google.com/
2. Create a project (or use existing)
3. Enable "Chrome Web Store API"
4. Create OAuth 2.0 credentials:
   - Application type: "Web application"
   - Add authorized redirect URI: `http://localhost`
5. Note the **Client ID** and **Client Secret**

### 2. Refresh Token

Generate a refresh token:

```bash
# 1. Get authorization code
# Open this URL in browser (replace CLIENT_ID):
https://accounts.google.com/o/oauth2/auth?response_type=code&scope=https://www.googleapis.com/auth/chromewebstore&client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost

# 2. After authorizing, you'll be redirected to:
# http://localhost/?code=AUTHORIZATION_CODE
# Copy the AUTHORIZATION_CODE

# 3. Exchange code for refresh token:
curl -X POST https://oauth2.googleapis.com/token \
  -d "client_id=YOUR_CLIENT_ID" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "code=AUTHORIZATION_CODE" \
  -d "grant_type=authorization_code" \
  -d "redirect_uri=http://localhost"

# Response will include "refresh_token" - save this!
```

### 3. Extension IDs

Get these from Chrome Web Store Developer Dashboard:

1. Go to https://chrome.google.com/webstore/devconsole
2. Find your extensions
3. Copy the extension IDs from the URLs

## Environment Variables

Set these before running the script:

```bash
export CHROME_CLIENT_ID="your-client-id.apps.googleusercontent.com"
export CHROME_CLIENT_SECRET="your-client-secret"
export CHROME_REFRESH_TOKEN="your-refresh-token"
export CHROME_KIE_EDITORS_EXTENSION_ID="kie-editors-extension-id"
export CHROME_SWF_EDITOR_EXTENSION_ID="swf-editor-extension-id"
```

## Usage

### Test Build (No Publishing)

```bash
./scripts/release/release-chrome-extensions.sh 1.0.0
```

### Publish to Chrome Web Store

```bash
# Set credentials
export CHROME_CLIENT_ID="..."
export CHROME_CLIENT_SECRET="..."
export CHROME_REFRESH_TOKEN="..."
export CHROME_KIE_EDITORS_EXTENSION_ID="..."
export CHROME_SWF_EDITOR_EXTENSION_ID="..."

# Publish
./scripts/release/release-chrome-extensions.sh 1.0.0 --publish
```

## Jenkins Setup

In Jenkins, store credentials securely:

1. Go to "Manage Jenkins" → "Manage Credentials"
2. Add credentials:
   - `chrome-client-id` (Secret text)
   - `chrome-client-secret` (Secret text)
   - `chrome-refresh-token` (Secret text)
   - `chrome-kie-editors-extension-id` (Secret text)
   - `chrome-swf-editor-extension-id` (Secret text)

3. Update Jenkinsfile to use credentials:

```groovy
stage('Publish Chrome Extensions') {
    steps {
        withCredentials([
            string(credentialsId: 'chrome-client-id', variable: 'CHROME_CLIENT_ID'),
            string(credentialsId: 'chrome-client-secret', variable: 'CHROME_CLIENT_SECRET'),
            string(credentialsId: 'chrome-refresh-token', variable: 'CHROME_REFRESH_TOKEN'),
            string(credentialsId: 'chrome-kie-editors-extension-id', variable: 'CHROME_KIE_EDITORS_EXTENSION_ID'),
            string(credentialsId: 'chrome-swf-editor-extension-id', variable: 'CHROME_SWF_EDITOR_EXTENSION_ID')
        ]) {
            sh './scripts/release/release-chrome-extensions.sh ${VERSION} --publish'
        }
    }
}
```

## How It Works

The script:

1. **Gets OAuth Token**: Exchanges refresh token for access token
2. **Uploads Extension**: Uploads the .zip file to Chrome Web Store
3. **Publishes Extension**: Makes the extension live
4. **Repeats**: Does this for both extensions (KIE Editors and SWF Editor)

## API Endpoints Used

- **Token**: `https://oauth2.googleapis.com/token`
- **Upload**: `https://www.googleapis.com/upload/chromewebstore/v1.1/items/{extensionId}`
- **Publish**: `https://www.googleapis.com/chromewebstore/v1.1/items/{extensionId}/publish`

## Error Handling

The script will:

- ✅ Check for required credentials before starting
- ✅ Validate upload success before publishing
- ✅ Show detailed error messages
- ✅ Exit with error code if anything fails

## Testing

### Test with Dry Run

```bash
# Build only (safe)
./scripts/release/release-chrome-extensions.sh 0.0.0-test
```

### Test with Test Extension

1. Create a test extension in Chrome Web Store
2. Get its extension ID
3. Use test credentials
4. Publish to test extension

```bash
export CHROME_CLIENT_ID="test-client-id"
export CHROME_CLIENT_SECRET="test-secret"
export CHROME_REFRESH_TOKEN="test-token"
export CHROME_KIE_EDITORS_EXTENSION_ID="test-extension-id"
export CHROME_SWF_EDITOR_EXTENSION_ID="test-extension-id-2"

./scripts/release/release-chrome-extensions.sh 0.0.0-test --publish
```

## Troubleshooting

### "Failed to get access token"

- Check CLIENT_ID and CLIENT_SECRET are correct
- Check REFRESH_TOKEN is valid (they can expire)
- Regenerate refresh token if needed

### "Upload failed"

- Check extension ID is correct
- Check you have permission to upload to this extension
- Check the .zip file is valid

### "Publish status: ITEM_NOT_UPDATABLE"

- Extension might be in review
- Wait for previous version to be published
- Check Chrome Web Store dashboard

## Security Notes

- ⚠️ Never commit credentials to git
- ✅ Use Jenkins credentials manager
- ✅ Rotate tokens regularly
- ✅ Use separate credentials for test/production

## References

- [Chrome Web Store API Documentation](https://developer.chrome.com/docs/webstore/api_index/)
- [OAuth 2.0 for Web Server Applications](https://developers.google.com/identity/protocols/oauth2/web-server)
