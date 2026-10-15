# TaxPlan Advisor — QA Bug Reports
**Application:** https://staging.taxplanadvisor.in/
**Environment:** Staging
**Browser:** Chrome 154.0.8037.98 (headless), Windows 10
**Test Suite:** Taxplan Advisor QA Assessment Suite
**Executed:** 15 Oct 2026, 21:20–21:33 IST
**Total Tests:** 32 across 3 modules
**Results:** 13 Passed · 19 Failed · 0 Skipped

---

## Overall Test Summary

| Module | Tests | Passed | Failed | Notes |
|---|:-:|:-:|:-:|---|
| Login (`LoginTest` TC001–TC010) | 10 | 6 | 4 | Page elements OK; consultant login fails, no error on invalid email, forgot password / consultant access links missing |
| Home Page (`HomePageTest` TC011–TC020) | 10 | 5 | 5 | Title/cards/promo/links/footer OK; service labels wrong, File ITR / View Filing Services / Consultation blocked by overlays |
| Client & Consultant Dashboard (`ClientDashboardExplorationTest`) | 12 | 2 | 10 | Client login OK, service pages load; dashboard nav, profile, logout, orders all missing; consultant login fails entirely |

---

## BUG-001 — Consultant Login Always Fails (Critical)

**Title:** Consultant account cannot log in — always stays on login page
**Severity:** Critical
**Priority:** P1
**Area:** Authentication / Consultant Portal
**Affected Tests:** TC003, TC_D01, TC_D02, TC_D03, TC_D04

**Description:**
When the test account `testconsultant@gmail.com` submits credentials on the standard login page, the application does not authenticate and keeps the user on `/login`. The `loginAsConsultant()` helper attempts to click a "Sign in as Consultant" button first — but that button is absent (see BUG-004), causing it to fall back to a direct email/password login, which itself fails for the consultant account. All downstream consultant dashboard tests consequently also fail.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/login`
2. Enter email `testconsultant@gmail.com` and the correct password
3. Click **Sign in**
4. Wait 3–4 seconds

**Expected Result:**
Consultant is authenticated and redirected to the consultant dashboard (URL should not contain `/login`).

**Actual Result:**
Browser remains on `https://staging.taxplanadvisor.in/login`. No redirect occurs.

**Error from Logs:**
```
BUG: Consultant login failed - still on login page.
URL: https://staging.taxplanadvisor.in/login expected [false] but found [true]
```

**Evidence:**
- `TC003_consultantLoginWithValidCredentials_20261015_212037.png`
- `TC_D01_consultantDashboard_20261015_213134.png`

---

## BUG-002 — No Error Message Shown for Non-Existent Email (High)

**Title:** Login with an invalid / unregistered email shows no error feedback
**Severity:** High
**Priority:** P2
**Area:** Authentication / Validation
**Affected Tests:** TC005

**Description:**
When a user submits login with a non-existent email address (`nonexistentuser@invalid.com`), the application does not display any error message. After 20 seconds no error element is found in the DOM. The user receives zero feedback — they do not know whether their email is wrong, their account doesn't exist, or the server failed.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/login`
2. Enter `nonexistentuser@invalid.com` in the email field
3. Enter any password
4. Click **Sign in**
5. Wait up to 20 seconds

**Expected Result:**
An error message such as "No account found with this email" or "Invalid credentials" is displayed on the page.

**Actual Result:**
No error message appears. Page stays on login with no visible feedback.

**Error from Logs:**
```
BUG: No error message displayed for invalid/non-existent email.
expected [true] but found [false]
```

**Evidence:**
- `TC005_loginWithInvalidEmail_20261015_212118.png`

---

## BUG-003 — "Forgot Password?" is Not a Clickable Anchor Link (Medium)

**Title:** "Forgot password?" control is not reachable as an `<a>` element — times out
**Severity:** Medium
**Priority:** P2
**Area:** Authentication / Login UI
**Affected Tests:** TC008

**Description:**
The login page visually shows a "Forgot password?" link next to the password field, but the element is not implemented as an `<a>` tag or is otherwise not clickable (possibly a `<span>` or `<button>` styled as a link). The test waits 20 seconds for `//a[contains(text(),'Forgot password')]` and receives a `NoSuchElementException`.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/login`
2. Inspect the "Forgot password?" element in browser DevTools
3. Note it is not an `<a>` element **OR** attempt to locate it via `//a[contains(text(),'Forgot password')]`

