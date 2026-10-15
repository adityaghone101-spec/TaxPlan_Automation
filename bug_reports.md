# TaxPlan Advisor QA — Test Execution Bug Report

**Application:** https://staging.taxplanadvisor.in/  
**Environment:** Staging  
**Suite:** Taxplan Advisor QA Assessment Suite (`testng.xml`)  
**Executed:** 15 Oct 2026, 19:22–19:29 IST  
**Browser:** Chrome 154 (headless), Windows 10  
**Results:** 20 tests run — **6 passed**, **14 failed**, **0 skipped** (405.5s)  
**Reports:** `test-output/ExtentReport.html` · `target/surefire-reports/` · screenshots in `test-output/screenshots/`

## Summary

| Module | Passed | Failed | Notes |
|---|---:|---:|---|
| Login (`LoginTest` TC001–TC010) | 1 | 9 | Page loads; Sign in and related actions blocked or links missing |
| Home (`HomePageTest` TC011–TC020) | 5 | 5 | Page/title/promo/footer/cards OK; overlays block CTAs and content |

Login authentication, invalid-credential messaging, and empty-field validation **could not be proven** in this run because the Sign in button was covered by the cookie banner. Failures below are grouped as product defects, not as 14 unique root causes.

---

## Bug 1 — Cookie consent banner covers primary CTAs (Critical)

**Title:** Cookie banner overlays Sign in, File ITR, Refer Now, and Consultation  
**Severity:** Critical  
**Area:** Global overlay / login + home  
**Failed tests:** TC002, TC003, TC004, TC005, TC006, TC007, TC010, TC013, TC016, TC019

**Description:** The cookie consent bar (`#cookie-consent-description`, Accept / Reject / Settings) sits over the lower portion of the viewport. Primary actions under it cannot be clicked. On login, credentials can be typed, but Sign in never receives the click. On home, File ITR is intercepted by the cookie description paragraph.

**Steps to Reproduce:**
1. Open a fresh session (no prior cookie consent).
2. Go to `https://staging.taxplanadvisor.in/login`.
3. Enter email and password.
4. Click Sign in (or File ITR on the home page without accepting cookies).

**Expected:** Cookie banner does not cover primary CTAs; user can sign in or use hero buttons without dismissing cookies first, or the banner is placed so those controls remain clickable.

**Actual:** Click is intercepted. Selenium: `element click intercepted` — Sign in at (203, 397) hit by overlay; File ITR at (198, 413) hit by `#cookie-consent-description`.

**Evidence:** `TC002_clientLoginWithValidCredentials_20261015_192254.png`, `TC013_verifyFileITRButton_20261015_192552.png`

---

## Bug 2 — Full-screen marketing modal blocks home page content (High)

**Title:** IdeaBaaz “Best Fintech Startup 2026” modal covers the home page  
**Severity:** High  
**Area:** Home / marketing  
**Failed tests:** TC012 (also contributes to missed service labels)

**Description:** A full-viewport promotional modal (TaxPlan Advisor × IdeaBaaz, 26–30 Aug 2026) appears on the home page and hides marketplace content. The test looking for “ITR Filing” timed out after 20s because the page behind the modal was not usable. Live copy (when the modal is not in the way) uses “ITR & GST Filing”, “ITR Salary Filing”, “Business Registrations”, not the older labels “ITR Filing” / “GST Filing” / “TDS Filing” / “Notice Handling”.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/` in a new session.
2. Observe whether the IdeaBaaz winner modal appears before the marketplace.

**Expected:** Promotional content must not block core navigation. If a modal is shown, it should be dismissible immediately and must not hide required service categories.

**Actual:** Modal occupies the full viewport. Service category “ITR Filing” is not visible to the user/automation.

**Evidence:** `TC012_verifyServiceCategoriesDisplayed_20261015_192545.png`

---

## Bug 3 — “View Filing Services” CTA is missing (High)

**Title:** Home page has no “View Filing Services” link  
**Severity:** High  
**Area:** Home navigation  
**Failed tests:** TC014

**Description:** After 20 seconds the locator `//a[contains(text(),'View Filing Services')]` finds no element. Current home IA uses “What do you need help with?” with ITR & GST Filing / Business Registrations / Consultation — the previous “View Filing Services” CTA is gone or renamed.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/`.
2. Dismiss overlays if present.
3. Search the page for “View Filing Services”.

