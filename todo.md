# ACodaType and Icon Resources Consistency Refactoring Plan

## Overview
Fix one-to-one correspondence between ACodaType enum values and icon resources, ensuring all type-based logic relies exclusively on valid ACodaType values.

## Critical Issues to Fix

### 1. Add Missing ACodaType Enum Values
**Problem:** Code references `ACodaType.USR` and `ACodaType.EBER` but they don't exist in the enum.

**Action:**
- Add `USR` to ACodaType enum with appropriate priority value
- Add `EBER` to ACodaType enum with appropriate priority value

**Files to modify:**
- `src/main/java/org/jlab/coda/cedit/system/ACodaType.java`

**Priority Values to determine:**
- USR: Used for EJFAT components (Packetizer, Load Balancer, Reassembly) and general user applications
- EBER: Event Builder variant

### 2. Fix Icon Name Mismatch
**Problem:** ACodaType has `SHEL` but icon is named `SHELL.png`

**Decision needed:**
- Option A: Rename `SHEL` to `SHELL` in ACodaType enum
- Option B: Rename `SHELL.png` to `SHEL.png`

**Recommended:** Option A (rename enum to SHELL) - more intuitive name

**Files to modify if Option A:**
- `src/main/java/org/jlab/coda/cedit/system/ACodaType.java`

**Files to modify if Option B:**
- `src/main/resources/resources/SHELL.png` → rename to `SHEL.png`

### 3. Create Missing Icons
**Problem:** 7 ACodaType values have no corresponding icons

**Action:**
For each missing icon, either:
- Create the icon file, OR
- Remove the ACodaType value if it's unused, OR
- Map it to an existing icon

**Missing icons:**
1. **SLC** (Shell Process) - used in code (menuItem35MouseClicked)
2. **WNC** (Docker Container) - used in code (menuItem36MouseClicked)
3. **FCS** - check if used, may be legacy
4. **EB** (Event Builder) - used in linking logic
5. **FPGA** - used in code but UI uses VTP icon for FPGA type components
6. **SMS** - check if used, may be legacy
7. **RCS** - check if used, may be legacy

**Decision needed:** Which approach for each type?

### 4. Handle Extra EBER Icon
**Problem:** EBER.png exists and EBER is used in code

**Action:** Keep EBER.png since we'll add EBER to ACodaType enum

### 5. Remove or Update Unused Types
**Problem:** Some ACodaType values may not be actively used

**Action:** Search for each type without an icon to determine if it's used

**Types to investigate:**
- FCS
- EB (used in linking logic, but no direct component creation)
- SMS
- RCS

## Detailed Refactoring Steps

### Phase 1: Fix Critical Compilation Errors
1. Add USR to ACodaType enum
2. Add EBER to ACodaType enum
3. Verify code compiles

### Phase 2: Fix Icon Name Mismatch
4. Rename SHEL to SHELL in ACodaType enum
5. Update any direct references to SHEL

### Phase 3: Create/Map Missing Icons
6. Create or identify icons for: SLC, WNC, FCS, EB, FPGA, SMS, RCS
7. Add icon files to src/main/resources/resources/

### Phase 4: Verification
8. Verify one-to-one mapping between all ACodaType values and icons
9. Verify no code references types outside ACodaType
10. Test icon loading for all component types
11. Run build and tests

## Files That Will Be Modified

### Core Files:
- `src/main/java/org/jlab/coda/cedit/system/ACodaType.java`

### Icon Resources:
- Rename: `src/main/resources/resources/SHELL.png` → varies by decision
- Create: 7 new icon files or map to existing ones

### No Code Changes Needed:
All code already references ACodaType.USR and ACodaType.EBER - these will work once added to enum.

## Questions for User

1. **Priority values:** What priority values should be assigned to USR and EBER?
   - Current pattern: multiples of 10 or 100
   - USR is used for user applications
   - EBER is an Event Builder variant

2. **SHEL vs SHELL:** Rename enum value SHEL to SHELL? (Recommended: Yes)

3. **Missing icons:** For SLC, WNC, FCS, EB, FPGA, SMS, RCS:
   - Should we create new icon files?
   - Should we use existing icons as aliases?
   - Should we remove unused types from ACodaType?

4. **Icon creation:** If creating new icons, what style/format should they follow?
   - Current icons are PNG format
   - Size and style should match existing icons

## Expected Outcome

After refactoring:
- All ACodaType enum values have exactly one corresponding icon resource
- All icon resources correspond to exactly one ACodaType value
- No code references types outside of ACodaType
- Clean one-to-one mapping guaranteed
- All compilation errors resolved
- All runtime icon loading errors resolved
