#!/bin/bash
# PostToolUse hook: remind to sync CLAUDE.md after README.md / build.gradle are edited.
f=$(jq -r '.tool_input.file_path // empty' | grep -E '(^|/)(README\.md|build\.gradle)$') || exit 0
msg="$f changed. Check CLAUDE.md against it and update the affected lines per its 'Keeping this file current' section."
jq -n --arg m "$msg" '{hookSpecificOutput:{hookEventName:"PostToolUse",additionalContext:$m}}'