**Expected:** A working link/button to filing services (or an equivalent, clearly labeled CTA).

**Actual:** No such link. Timeout / `NoSuchElementException`.

**Evidence:** `TC014_verifyViewFilingServicesLink_20261015_192626.png`, surefire stacktrace.

---

## Bug 4 — Consultant Access entry point missing on login (High)

**Title:** Login page has no “Consultant Access” link  
**Severity:** High  
**Area:** Login / consultant  
**Failed tests:** TC009 (TC003 blocked before consultant login could be verified)

**Description:** Login UI is client-oriented (Google + email/password). There is no “Consultant Access” control. Automation waited 20s for `//a[contains(text(),'Consultant Access')]`. Consultant login (`testconsultant@gmail.com`) was never reached because Sign in is also overlay-blocked.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/login`.
2. Look for Consultant Access / Sign in as Consultant.

**Expected:** Consultants can reach a consultant login path from the client login page.

**Actual:** Link not present. Timeout waiting for clickable element.

**Evidence:** Login screenshots (TC002/TC008) show only Sign in, Google, Forgot password?, cookie bar, Enquire now, Nirmala AI.

---

## Bug 5 — Forgot password control is not a usable link under overlays (Medium)

**Title:** “Forgot password?” is not reachable as an `<a>` (blocked / wrong control type)  
**Severity:** Medium  
**Area:** Login  
**Failed tests:** TC008

**Description:** The UI shows “Forgot password?” next to the password label, but the test locator `//a[contains(text(),'Forgot password')]` never becomes clickable (20s timeout). The cookie bar and Nirmala chat (“How can I help you!”) sit over the lower form. The control may be a button/span rather than an anchor.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/login`.
2. Do not accept cookies.
3. Click Forgot password?.

**Expected:** Navigation to a reset-password flow; control remains clickable.

**Actual:** Automation cannot click it. User may also hit the cookie/chat overlay.

**Evidence:** `TC008_verifyForgotPasswordLink_20261015_192402.png`

---

## Bug 6 — Chat / Enquire widgets compete with cookie bar and CTAs (Medium)

**Title:** Nirmala AI bubble and “Enquire now” overlap cookie banner and page actions  
**Severity:** Medium  
**Area:** Global widgets  
**Failed tests:** TC016, TC019 (`element not interactable`); login intercept also hit `<p class="text-sm font-semibold leading-tight text-slate-900">` (chat title)

**Description:** Fixed widgets (green Enquire now tab, WhatsApp, Nirmala avatar + “How can I help you!”) stack on the same bottom-right area as the cookie bar. Consultation and Refer Now were not interactable. This is a z-index / layout issue on first visit.

**Steps to Reproduce:**
1. Open home or login in a fresh session.
2. Attempt to click Refer Now, Consultation, or Sign in without dismissing cookies or chat.

**Expected:** Widgets must not steal clicks from primary CTAs.

**Actual:** `ElementClickInterceptedException` / `element not interactable`.

**Evidence:** Login and home screenshots; TC016/TC019 logs.

---

## Passed tests (not defects)

| ID | Result |
|---|---|
| TC001 Login page elements | Pass — Sign in header and `/login` URL |
| TC011 Home page loads | Pass — title present |
| TC015 Consultant cards | Pass — cards found |
| TC017 Links present | Pass — page has anchors (does not prove each link works) |
| TC018 Promo TAXPLAN10 | Pass |
| TC020 Footer disclaimer | Pass — “not affiliated” text present |

---

## Not verified this run

These assertions never executed because Sign in could not be clicked:

- Valid client login (`testclient@gmail.com`)
- Valid consultant login (`testconsultant@gmail.com`)
- Invalid password / invalid email error messages
- Empty email / empty password validation
- SQL injection blocked at submit

Re-test after cookie banner no longer covers Sign in.

---

## Framework note (test harness, not the product)

First suite run skipped all 10 login tests: `config` was instance-scoped and set only in `@BeforeSuite` on another TestNG instance (`NullPointerException` in `BaseTest.initDriver`). `ConfigReader` is now static so login tests execute. Suite parallel mode was set to sequential to avoid a shared static `WebDriver`.
