<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# Chrome Web Store Publishing Setup

## Overview

The `release-all.sh` script publishes Chrome extensions to the Chrome Web Store using the Chrome Web Store API.

## Required Credentials

You need 4 environment variables:

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

Get this from Chrome Web Store Developer Dashboard:

1. Go to https://chrome.google.com/webstore/devconsole
2. Find the KIE Editors extension
3. Copy the extension ID from the URL

## Environment Variables

Set these before running the release script with `--publish`:

```bash
export CHROME_CLIENT_ID="your-client-id.apps.googleusercontent.com"
export CHROME_CLIENT_SECRET="your-client-secret"
export CHROME_REFRESH_TOKEN="your-refresh-token"
export CHROME_KIE_EDITORS_EXTENSION_ID="kie-editors-extension-id"
```

## Usage

### Test Build (No Publishing)

```bash
./scripts/release/release-all.sh 10.3.0
```

### Publish to Chrome Web Store

```bash
# Set credentials
export CHROME_CLIENT_ID="..."
export CHROME_CLIENT_SECRET="..."
export CHROME_REFRESH_TOKEN="..."
export CHROME_KIE_EDITORS_EXTENSION_ID="..."

# Publish
./scripts/release/release-all.sh 10.3.0 --publish
```

## Jenkins Setup

In Jenkins, credentials are configured in `.ci/jenkins/Jenkinsfile.103xplus.release-publish` using:

- `CHROME_CLIENT_ID` / `CHROME_CLIENT_SECRET`: `chromeStoreCredentialsId`
- `CHROME_REFRESH_TOKEN`: `chromeStoreRefreshTokenCredentialsId`
- `CHROME_KIE_EDITORS_EXTENSION_ID`: `chromeExtensionIdCredentialsId`

## How It Works

The script:

1. **Gets OAuth Token**: Exchanges refresh token for access token via `https://oauth2.googleapis.com/token`
2. **Uploads Extension**: Uploads the `.zip` file to Chrome Web Store API
3. **Publishes Extension**: Publishes the uploaded item and validates API response status (`OK` or `PUBLISHED_WITH_FRICTION_WARNING`)

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
# Build only (safe) — builds Chrome extension without publishing
./scripts/release/release-all.sh 0.0.0-test
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

./scripts/release/release-all.sh 0.0.0-test --publish
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
