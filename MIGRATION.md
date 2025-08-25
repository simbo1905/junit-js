# Migration to Central Publishing Portal

The OSSRH service will reach end-of-life on June 30th, 2025. This project needs to migrate from the legacy OSSRH system to the new Central Publishing Portal.

## Current Status

✅ **Confirmed**: Legacy OSSRH deployment fails with 403 Forbidden error  
❌ **Required**: Migration to Central Publishing Portal for future releases

## Manual Steps Required

### 1. Account Migration
- Visit https://central.sonatype.com/
- Log in with your existing Sonatype JIRA credentials (simbo1905)
- If login fails, create a new account and request namespace verification
- Verify you can access the `org.bitbucket.thinbus` namespace

### 2. Generate Publishing Token
- In Central Portal, go to "View Account" → "Generate User Token"
- Save the username and password token for Maven authentication
- Update `.env` file with new credentials:
  ```
  CENTRAL_USERNAME=your_token_username
  CENTRAL_PASSWORD=your_token_password
  GPG_PASSPHRASE=Wha7passingbells?
  ```

### 3. Namespace Verification (if needed)
If you don't have access to `org.bitbucket.thinbus`:
- Request namespace verification in Central Portal
- Provide proof of ownership of bitbucket.org/thinbus domain
- Wait for approval (usually 1-2 business days)

## Technical Changes Made

The `release.py` script has been updated to support both legacy OSSRH and new Central Portal:

### New Central Portal Configuration
- **Endpoint**: `https://central.sonatype.com/api/v1/publisher/`
- **Authentication**: User token (not JIRA credentials)
- **Process**: Direct upload via REST API or Maven plugin

### Updated Maven Configuration
The `pom.xml` now includes dual configuration:
- Legacy OSSRH (for reference)
- New Central Portal endpoints
- Updated `central-publishing-maven-plugin`

## Testing the Migration

1. **Verify Access**: 
   ```bash
   python3 release.py --check-access
   ```

2. **Dry Run**:
   ```bash
   python3 release.py --dry-run
   ```

3. **Deploy**:
   ```bash
   python3 release.py --step deploy
   ```

## Rollback Plan

If Central Portal deployment fails:
1. The v2.0.0 GitHub release is already created
2. Artifacts can be manually uploaded via Central Portal web interface
3. Legacy OSSRH configuration is preserved in `pom.xml` comments

## Next Steps After Migration

1. Test successful deployment to Central Portal
2. Verify artifacts appear on Maven Central
3. Update documentation to reflect new process
4. Remove this MIGRATION.md file
5. Clean up legacy OSSRH configuration from `pom.xml`

## Support Resources

- **Central Portal Docs**: https://central.sonatype.org/
- **Migration Guide**: https://central.sonatype.org/faq/what-is-different-between-central-portal-and-legacy-ossrh/
- **Support**: https://central.sonatype.org/support/