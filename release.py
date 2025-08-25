#!/usr/bin/env python3
"""
Maven Central Release Script for junit-js

This script helps you release the junit-js library to Maven Central.
It loads credentials from .env file and provides a step-by-step CLI interface.
"""

import os
import subprocess
import sys
from pathlib import Path
import argparse

def mask_credential(value):
    """Mask credential with asterisks matching length"""
    if not value:
        return ""
    return '*' * len(value)

def load_env():
    """Load environment variables from .env file"""
    env_file = Path('.env')
    if not env_file.exists():
        print("❌ .env file not found. Please create it with your credentials.")
        print("Example .env content:")
        print("GPG_PASSPHRASE=your_gpg_passphrase")
        print("# For Central Publishing Portal (new):")
        print("CENTRAL_USERNAME=your_token_username") 
        print("CENTRAL_PASSWORD=your_token_password")
        print("# Legacy OSSRH (deprecated):")
        print("OSSRH_USERNAME=your_ossrh_username") 
        print("OSSRH_PASSWORD=your_ossrh_password")
        sys.exit(1)
    
    env_vars = {}
    with open(env_file) as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#') and '=' in line:
                key, value = line.split('=', 1)
                env_vars[key] = value
                os.environ[key] = value
    
    return env_vars

def run_command(cmd, description, check=True):
    """Run a command and handle errors"""
    print(f"\n🔄 {description}")
    print(f"Command: {cmd}")
    
    if input("Continue? (y/N): ").lower() != 'y':
        print("Skipped.")
        return False
    
    try:
        result = subprocess.run(cmd, shell=True, check=check, capture_output=True, text=True)
        if result.stdout:
            print("Output:", result.stdout)
        return True
    except subprocess.CalledProcessError as e:
        print(f"❌ Error: {e}")
        if e.stderr:
            print("Error output:", e.stderr)
        return False

def check_prerequisites():
    """Check that all prerequisites are met"""
    print("🔍 Checking prerequisites...")
    
    # Check Maven
    try:
        subprocess.run(['mvn', '--version'], check=True, capture_output=True)
        print("✅ Maven found")
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("❌ Maven not found. Please install Maven.")
        return False
    
    # Check GPG
    try:
        subprocess.run(['gpg', '--version'], check=True, capture_output=True)
        print("✅ GPG found")
    except (subprocess.CalledProcessError, FileNotFoundError):
        print("❌ GPG not found. Please install GPG.")
        return False
    
    # Check git status
    try:
        result = subprocess.run(['git', 'status', '--porcelain'], check=True, capture_output=True, text=True)
        if result.stdout.strip():
            print("❌ Working directory is not clean. Please commit or stash changes.")
            return False
        print("✅ Git working directory is clean")
    except subprocess.CalledProcessError:
        print("❌ Git error. Please check your git repository.")
        return False
    
    return True

def get_version():
    """Extract version from pom.xml"""
    try:
        result = subprocess.run(['mvn', 'help:evaluate', '-Dexpression=project.version', '-q', '-DforceStdout'], 
                              check=True, capture_output=True, text=True)
        return result.stdout.strip()
    except subprocess.CalledProcessError:
        print("❌ Could not extract version from pom.xml")
        return None

def check_central_access(env_vars):
    """Check if we have Central Portal credentials"""
    has_central = 'CENTRAL_USERNAME' in env_vars and 'CENTRAL_PASSWORD' in env_vars
    has_ossrh = 'OSSRH_USERNAME' in env_vars and 'OSSRH_PASSWORD' in env_vars
    
    if has_central:
        print("✅ Central Publishing Portal credentials found")
        print(f"   Username: {env_vars['CENTRAL_USERNAME']}")
        print(f"   Password: {mask_credential(env_vars['CENTRAL_PASSWORD'])}")
        return 'central'
    elif has_ossrh:
        print("⚠️  Only legacy OSSRH credentials found")
        print(f"   Username: {env_vars['OSSRH_USERNAME']}")
        print(f"   Password: {mask_credential(env_vars['OSSRH_PASSWORD'])}")
        print("   Migration to Central Portal required - see MIGRATION.md")
        return 'ossrh'
    else:
        print("❌ No publishing credentials found")
        return None

