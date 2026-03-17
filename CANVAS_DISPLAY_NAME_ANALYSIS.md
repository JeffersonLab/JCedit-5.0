# Canvas Display Name Issue - Root Cause Analysis

## Issue Description
Canvas displays old legacy labels like "USER", "FILE" instead of current ACodaType names like "USR", "FILE" when loading saved configurations.

## Root Cause

The issue occurs when **loading saved RDF configuration files** that contain components with old type names. The system does NOT validate or migrate legacy type names during deserialization.

### Data Flow Analysis

#### 1. New Component Creation (Menu → Canvas) ✓ WORKS CORRECTLY
```
User clicks menu item (e.g., "Application")
  ↓
CDesktopNew.ScmiMouseClicked() creates JLabel
  - JLabel text = "Application" (display text, NOT used)
  - label.setName("USR") (correct type)
  ↓
CCanvas.addComponent(label)
  - Gets type from label.getName() → "USR" ✓
  - Creates component name: "USR" + id → "USR1" ✓
  - JCGComponent.setName("USR1") ✓
  - JCGComponent.setType("USR") ✓
  ↓
DrawingCanvas.drawGComponent(gc.getName(), ...)
  - Displays "USR1" on canvas ✓ CORRECT
```

#### 2. Loading Saved Configuration (RDF → Canvas) ✗ PROBLEMATIC
```
User opens saved .rdf file
  ↓
JCParser.parseComponent() reads RDF data
  - Line 346-349: cmp.setType(getValue(x, "hasType"))
  - Line 338-344: cmp.setName(getValue(x, "hasName"))
  - NO VALIDATION against ACodaType enum ✗
  - NO MIGRATION of old type names ✗
  ↓
If RDF contains old data:
  - hasType = "FPGA" (removed type, should be "VTP")
  - hasName = "USER1" (old naming, should be "USR1")
  ↓
JCGComponent loaded with OLD VALUES
  - type = "FPGA" ✗
  - name = "USER1" ✗
  ↓
DrawingCanvas.drawGComponent(gc.getName(), ...)
  - Displays "USER1" on canvas ✗ WRONG!
```

## File Analysis

### JCParser.java (Lines 320-453)
**Location**: `/src/main/java/org/jlab/coda/cedit/parsers/coolparser/JCParser.java`

**Problematic Code**:
```java
private Map<String, JCGComponent> parseComponent(Object subject, String predicate) {
    // ...
    tmps = getValue(x, "hasName");
    if (tmps != null) {
        cmp.setName(tmps);  // ← Sets name directly from RDF, no validation
    }

    tmps = getValue(x, "hasType");
    if (tmps != null) {
        cmp.setType(tmps);  // ← Sets type directly from RDF, no validation
    }
    // ...
}
```

**Issues**:
1. No validation that `type` is a valid ACodaType enum value
2. No migration of legacy types (FPGA → VTP, USER → USR, etc.)
3. No validation that `name` follows current naming convention
4. Old RDF files with removed types (FPGA, FCS, SMS, RCS) will load invalid components

### DrawingCanvas.java (Line 805)
**Location**: `/src/main/java/org/jlab/coda/cedit/cooldesktop/DrawingCanvas.java`

```java
public void drawGComponent(String compName, double x, double y, double w, double h) {
    g2D.drawString(compName, (int) x + 2, (int) (y + h - 2));  // ← Displays component name
}
```

This correctly displays whatever name is stored in the JCGComponent. The issue is that old RDF files have wrong names stored.

## Legacy Type Mapping

Old saved configurations may contain these legacy type names:

| Old Type | Current Type | Status |
|----------|--------------|--------|
| FPGA | VTP | Removed - must migrate |
| FCS | (removed) | Removed - must reject |
| SMS | (removed) | Removed - must reject |
| RCS | (removed) | Removed - must reject |
| SHEL | SHELL | Renamed - must migrate |

Additionally, old configurations may have components with names using old conventions that don't match current ACodaType values.

## Solution

### Required Changes

#### 1. Add Type Validation in JCParser.parseComponent()
```java
// After line 348
tmps = getValue(x, "hasType");
if (tmps != null) {
    // Migrate legacy type names
    tmps = migrateTypeName(tmps);

    // Validate against ACodaType enum
    if (ACodaType.getEnum(tmps) == null) {
        System.out.println("COOL-WARNING: Invalid type '" + tmps + "' for component " + x + ". Skipping component.");
        continue; // Skip invalid components
    }

    cmp.setType(tmps);
}
```

#### 2. Implement Type Migration Method
```java
/**
 * Migrates legacy type names to current ACodaType values
 * @param oldType The type name from the RDF file
 * @return The migrated type name, or original if no migration needed
 */
private String migrateTypeName(String oldType) {
    switch (oldType.toUpperCase()) {
        case "FPGA":
            System.out.println("COOL-INFO: Migrating type FPGA → VTP");
            return "VTP";
        case "SHEL":
            System.out.println("COOL-INFO: Migrating type SHEL → SHELL");
            return "SHELL";
        case "FCS":
        case "SMS":
        case "RCS":
            System.out.println("COOL-WARNING: Type " + oldType + " has been removed. Component will be skipped.");
            return null;
        default:
            return oldType;
    }
}
```

#### 3. Update Component Name After Type Migration
```java
// After setting type and ID, regenerate name if needed
if (cmp.getType() != null && cmp.getId() > 0) {
    // Extract base type from old name (e.g., "FPGA1" → "FPGA")
    String expectedName = cmp.getType() + cmp.getId();

    // If name doesn't match current convention, regenerate it
    if (!cmp.getName().equals(expectedName)) {
        System.out.println("COOL-INFO: Migrating component name from '" +
                          cmp.getName() + "' to '" + expectedName + "'");
        cmp.setName(expectedName);
    }
}
```

### Alternative: Pre-Migration Script
Create a migration script that updates RDF files before loading:
- Scan all .rdf files in config directories
- Find and replace old type names with new ones
- Update component names to match new conventions
- Create backup before modification

## Testing Plan

1. **Create Test RDF File**: Manually create a test RDF with old type names:
   - Component with type="FPGA" and name="FPGA1"
   - Component with type="SHEL" and name="SHEL1"
   - Component with old name like "USER1" (if this was ever used)

2. **Load Without Fix**: Verify canvas displays old names

3. **Apply Fix**: Implement type migration in JCParser

4. **Load With Fix**: Verify:
   - FPGA components migrated to VTP
   - SHEL components migrated to SHELL
   - Component names regenerated correctly
   - Canvas displays correct current names

5. **Icon Loading**: Verify icons load correctly for migrated types

## Impact Assessment

### Files to Modify
1. `src/main/java/org/jlab/coda/cedit/parsers/coolparser/JCParser.java` - Add migration logic
2. Potentially need to update RDF writing code to ensure new types are saved

### Risk Level
**MEDIUM** - Changes affect configuration loading, but migration logic is straightforward

### Backward Compatibility
- Old RDF files will be automatically migrated on load
- No changes to RDF file format itself
- Users should be notified that old types are being migrated
- Consider adding a "save migrated config" option to update files permanently

## Recommendations

1. **Immediate**: Implement type validation and migration in JCParser.parseComponent()
2. **Short-term**: Add comprehensive logging for all migrations
3. **Long-term**: Consider adding a "Configuration Migration Tool" to batch-update old RDF files
4. **Documentation**: Create migration guide for users with old configurations

## Status
**Analysis Complete** - Ready for implementation
