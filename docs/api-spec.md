# API 명세서

> Notion `API 명세서` DB(26개 행)를 2026-10-06에 가져온 것이다.
> **이 표의 경로·메서드는 목록일 뿐이고 요청·응답 본문은 신뢰할 수 없다.** 아래 [상태](#상태) 참고.
> 요청·응답의 실제 필드와 오류 코드는 [`feature-spec.md`](feature-spec.md)가 기준이고, 응답 형식·이름 규칙은 `README.md` "API 응답 규칙"(요약은 CLAUDE.md)이 기준이다.

## 상태

Notion의 API 명세서 페이지 26개 중 **`/api/auth/register` 하나만 실제로 작성된 내용**이다. 나머지는 `register` 템플릿을 그대로 복사해 둔 자리표시자라서 경로 이름만 의미가 있다. 확인한 내용:

- 요청 본문이 모두 `login_id` / `password` / `nickname`이다 (이 프로젝트의 로그인 식별자는 **이메일**이고 `login_id`는 없다).
- 응답이 `201 회원가입 성공` + `access_token`/`refresh_token`(JWT)이다 (인증은 **세션 쿠키 `SID`**로 확정).
- 오류가 `{ "error": { "code": "BADREQUEST", ... } }`이다 (확정 형식은 `{ success: false, error_code, message }`).
- 일부 JSON은 문법 오류도 있다 (쉼표 누락 등).

`register` 페이지 자체도 확정 규칙과 어긋나는 점이 있다.

| 항목 | Notion `register` 페이지 | 확정 (feature-spec / README) |
|---|---|---|
| 로그인 식별자 | `login_id` (영문+숫자 4~20자) | `email` |
| 닉네임 필드 | `nickname` | `username` |
| 비밀번호 정책 | 8자 이상 **14자 미만** | 8자 이상 **128자 이하**, 대/소문자+숫자+특수문자 |
| 비밀번호 확인 | 없음 | `password_confirm` 필수 |
| 응답 | `user.id`(uuid), `login_id`, `nickname`, `created_at` | `email`, `username` (**`user_id` 미포함**) |
| 오류 형식 | `{ error: { code, message } }` | `{ success: false, error_code, message }` |
| 중복 409 메시지 | "이미 사용중인 아이디" | `email` 중복 |
| 인증 | (비고에서 SID 방식 확정이라고 명시, 본문은 JWT 잔재) | `SID` 쿠키 |

→ **구현할 때는 이 페이지의 본문을 따르지 말고 [feature-spec.md의 회원가입](feature-spec.md#회원가입)을 따른다.**

## 엔드포인트 목록 (Notion 표 기준)

| 메서드 | 경로 | Notion의 기능명 | 타겟 | feature-spec의 대응 / 상태 |
|---|---|---|---|---|
| POST | `/api/auth/register` | 회원가입 | 공통 | [회원가입](feature-spec.md#회원가입) |
| POST | `/api/auth/login` | 로그인 | 공통 | [로그인](feature-spec.md#로그인) |
| POST | `/api/auth/logout` | 로그아웃 | 공통 | [로그아웃](feature-spec.md#로그아웃) |
| PATCH | `/api/auth/profile/modify` | 유저 정보 수정 | 공통 | [유저 정보 수정](feature-spec.md#유저-정보-수정) |
| GET | `/api/auth/profile` | 유저 정보 출력 | 공통 | [유저 정보 출력](feature-spec.md#유저-정보-출력) |
| PATCH | `/api/host/ctf-settings` | CTF 설정 | 호스트 | [CTF 설정 기능](feature-spec.md#ctf-설정-기능) |
| POST | `/api/host/workspaces/users` | 팀/유저 등록 | 호스트 | [팀/유저 등록](feature-spec.md#팀유저-등록) |
| PATCH | `/api/host/workspaces/assign` | 워크스페이스 지정 | 호스트 | ⚠️ 별도 호출이 아님 ("팀/유저 등록"과 같은 요청). 이 경로는 불필요 |
| POST | `/api/host/logs/aggregate` | 호스트 로그 집계 | 호스트 | ⚠️ 내부 처리, API 없음 |
| GET | `/api/host/logs` | 호스트 로그 출력 | 호스트 | [로그 출력](feature-spec.md#로그-출력) |
| POST | `/api/participant/data/encrypt` | 암호화 저장 | 참가자 | ⚠️ 내부 공통 로직, API 없음 |
| GET | `/api/participant/contest/status` | 종료 후 수정불가 | 참가자 | ⚠️ 쓰기 요청의 공통 검사(`423`), 별도 API 불필요 |
| POST | `/api/participant/files/create` | 파일 생성 | 참가자 | [파일 생성](feature-spec.md#파일-생성) |
| DELETE | `/api/participant/files/delete` | 파일 삭제 | 참가자 | [파일 삭제](feature-spec.md#파일-삭제) |
| PATCH | `/api/participant/files/modify` | 파일 수정 | 참가자 | [파일 수정](feature-spec.md#파일-수정) |
| POST | `/api/participant/problems/add` | 풀 문제 추가 | 참가자 | [풀 문제 추가](feature-spec.md#풀-문제-추가) |
| POST | `/api/participant/logs/aggregate` | 참가자 로그 집계 | 참가자 | ⚠️ 내부 처리, API 없음 |
| GET | `/api/participant/logs` | 참가자 로그 출력 | 참가자 | [로그 출력](feature-spec.md#로그-출력) (호스트 로그 출력과 같은 기능, 조회 범위만 다름) |
| POST | `/api/participant/timeline/aggregate` | 타임라인 집계 | 참가자 | ⚠️ 잠금 직후 서버가 자동 실행, API 없음 |
| GET | `/api/participant/timeline` | 타임라인 출력 | 참가자 | [타임라인 출력](feature-spec.md#타임라인-출력) |
| POST | `/api/participant/branches/create` | 브랜치 생성 | 참가자 | [브랜치 생성](feature-spec.md#브랜치-생성) |
| DELETE | `/api/participant/branches/delete` | 브랜치 삭제 | 참가자 | [브랜치 삭제](feature-spec.md#브랜치-삭제) |
| POST | `/api/participant/branches/merge` | 브랜치 병합 | 참가자 | [브랜치 병합](feature-spec.md#브랜치-병합) |
| POST | `/api/participant/commits/request` | 변경 사항 커밋 요청 | 참가자 | ⚠️ 이전 모델 잔재. 현재는 [파일 수정](feature-spec.md#파일-수정)(`200`/`202`) |
| POST | `/api/participant/commits/consent` | 커밋 동의 여부 전송 | 참가자 | ⚠️ 이전 모델 잔재. 현재는 [WebSocket 메시지](feature-spec.md#수정-동의-여부-전송) |
| POST | `/api/participant/commits/apply` | 커밋 적용 | 참가자 | ⚠️ 이전 모델 잔재. 동의 완료 시 서버가 자동 반영, 팀장은 `force` 메시지로 강제 적용 |
| POST | `/api/participant/workspaces/password` | 워크스페이스 비번 설정 | 참가자 | [워크스페이스 비밀번호 설정](feature-spec.md#워크스페이스-비밀번호-설정) |

경로 이름 규칙(`/api/<타겟>/...`, 동사형 경로)은 이 표의 관습이다. 스펙 문서에는 REST 설계 원칙이 정해져 있지 않으니 컨트롤러를 만들 때 팀과 정한다.

## 기능 명세에는 있는데 이 표에 없는 API

[feature-spec.md](feature-spec.md)에서 HTTP로 노출되어야 하지만 Notion 표에 행이 없는 기능이다. 경로는 아직 정해지지 않았다.

| 기능 | 메서드 성격 | 비고 |
|---|---|---|
| 비밀번호 찾기 | 요청 | `200`, 계정 존재 여부와 무관하게 동일 응답, `429` |
| 비밀번호 재설정 | 요청 | `reset_token` 사용, `410` 만료 |
| 대시보드 목록 조회 | 조회 | 제안 항목 |
| CTF 생성 | 생성 | 제안 항목. 계정당 3개 제한, `403` |
| 문제 등록 | 생성 | 파일 업로드(multipart) 포함 |
| 문제 관리 (조회/수정/삭제) | 조회·수정·삭제 | 시작 후 수정·삭제는 `409` |
| 모니터링 집계 | 조회 | 호스트, 폴링 |
| CTF 총 요약 | 조회 | 호스트, 종료 후 |
| 워크스페이스 참가 | 요청 | 비밀번호 입력, 마스터 키 캐싱 |
| 파일 내용 조회 | 조회 | 복호화 |
| 문제 목록 조회 | 조회 | |
| 파일 트리 조회 | 조회 | 제안 항목. 키 없이 소속 검증만 |
| 파일 이동 | 수정 | 즉시 반영 |
| 파일 복사 | 생성 | 즉시 반영 |
| 파일 메모 | 수정 | 즉시 반영, 조회는 파일 내용 조회에 포함 |
| 풀이 완료 표시 | 수정 | 즉시 반영 |
| 브랜치 목록 조회 | 조회 | 제안 항목 |
| 실시간 연결 / 열람 상태 전송 / 수정 동의 여부 전송 / 병합 응답 | WebSocket | HTTP 엔드포인트가 아니라 소켓 메시지. 메시지 형식은 [실시간 연결](feature-spec.md#실시간-연결-웹소켓) |

## 공통 규칙 요약

전문은 `README.md` "API 응답 규칙"이다. 구현 시 놓치기 쉬운 것만 적는다.

- 성공 `{ success: true, message, data }`, 오류 `{ success: false, error_code, message }`. 검증 실패만 `errors: [{field, message}]`를 추가한다.
- JSON은 snake_case, 코드는 camelCase. 날짜는 오프셋 포함 ISO 8601.
- 이 프로젝트에서 자주 쓰는 상태 코드: `200/201/202`, `400`, `401`(세션·마스터 키 없음), `403`/`404`(소속 아님은 존재 노출 방지를 위해 `404`), `409`(충돌·비밀번호 미설정), `410`(만료·폐기), `423`(잠김), `429`.
- `204 No Content`는 쓰지 않는다. 본문이 없어서 "`data`는 항상 포함" 규칙과 모순이므로 페이로드가 없는 성공은 `200`/`201` + `data: null`이다 → [feature-spec.md 결정 기록](feature-spec.md#결정-기록).
- 오류 코드는 한 곳(`global/exception/ErrorCode`)에서 관리한다 (`AUTH_FAILED`, `TEAM_NOT_FOUND` 형식).
