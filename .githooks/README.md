# Git Hooks

This directory contains shared git hooks for the DFL Manager Online project.

## Installation

Run the setup script to install the hooks:

```bash
.githooks/setup.sh
```

Or manually:

```bash
git config core.hooksPath .githooks
chmod +x .githooks/*
```

## Available Hooks

### pre-push

Runs Maven tests before allowing a push. This ensures:
- All tests pass before code is pushed to the remote
- No broken code enters the repository

**What it does:**
1. Runs `mvn test -q`
2. If tests fail, blocks the push
3. If tests pass, allows the push to proceed

**Skipping the hook (not recommended):**
```bash
git push --no-verify
```

## Testing the Hook

To test the pre-push hook without pushing:

```bash
.githooks/pre-push
```

## Troubleshooting

**Hook not running:**
- Make sure you ran the setup script: `.githooks/setup.sh`
- Verify hooks are executable: `ls -la .githooks/`
- Check git config: `git config core.hooksPath`
