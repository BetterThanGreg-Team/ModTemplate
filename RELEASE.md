# Release Process

This project uses **tag-based releases** with automated publishing to Modrinth, CurseForge, and GitHub Releases via GitHub Actions. Changelogs are generated automatically from pull requests.

## How It Works

1. **Development** happens on the `dev` branch via pull requests.
2. **When ready to release**, create a tag (e.g., `0.1.0` or `v0.1.0`) on the `main` branch.
3. **GitHub Actions** triggers the `Release` workflow which:
   - Builds the mod with the version from the tag
   - Generates a changelog from all PRs merged since the last tag
   - Creates a GitHub Release (with auto-generated release notes)
   - Publishes the jar to Modrinth and CurseForge

## Prerequisites (One-Time Setup)

### 1. Repository Secrets (API Tokens)

Add these as **repository secrets** in GitHub (`Settings → Secrets and variables → Actions`):

| Secret | Description | Where to get it |
|--------|-------------|-----------------|
| `MODRINTH_TOKEN` | Modrinth API token | https://modrinth.com/settings/tokens |
| `CURSEFORGE_TOKEN` | CurseForge API token | https://console.curseforge.com/ → API Keys |

### 2. Repository Variables (Project IDs)

Add these as **repository variables** (same page, click the "Variables" tab):

| Variable | Description | Where to find it |
|----------|-------------|-----------------|
| `MODRINTH_ID` | Your Modrinth project ID | URL of your project page: `https://modrinth.com/mod/<id>` |
| `CURSEFORGE_ID` | Your CurseForge project ID | Your project's About page → "ID" field |

## Creating a Release

### Step-by-Step

1. **Merge all changes** for the release into `main` from `dev`:
   ```bash
   git checkout main
   git merge dev
   git push origin main
   ```

2. **Create a tag** with the new version:
   ```bash
   git tag 0.1.0
   git push origin 0.1.0
   ```
   You can also use a `v` prefix — both `0.1.0` and `v0.1.0` work.

3. **The GitHub Action runs automatically.** Go to the Actions tab to watch the progress.

4. **GitHub generates release notes** from all PRs merged since the last tag — no manual changelog writing needed.

### Version Types

The workflow detects the version type from the tag name:

| Tag example | Version type | GitHub Release | Notes |
|-------------|-------------|----------------|-------|
| `0.1.0` | `release` | Full release | Stable public release |
| `0.2.0-alpha.1` | `alpha` | Pre-release | For alpha testers |
| `0.2.0-beta.1` | `beta` | Pre-release | For beta testers |
| `0.2.0-rc.1` | `beta` | Pre-release | Release candidate |

### Quick Script

Save this as `release.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
VERSION="$1"
git checkout main
git merge dev
git push origin main
git tag "$VERSION"
git push origin "$VERSION"
echo "Release $VERSION triggered — watch it at: https://github.com/BetterThanGreg-Team/ModTemplate/actions"
```

Usage: `./release.sh 0.1.0`

## Changelog Generation

Changelogs are generated automatically by GitHub's **auto-generated release notes** feature. This means:

- Every PR merged into `main` (or the branch you tag) appears in the changelog
- PRs are grouped by labels (e.g., `enhancement`, `bug`, `documentation`)
- The changelog compares against the **previous tag** — so tagging `0.2.0` after `0.1.0` shows everything merged between those two tags

To make the changelog useful:

- **Use descriptive PR titles** — these become the changelog entries
- **Use GitHub labels** on PRs (`enhancement`, `bug`, `dependencies`, etc.) — these group the changelog into sections
- **Use `documentation` label** for docs-only PRs

## Customizing Metadata

The workflow file is at `.github/workflows/release.yml`. Common things to change:

| Setting | Location (in `release.yml`) | Default |
|---------|----------------------------|---------|
| Java version | `java-version: '21'` | 21 |
| Mod loaders | `loaders: neoforge` | neoforge |
| Game versions | `game-versions: 1.21.1` | 1.21.1 |

### Changing Game Version or Loader

Edit these lines in `.github/workflows/release.yml`:

```yaml
loaders: neoforge
game-versions: 1.21.1
java: 21
```

Supported loaders: `forge`, `neoforge`, `fabric`, `quilt`.

## Troubleshooting

### Release workflow didn't trigger
- Ensure the tag was pushed to GitHub, not just created locally
- Check the tag pattern matches `v[0-9]*` or `[0-9]*` (e.g., `0.1.0`, `v1.0.0`)
- Verify the workflow file is on the branch where the tag is

### Publishing to Modrinth/CurseForge failed
- Verify the API tokens are set correctly in repository secrets
- Make sure the project IDs match your Modrinth/CurseForge projects
- Check the workflow run logs for specific error messages

### Wrong version in the jar
- The `VERSION` environment variable is set from the tag during build
- Check `build.gradle.kts` uses `System.getenv("VERSION")` — the template already does this