def main():
    parser = argparse.ArgumentParser(description='Release junit-js to Maven Central')
    parser.add_argument('--step', choices=['test', 'tag', 'deploy', 'all'], default='all',
                       help='Run specific step or all steps')
    parser.add_argument('--check-access', action='store_true', help='Check credential access method')
    parser.add_argument('--dry-run', action='store_true', help='Show commands without executing')
    parser.add_argument('--force-ossrh', action='store_true', help='Force use of legacy OSSRH (deprecated)')
    
    args = parser.parse_args()
    
    print("🚀 Maven Central Release Script for junit-js")
    print("=" * 50)
    
    # Load environment
    env_vars = load_env()
    print(f"✅ Loaded {len(env_vars)} environment variables from .env")
    
    # Show masked credentials for verification
    if 'GPG_PASSPHRASE' in env_vars:
        print(f"   GPG_PASSPHRASE: {mask_credential(env_vars['GPG_PASSPHRASE'])}")
    
    # Check access method
    access_method = check_central_access(env_vars)
    if not access_method:
        print("Please update .env with publishing credentials - see MIGRATION.md")
        sys.exit(1)
    
    # Handle check-access command
    if args.check_access:
        if access_method == 'central':
            print("✅ Ready for Central Publishing Portal")
        else:
            print("⚠️  Migration required - see MIGRATION.md")
        return
    
    # Check prerequisites
    if not check_prerequisites():
        sys.exit(1)
    
    # Get version
    version = get_version()
    if not version:
        sys.exit(1)
    print(f"📦 Current version: {version}")
    
    if args.dry_run:
        print("\n🔍 DRY RUN MODE - Commands will be shown but not executed")
    
    # Determine deployment method
    use_central = access_method == 'central' and not args.force_ossrh
    if use_central:
        print("🆕 Using Central Publishing Portal")
    else:
        print("⚠️  Using legacy OSSRH (deprecated)")
        if access_method == 'ossrh':
            print("   Consider migrating to Central Portal - see MIGRATION.md")
    
    # Step 1: Test build
    if args.step in ['test', 'all']:
        print("\n" + "="*50)
        print("STEP 1: Test Build")
        print("="*50)
        
        cmd = "mvn clean verify"
        if args.dry_run:
            print(f"Would run: {cmd}")
        else:
            if not run_command(cmd, "Running clean build and tests"):
                print("❌ Build failed. Please fix issues before continuing.")
                sys.exit(1)
    
    # Step 2: Create tag
    if args.step in ['tag', 'all']:
        print("\n" + "="*50)
        print("STEP 2: Create Release Tag")
        print("="*50)
        
        tag_name = f"v{version}"
        
        # Check if tag already exists
        try:
            subprocess.run(['git', 'rev-parse', tag_name], check=True, capture_output=True)
            print(f"⚠️  Tag {tag_name} already exists")
            if input("Delete existing tag and recreate? (y/N): ").lower() == 'y':
                cmds = [
                    f"git tag -d {tag_name}",
                    f"git push origin :refs/tags/{tag_name}",
                    f"git push bitbucket :refs/tags/{tag_name}"
                ]
                for cmd in cmds:
                    if args.dry_run:
                        print(f"Would run: {cmd}")
                    else:
                        run_command(cmd, f"Deleting existing tag {tag_name}", check=False)
            else:
                print("Skipping tag creation.")
                if args.step == 'tag':
                    return
        except subprocess.CalledProcessError:
            pass  # Tag doesn't exist, which is good
        
        cmds = [
            f'git tag -a {tag_name} -m "Release {tag_name}: JUnit 5 upgrade with vintage mode support"',
            f"git push origin {tag_name}",
            f"git push bitbucket {tag_name}"
        ]
        
        for cmd in cmds:
            if args.dry_run:
                print(f"Would run: {cmd}")
            else:
                if not run_command(cmd, f"Creating and pushing tag {tag_name}"):
                    sys.exit(1)
    
    # Step 3: Deploy to Maven Central
    if args.step in ['deploy', 'all']:
        print("\n" + "="*50)
        print("STEP 3: Deploy to Maven Central")
        print("="*50)
        
        print("⚠️  IMPORTANT: This will deploy to Maven Central!")
        print("Make sure:")
        print("- Your OSSRH credentials are correct")
        print("- You have permission to publish to org.bitbucket.thinbus namespace")
        print("- All tests pass")
        print("- The version number is correct")
        
        gpg_passphrase = env_vars.get('GPG_PASSPHRASE', '')
        
        if use_central:
            # Central Publishing Portal
            cmd = f'mvn deploy -Dgpg.skip=false -Dgpg.passphrase="{gpg_passphrase}" -Dgpg.pinentry-mode=loopback'
            display_cmd = f"mvn deploy -Dgpg.skip=false -Dgpg.passphrase={mask_credential(gpg_passphrase)} -Dgpg.pinentry-mode=loopback"
        else:
            # Legacy OSSRH (will likely fail)
            cmd = f'mvn deploy -Dgpg.skip=false -Dgpg.passphrase="{gpg_passphrase}" -Dgpg.pinentry-mode=loopback'
            display_cmd = f"mvn deploy -Dgpg.skip=false -Dgpg.passphrase={mask_credential(gpg_passphrase)} -Dgpg.pinentry-mode=loopback (LEGACY OSSRH)"
        
        if args.dry_run:
            print(f"Would run: {display_cmd}")
        else:
            if not run_command(cmd, f"Deploying to {'Central Portal' if use_central else 'Maven Central (legacy)'}"):
                print("❌ Deployment failed. Check the error messages above.")
                if use_central:
                    print("Common issues:")
                    print("- Authentication: Check Central Portal token credentials")
                    print("- Namespace: Verify access to org.bitbucket.thinbus")
                    print("- GPG signing: Check GPG passphrase and key setup")
                else:
                    print("Legacy OSSRH is deprecated and may no longer work.")
                    print("Please migrate to Central Portal - see MIGRATION.md")
                sys.exit(1)
    
    # Create GitHub release
    if args.step in ['all']:
        print("\n" + "="*50)
        print("STEP 4: Create GitHub Release")
        print("="*50)
        
        tag_name = f"v{version}"
        release_notes = f"""## Release {version}: JUnit 5 Upgrade with Vintage Mode Support

This release upgrades the project to JUnit 5 while maintaining backward compatibility through vintage mode support.

### Key Features
- Full JUnit 5.10.0 support with JUnit 4 vintage engine
- Enhanced Maven Surefire configuration (3.2.5)
- Improved compiler warning detection and enforcement
- Updated documentation and developer guides

### Requirements
- Java 21 or higher
- Maven 3.6+ or Gradle 7+

### Maven Dependency
```xml
<dependency>
    <groupId>org.bitbucket.thinbus</groupId>
    <artifactId>junit-js</artifactId>
    <version>{version}</version>
</dependency>
```

This release maintains full compatibility with existing JavaScript test suites while providing access to modern JUnit 5 features."""
        
        cmd = f'gh release create {tag_name} --title "Release {tag_name}: JUnit 5 Upgrade with Vintage Mode Support" --notes "{release_notes}"'
        
        if args.dry_run:
            print(f"Would run: gh release create {tag_name} --title ... --notes ...")
        else:
            if not run_command(cmd, "Creating GitHub release"):
                print("⚠️  GitHub release creation failed, but this is not critical.")
    
    print("\n🎉 Release process completed!")
    print(f"📦 Version {version} should now be available on Maven Central")
    print("🔗 Check status at: https://central.sonatype.com/artifact/org.bitbucket.thinbus/junit-js")

if __name__ == '__main__':
    main()