**Expected Result:**
Clicking "Forgot password?" navigates to a password-reset page (URL no longer contains `/login`).

**Actual Result:**
```
TimeoutException: Expected condition failed: waiting for element to be clickable:
Proxy element for: DefaultElementLocator 'By.xpath: //a[contains(text(),'Forgot password')]'
(tried for 20 second(s) with 500 milliseconds interval)
Caused by: NoSuchElementException: no such element
```

**Evidence:**
- `TC008_verifyForgotPasswordLink_20261015_212215.png`

---

## BUG-004 — "Consultant Access" Entry Point Missing from Login Page (High)

**Title:** Login page has no "Consultant Access" link or dedicated consultant sign-in path
**Severity:** High
**Priority:** P1
**Area:** Authentication / Consultant Portal
**Affected Tests:** TC009

**Description:**
There is no visible "Consultant Access" anchor, button, or separate login URL for consultants on the login page. The test locates `//a[contains(text(),'Consultant Access')]` and waits 20 seconds with no result. Consultants have no self-service way to reach a distinct login flow.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/login`
2. Look for any "Consultant Access", "Sign in as Consultant", or similar control
3. Attempt to locate `//a[contains(text(),'Consultant Access')]` in the DOM

**Expected Result:**
A clearly labelled link/button exists that routes consultants to a separate authentication flow or consultant-specific login page (URL should contain `consultant`).

**Actual Result:**
No such element exists. Test times out after 20 seconds:
```
TimeoutException: Caused by: NoSuchElementException: no such element:
Unable to locate element: {method: xpath, selector: //a[contains(text(),'Consultant Access')]}
```

**Evidence:**
- `TC009_verifyConsultantAccessLink_20261015_212250.png`

---

## BUG-005 — Home Page Service Category Labels Are Outdated (High)

**Title:** Service categories "ITR Filing", "GST Filing", "TDS Filing", "Notice Handling" not found on home page
**Severity:** High
**Priority:** P2
**Area:** Home Page / Service Catalogue
**Affected Tests:** TC012

**Description:**
The home page no longer displays the service category labels that tests expect: `ITR Filing`, `GST Filing`, `TDS Filing`, `Notice Handling`. The current UI uses merged/renamed categories (e.g. "ITR & GST Filing", "Business Registrations", "Consultation"). A full-screen promotional modal (IdeaBaaz "Best Fintech Startup 2026") also appears on load, blocking background content from the automation layer and contributing to the failure.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/` in a fresh browser session
2. Observe the IdeaBaaz promotional modal that covers the page
3. Search page source for "ITR Filing", "GST Filing", "TDS Filing", "Notice Handling"

**Expected Result:**
All required service categories are clearly displayed and accessible (either under old labels or updated labels that match test assertions).

**Actual Result:**
Labels not found. Test fails on the first missing category: `ITR Filing`
```
BUG: Service category 'ITR Filing' is not displayed on home page
expected [true] but found [false]
```

**Evidence:**
- `TC012_verifyServiceCategoriesDisplayed_20261015_212331.png`

---

## BUG-006 — "File ITR" Button Not Clickable on Home Page (High)

**Title:** "File ITR" button on home page cannot be clicked — 20-second timeout
**Severity:** High
**Priority:** P1
**Area:** Home Page / Primary CTA
**Affected Tests:** TC013

**Description:**
The "File ITR" button (`//button[contains(text(),'File ITR')]`) is not found or not clickable within 20 seconds. This button is a primary call-to-action on the home page. This failure is likely caused either by the promotional modal covering the button, the cookie consent banner overlapping it, or the button text/element type being different in the current build.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/`
2. Without dismissing any overlays, attempt to click the "File ITR" button
3. Observe whether the button is visible and clickable

**Expected Result:**
Clicking "File ITR" navigates the user to a new page (URL changes).

**Actual Result:**
```
TimeoutException: Expected condition failed: waiting for element to be clickable:
[[ChromeDriver] -> xpath: //button[contains(text(),'File ITR')]]
(tried for 20 second(s) with 500 milliseconds interval)
```

**Evidence:**
- `TC013_verifyFileITRButton_20261015_212406.png`

---

## BUG-007 — "View Filing Services" Link Does Not Exist on Home Page (High)

**Title:** Home page has no "View Filing Services" link
**Severity:** High
**Priority:** P2
**Area:** Home Page / Navigation
**Affected Tests:** TC014

