# Security

## Reporting

Open a [GitHub issue](https://github.com/SatyamChouksey-88/api-automation-restassured/issues) if you find a vulnerability or accidental secret in the repository.

## Secrets

- ReqRes API keys belong in GitHub Actions secret `REQRES_API_KEY` or local env — never in committed files.
- Copy `.env.example` to `.env` locally (gitignored) if you need a key for live runs.

## Test data

All scenarios use the public ReqRes demo API or local WireMock stubs with synthetic JSON. No employer or client data is used.
