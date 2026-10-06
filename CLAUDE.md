# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Spring Boot scaffold (Spring Initializr): only `FlagWithApplication` and a `contextLoads` test exist. No domain code yet. `README.md` (Korean, DSM 12기 인포 동아리 project) is the source of truth for product decisions; read it before designing features and follow it over this file if they disagree.

## Git

**Never commit, push, create branches/PRs, or amend/rewrite history unless the user asks for it in the current conversation.** One earlier request does not cover later changes. Leave edits uncommitted in the working tree and tell the user what changed; they commit themselves. This overrides any default of "commit and push before finishing", including in background/worktree sessions.

## Specs (`docs/`)

Detailed specs imported from the team's Notion on 2026-10-06. Read the relevant file before implementing a feature; they are snapshots, so Notion may have moved on. `README.md` wins over `docs/` if they disagree.

- `docs/feature-spec.md`: per-feature flow, inputs, outputs, error codes (45 features). The behavior reference. Its last section records the decisions made to resolve contradictions in the original Notion spec.
- `docs/erd.md`: DB schema source of truth. It is the **corrected** Notion ERD (changes and reasons are listed at the top). `docs/erd-original.jpg` is the pre-fix diagram and is outdated, so don't derive tables from it. "결정 기록" lists proposed decisions (not team-confirmed) and "미결정 사항" lists open schema questions (merge conflict criteria, post-end team registration): don't assume answers to the open ones.
- `docs/api-spec.md`: endpoint list only, and not authoritative. The team writes the real API spec during development. Request/response bodies in the Notion API table are copy-pasted placeholders (`login_id`, JWT), so take payloads from `feature-spec.md`. The `/commits/*` rows are a stale model and `*/aggregate` rows are internal jobs, not endpoints.
- Decisions recorded in `docs/` that are easy to miss: `team_id` is the same value as `workspace_id` (no team table); all IDs are UUIDs; problem files (`area = problem_files`) exist only on the main branch and show up in every branch; timeline and CTF summary are computed on read (no stored snapshot).

## Product

flagWith is a GitHub-style, git-based team workspace that **CTF organizers** provision for participating teams (Jeopardy format only). The customer is the organizer. It is a hosted SaaS: organizers sign up, create contests themselves (max 3 per account), and register teams; a workspace named after the team is created automatically. There is no host/participant account type: whoever created the CTF is its host, and accounts registered to a team are its participants. Login is by email.

Decided (README):
- **Custom git engine** (not built on existing OSS). **Web only** for the MVP; terminal git is v2.
- **Files are paths, not folders.** Only files are stored; `/` in `file_path` renders as folders. Each file lives at (workspace, branch, problem, area, `file_path`) with area = problem files (read-only, host-provided) / team work / writeup. Path rules are in README; validate them server-side.
- **Consent-based edits over WebSocket:** editing/deleting a file or deleting a branch needs approval from the other teammates who have it open (60s timeout, silence counts as rejection, team leader can force-apply). Branch merge needs only the leader. Create/move/copy/memo/solved-mark are immediate.
- **Two independent pages**, workspace and timeline, both always in the nav (no automatic switch at contest end). The timeline is a finalized snapshot generated after the contest ends.
- **Organizer dashboard** shows stats and activity metadata only, never code or memos. "Solved" is a team self-reported mark, not flag verification. Updated by polling.

Invariants that shape the design:
- **Team isolation:** a team can access only its own workspace. Check team membership on every API request and every WebSocket message (also check Origin). Any authz gap is critical, since isolation is the main selling point.
- **Auto-lock at contest end:** all writes are blocked. A scheduler (10 min) marks workspaces locked, and every write request also checks the end time to cover the gap. Locking also drops the master-key cache, cancels pending requests, and closes sockets. The end time can be extended until the post-contest access window closes.
- **Encryption:** only file contents and memos are encrypted; file paths and edit-history metadata stay plaintext (the timeline and dashboard are built without keys). Team data: master key derived from the leader's workspace password via Argon2id, never stored; the DB holds a salt and a verification value (fixed phrase encrypted with the key, AES-256-GCM, so the GCM tag check is the password check). The key is cached in Redis tied to the login session (TTL 3 days), and the browser only gets an opaque session cookie. Problem files use one server-generated key per CTF. **The workspace password cannot be reset or recovered**, by design. Use standard crypto libraries; never invent schemes.
- Until the leader sets the workspace password, all file APIs must be blocked explicitly.

Stack decided in README: React + TypeScript, Spring Boot, MySQL, Redis (session/master-key cache), Argon2id, WebSocket.