**Description:**
The expected link `//a[contains(text(),'View Filing Services')]` does not exist anywhere in the current home page DOM. The previous IA included a "View Filing Services" CTA, but it appears to have been removed or renamed without a replacement being clearly labelled.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/`
2. Search all anchor tags on the page for text containing "View Filing Services"
3. Attempt to click a link with that label

**Expected Result:**
A working "View Filing Services" (or equivalently-named) link exists and navigates the user to the services listing.

**Actual Result:**
```
TimeoutException: Caused by: NoSuchElementException:
Unable to locate element: {method: xpath, selector: //a[contains(text(),'View Filing Services')]}
```

**Evidence:**
- `TC014_verifyViewFilingServicesLink_20261015_212453.png`

---

## BUG-008 — "Refer Now" Link Not Interactable (Medium)

**Title:** "Refer Now" link exists in DOM but throws `ElementNotInteractableException`
**Severity:** Medium
**Priority:** P2
**Area:** Home Page / Referral CTA
**Affected Tests:** TC016

**Description:**
The "Refer Now" anchor tag is found in the DOM (`//a[contains(text(),'Refer Now')]`) but is not interactable when clicked. The element is likely visually hidden (e.g., inside a collapsed menu, behind a widget, or has `display:none` / `pointer-events:none`). The green "Enquire now" tab, WhatsApp icon, and Nirmala AI chat widget in the bottom-right area may be stacking over this element.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/`
2. Do **not** dismiss the cookie consent banner
3. Find the "Refer Now" link on the page
4. Attempt to click it

**Expected Result:**
Clicking "Refer Now" navigates to a referral page (URL should contain `refer`).

**Actual Result:**
```
ElementNotInteractableException: element not interactable
Element: [[ChromeDriver] -> xpath: //a[contains(text(),'Refer Now')]]
```

**Evidence:**
- `TC016_verifyReferNowLink_20261015_212530.png`

---

## BUG-009 — "Consultation" Button Not Found or Not Clickable on Home Page (High)

**Title:** Home page "Consultation" button cannot be clicked — 20-second timeout
**Severity:** High
**Priority:** P1
**Area:** Home Page / Consultation CTA
**Affected Tests:** TC019

**Description:**
The test looks for `//button[contains(text(),'Consultation')]` and waits 20 seconds. The element is either absent, covered by overlays (cookie banner / Nirmala AI widget / Enquire Now tab), or has been renamed. This is a primary conversion CTA.

**Steps to Reproduce:**
1. Open `https://staging.taxplanadvisor.in/`
2. Without dismissing any overlays, attempt to click a "Consultation" button
3. Observe whether the button exists and is clickable

**Expected Result:**
A "Consultation" or "Book Consultation" button is visible and clickable, navigating the user to a booking page (URL changes).

**Actual Result:**
```
TimeoutException: Expected condition failed: waiting for element to be clickable:
[[ChromeDriver] -> xpath: //button[contains(text(),'Consultation')]]
(tried for 20 second(s) with 500 milliseconds interval)
```

**Evidence:**
- `TC019_verifyConsultationButton_20261015_212616.png`

---

## BUG-010 — Client Dashboard Has No `<nav>` Element (High)

**Title:** Client dashboard renders without a `<nav>` HTML element — no navigation bar detected
**Severity:** High
**Priority:** P2
**Area:** Client Dashboard / Navigation
**Affected Tests:** TC_C02

**Description:**
After successful client login, the dashboard page source does not contain a `<nav>` element. This means either the navigation bar is built without semantic HTML (e.g., a `<div>` masquerading as a nav), or the navigation component fails to mount post-login. Without a proper nav, accessibility and keyboard navigation are also impacted.

**Steps to Reproduce:**
1. Log in with valid client credentials (`testclient@gmail.com`)
2. Wait for the dashboard to load (3 seconds)
3. Dismiss any promotional popup
4. Inspect page source: search for `<nav>` element

**Expected Result:**
A `<nav>` element is present containing navigation links (e.g., Dashboard, My Services, Profile, Logout).

**Actual Result:**
```
BUG: Navigation bar not found on dashboard expected [true] but found [false]
AssertionError at ClientDashboardExplorationTest.java:127
```

**Evidence:**
- `TC_C02_dashboardUI_20261015_212739.png`
- `TC_C02_clientDashboardUIElements_20261015_212739.png`

---

## BUG-011 — No Profile / Account / Settings Link on Client Dashboard (High)

**Title:** Client dashboard has no accessible Profile, Account, or Settings navigation link
**Severity:** High
**Priority:** P2
**Area:** Client Dashboard / Profile Management
**Affected Tests:** TC_C03

**Description:**
After client login, the test searches for any element with text containing "Profile", "Account", or "Settings", or with `href` containing `profile`, `account`, or `settings`. No such element is found. Clients have no visible way to access or update their personal information.

**Steps to Reproduce:**
1. Log in with valid client credentials
2. On the post-login dashboard, look for any "Profile", "Account", or "Settings" link
3. Attempt to click one and observe the resulting URL

**Expected Result:**
A Profile or Account settings page is reachable from the dashboard, allowing the user to view/edit their name, email, phone, etc.

**Actual Result:**
```
BUG: No Profile/Account/Settings link accessible from client dashboard
expected [true] but found [false]
```

**Evidence:**
- `TC_C03_clientProfile_20261015_212818.png`
- `TC_C03_clientProfilePage_20261015_212819.png`

---

## BUG-012 — No "My Services" / "My Orders" Section on Client Dashboard (High)

**Title:** Client dashboard does not expose a "My Services", "My Orders", or "My Filings" section
**Severity:** High
**Priority:** P1
**Area:** Client Dashboard / Service History
**Affected Tests:** TC_C04

**Description:**
After client login, no element is found with text or `href` related to "My Services", "My Orders", "My Filings", "Orders", or "Filings". This is a critical feature — clients should be able to view the status of their booked tax services. Its absence means users cannot track any engagement with the platform post-purchase.

**Steps to Reproduce:**
1. Log in with valid client credentials
2. On the post-login dashboard, look for any section labelled "My Services", "My Orders", "Orders", "My Filings", or similar
3. Attempt to navigate to it

**Expected Result:**
A clearly labelled section or link allows the client to view their purchased/booked services and their current status.

**Actual Result:**
```
BUG: My Services/My Orders section not found in client dashboard
expected [true] but found [false]
```

**Evidence:**
- `TC_C04_myServices_20261015_212859.png`
- `TC_C04_clientMyServices_20261015_212900.png`

---

## BUG-013 — Client Cannot Log Out — Logout Button Not Found (Critical)

**Title:** No Logout / Sign Out button found on client dashboard
**Severity:** Critical
**Priority:** P1
**Area:** Client Dashboard / Session Management
**Affected Tests:** TC_C07

**Description:**
After client login, the test searches for avatar/user-menu elements and then for any element with text "Logout", "Log out", "Sign out", or "Signout". None are found after a 20-second wait. Clients have no way to end their session. This is both a functional and security issue — users sharing devices cannot log out.

**Steps to Reproduce:**
1. Log in with valid client credentials
2. On the post-login dashboard, look for a user avatar, profile menu trigger, or direct "Logout" button
3. Attempt to click it and observe the resulting URL

**Expected Result:**
A Logout control is accessible from the dashboard. Clicking it terminates the session and redirects to the login page or home page.

**Actual Result:**
```
BUG: Could not find or click Logout button on client dashboard
expected [true] but found [false]
```

**Evidence:**
- `TC_C07_clientLogout_20261015_213116.png`
- `TC_C07_clientLogout_20261015_213117.png`

---

## BUG-014 — Consultant Dashboard Has No Logout Option (Critical)

**Title:** No Logout / Sign Out option found on consultant dashboard (when logged in via fallback path)
**Severity:** Critical
**Priority:** P1
**Area:** Consultant Dashboard / Session Management
**Affected Tests:** TC_D02

**Description:**
Even when the consultant login partially proceeds (via the standard email/password path), the resulting dashboard page source contains neither "Logout", "Sign out", nor "Log out" text. This parallels BUG-013 for the client, but applies to the consultant role. Consultants cannot terminate their session.

**Steps to Reproduce:**
1. Log in as consultant (via email/password fallback since the dedicated path is missing — see BUG-004)
2. On the post-login page, search for any Logout or Sign Out control

**Expected Result:**
A Logout option is present and functional on the consultant dashboard.

**Actual Result:**
```
BUG: No logout option found on consultant dashboard
expected [true] but found [false]
```

**Evidence:**
- `TC_D02_consultantDashboardUI_20261015_213206.png`

---

## BUG-015 — Consultant Profile / Settings Not Accessible from Dashboard (High)

**Title:** No Profile or Settings link found on consultant dashboard
**Severity:** High
**Priority:** P2
**Area:** Consultant Dashboard / Profile
**Affected Tests:** TC_D03

**Description:**
After attempting consultant login, no element with text/href referencing "Profile", "Settings", or "Account" is found in the dashboard. Consultants cannot update their professional information, rates, or availability.

**Steps to Reproduce:**
1. Log in as consultant
2. On the post-login dashboard, search for any "Profile", "Settings", or "Account" navigation item
3. Attempt to click it

**Expected Result:**
A Profile or Settings page is accessible from the consultant dashboard where the consultant can view and edit their professional details.

**Actual Result:**
```
BUG: Consultant Profile/Settings page not accessible from dashboard
expected [true] but found [false]
```

**Evidence:**
- `TC_D03_consultantProfile_20261015_213246.png`
- `TC_D03_consultantProfile_20261015_213247.png`

---

## BUG-016 — Consultant Dashboard Has No Orders / Requests Section (Critical)

**Title:** Consultant dashboard has no Orders, Requests, or Assignments section
**Severity:** Critical
**Priority:** P1
**Area:** Consultant Dashboard / Order Management
**Affected Tests:** TC_D04

**Description:**
After consultant login, no element with text or href related to "Order", "Request", "Assignment", "Task", or their href equivalents is found. Consultants cannot view or manage the client requests assigned to them. This is core workflow functionality — without it, consultants cannot perform their primary job on the platform.

**Steps to Reproduce:**
1. Log in as consultant
2. On the consultant dashboard, search for any "Orders", "Requests", "Assignments", or "Tasks" section
3. Attempt to navigate to it

**Expected Result:**
A clearly labelled section exists for assigned client orders/requests, listing items with status, client info, and action buttons.

**Actual Result:**
```
BUG: Consultant dashboard has no Orders/Requests section
expected [true] but found [false]
```

**Evidence:**
- `TC_D04_consultantOrders_20261015_213326.png`
- `TC_D04_consultantOrders_20261015_213327.png`

---

## Supporting Evidence Summary

| Screenshot | Related Bug |
|---|---|
| `TC003_consultantLoginWithValidCredentials_20261015_212037.png` | BUG-001 |
| `TC005_loginWithInvalidEmail_20261015_212118.png` | BUG-002 |
| `TC008_verifyForgotPasswordLink_20261015_212215.png` | BUG-003 |
| `TC009_verifyConsultantAccessLink_20261015_212250.png` | BUG-004 |
| `TC012_verifyServiceCategoriesDisplayed_20261015_212331.png` | BUG-005 |
| `TC013_verifyFileITRButton_20261015_212406.png` | BUG-006 |
| `TC014_verifyViewFilingServicesLink_20261015_212453.png` | BUG-007 |
| `TC016_verifyReferNowLink_20261015_212530.png` | BUG-008 |
| `TC019_verifyConsultationButton_20261015_212616.png` | BUG-009 |
| `TC_C02_dashboardUI_20261015_212739.png` | BUG-010 |
| `TC_C03_clientProfile_20261015_212818.png` | BUG-011 |
| `TC_C04_myServices_20261015_212859.png` | BUG-012 |
| `TC_C07_clientLogout_20261015_213116.png` | BUG-013 |
| `TC_D02_consultantDashboardUI_20261015_213206.png` | BUG-014 |
| `TC_D03_consultantProfile_20261015_213246.png` | BUG-015 |
| `TC_D04_consultantOrders_20261015_213326.png` | BUG-016 |

All screenshots are located in: `test-output/screenshots/`

---

## Tests That Passed (No Defects)

| ID | Description |
|---|---|
| TC001 | Login page loads with correct elements and `/login` URL |
| TC002 | Client login with valid credentials succeeds |
| TC004 | Invalid password — error message displayed correctly |
| TC006 | Empty email — login blocked |
| TC007 | Empty password — login blocked |
| TC010 | SQL injection attempt correctly blocked |
| TC011 | Home page loads with non-empty title |
| TC015 | Consultant cards displayed on home page |
| TC017 | Home page contains anchor links |
| TC018 | Promo code "TAXPLAN10" banner is displayed |
| TC020 | Footer disclaimer ("not affiliated") text present |
| TC_C01 | Client login and redirect to dashboard succeeds |
| TC_C05 | ITR Filing service page loads with booking CTA |
| TC_C06 | Consultation page loads correctly when accessed directly |
