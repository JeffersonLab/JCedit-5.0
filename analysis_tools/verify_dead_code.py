#!/usr/bin/env python3
"""
Manual verification script for dead code findings
"""

import re
from pathlib import Path

# Findings to verify manually
VERIFIED_FINDINGS = {
    'unused_imports': [],
    'unused_private_methods': [],
    'unused_private_fields': [],
    'true_unreachable_code': [],
    'false_positives': []
}

def verify_unreachable_code():
    """Verify unreachable code by checking if it's truly after an unconditional return"""

    # Check CCanvas.java line 134
    file_path = Path("/Users/gurjyan/Documents/Devel/JCedit-5.0/src/main/java/org/jlab/coda/cedit/cooldesktop/CCanvas.java")
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()

    # Line 132: if(...) return;
    # Line 134: gCmpImageFile = ...
    # This IS unreachable because line 132 is in an if statement, not unconditional
    # Let's check the context
    context = lines[130:145]
    print("CCanvas.java lines 131-145:")
    for i, line in enumerate(context, 131):
        print(f"{i}: {line}", end='')
    print("\n" + "="*80)

    # Check CDesktopNew.java line 1483
    file_path = Path("/Users/gurjyan/Documents/Devel/JCedit-5.0/src/main/java/org/jlab/coda/cedit/cooldesktop/CDesktopNew.java")
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()

    context = lines[1475:1495]
    print("\nCDesktopNew.java lines 1476-1495:")
    for i, line in enumerate(context, 1476):
        print(f"{i}: {line}", end='')
    print("\n" + "="*80)

def verify_equals_methods():
    """Check if the equals() method findings are false positives"""
    file_path = Path("/Users/gurjyan/Documents/Devel/JCedit-5.0/src/main/java/org/jlab/coda/cedit/system/JCGComponent.java")
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()

    # Find the equals method
    for i, line in enumerate(lines):
        if 'public boolean equals' in line:
            print("\nJCGComponent.java equals() method:")
            for j in range(i, min(i+30, len(lines))):
                print(f"{j+1}: {lines[j]}", end='')
            break
    print("\n" + "="*80)

def check_comment_sections():
    """Check for DO NOT MODIFY sections"""
    base_path = Path("/Users/gurjyan/Documents/Devel/JCedit-5.0/src/main/java")

    protected_sections = []

    for java_file in base_path.rglob('*.java'):
        with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()

        if re.search(r'DO NOT MODIFY|DO NOT EDIT|GEN-BEGIN|AUTO-GENERATED', content, re.IGNORECASE):
            protected_sections.append(str(java_file))

    print(f"\nFiles with protected sections ({len(protected_sections)}):")
    for file in protected_sections:
        print(f"  - {file}")
    print("="*80)

if __name__ == "__main__":
    print("="*80)
    print("DEAD CODE VERIFICATION")
    print("="*80)

    verify_unreachable_code()
    verify_equals_methods()
    check_comment_sections()
