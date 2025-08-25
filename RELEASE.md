# Release Process

## Prerequisites

### 1. Configure Maven Settings
Add Central Portal credentials to `~/.m2/settings.xml`:
```xml
<server>
    <id>central</id>
    <username>your_token_username</username>
    <password>your_token_password</password>
</server>
```
**Important**: Use `<id>central</id>` (not `${server}`) to match the pom.xml configuration.

### 2. GPG Key
- GPG key configured for signing (should be in settings.xml too)
- Clean git working directory

## Release Commands

### 1. Run Tests
```bash
mvn clean test
```

### 2. Deploy to Maven Central
```bash
mvn clean deploy
```

That's it! The Central Publishing Portal plugin handles:
- GPG signing (using your configured GPG key)
- Authentication (using settings.xml server config)
- Automatic publishing to Maven Central

## Verify Release
Check https://central.sonatype.com/artifact/org.bitbucket.thinbus/junit-js for the new version.

## Troubleshooting
If deploy fails with "Cannot invoke Server.clone() because server is null":
- Check that your `~/.m2/settings.xml` has `<id>central</id>` (not `${server}`)
- Verify the server block is inside `<servers>` section