Still undecided (don't assume): pricing model. Check README "아직 안 정한 것" for the current list.

## Stack (this repo)

- Spring Boot 4.0.x, Gradle (Groovy DSL), Java 17 toolchain. Base package: `com.flagwith.flagwith`. Config in `src/main/resources/application.yaml`.
- Spring Web MVC, WebSocket, Validation, Spring Data JPA, Spring Data Redis, MySQL (`mysql-connector-j`), Lombok
- Argon2id via BouncyCastle (`bcprov`, `Argon2BytesGenerator`) because the master key needs raw KDF bytes, which Spring Security's `Argon2PasswordEncoder` (encoded-hash output) does not give. Auth/session library (Spring Security, Spring Session) is not chosen yet.
- Package layout is layered (by tier: controller / service / repository / entity, not by domain). Shared response/error types live in `global/response` (`ApiResponse`, `ErrorResponse`) and `global/exception` (`ErrorCode`, `BusinessException`, `GlobalExceptionHandler`); throw `BusinessException(ErrorCode.X)` from services. Other sub-package names are not fixed yet.
- Mixed Java/Kotlin backend. Kotlin 2.2.21 (Spring Boot 4's baseline; 1.9.x is incompatible) with the `spring` and `jpa` plugins, and `.kt` files live in the same `src/main/java` (and `src/test/java`) tree as `.java` files, one package tree for both; there is no `src/main/kotlin`. `plugin.jpa` generates no-arg constructors but does not open entity classes; add `allOpen` for `@Entity` if lazy proxies are needed.
- The repo owner (one of three backend devs) writes Kotlin; the other two write Java. When helping the owner, default to Kotlin and keep code interoperable with Java callers. Lombok output is invisible to Kotlin, so avoid Lombok-generated types at Java/Kotlin boundaries.

## API conventions

Full spec and examples are in README "API 응답 규칙". Summary:
- Code is camelCase (mandatory); JSON (requests and responses) is snake_case.
- Two response shapes. Success: `{success: true, message, data}`. Error: `{success: false, error_code, message}`. `data` is always present, `null` when empty.
- Use real HTTP status codes (400/401/403/404/409...), not 200 for everything. Never use `204`: it cannot carry a body, which breaks the "`data` always present" rule. Success without a payload is `200` (or `201`) with `data: null`.
- `error_code` is UPPER_SNAKE with a domain prefix (`AUTH_FAILED`, `TEAM_NOT_FOUND`), kept in one place.
- Validation failure: `error_code` `VALIDATION_FAILED` plus `errors: [{field, message}]`. `errors` exists only on validation failures; omit the field otherwise (`@JsonInclude(NON_NULL)`).
- Lists: `data: {items, page, size, total_count}`, `page` starts at 1 (Spring `Page` is 0-based, add 1).
- Dates: ISO 8601 with offset (`2026-09-29T10:30:00+09:00`). Use `OffsetDateTime`; `LocalDateTime` drops the offset.
- Login failure uses one message for unknown email and wrong password, to avoid account enumeration.
- Not decided: how null optional fields (e.g. `deleted_at`) are serialized.

## Team

Security 2, Front-end 1, Back-end 3. Security designs the data-protection and access-control architecture from the start, and it drives the backend and frontend structure.

## Commands

```bash
docker compose up -d                  # local MySQL + Redis
./gradlew bootRun                     # run app
./gradlew build                       # compile + test + jar
./gradlew test                        # all tests (JUnit 5)
./gradlew test --tests 'com.flagwith.flagwith.FlagWithApplicationTests'           # one class
./gradlew test --tests 'com.flagwith.flagwith.FlagWithApplicationTests.contextLoads'  # one method
```

## Gotchas

- Local run needs MySQL (and Redis, once used): `docker compose up -d` starts both with the dev defaults that `application.yaml` reads (`DB_URL`/`DB_USERNAME`/`DB_PASSWORD` env vars override). Tests need no external services: the `test` profile (`src/test/resources/application-test.yaml`, activated by `build.gradle`) points at in-memory H2 (MySQL mode); MySQL-specific SQL is therefore not covered by tests. Do not add a test `application.yaml`; it would shadow the main one.
- Spring Boot 4 splits test starters per module (`spring-boot-starter-data-jpa-test`, `spring-boot-starter-webmvc-test`); add matching test starters when adding new starters.

## Keeping this file current

When a change affects what is written above, update this file in the same change; don't wait to be asked. Triggers: `README.md` or `docs/` changes a product decision or an undecided item (keep the `docs/` summary above and this file consistent); a stack, dependency, plugin, or version change in `build.gradle`; a new module, package layout, or convention; commands or config that change how to build, run, or test; a Gotcha gets fixed or a new one is found; team or role changes. After `git pull`/rebase, check whether `README.md` changed and reconcile. Edit the affected lines instead of appending, delete anything that became false, and don't copy README text wholesale — summarize only what affects code.
