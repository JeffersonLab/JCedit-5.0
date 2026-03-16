#!/usr/bin/env python3
"""
Find unused classes in the codebase
"""

import re
from pathlib import Path
from collections import defaultdict

def find_unused_classes():
    base_path = Path("/Users/gurjyan/Documents/Devel/JCedit-5.0/src/main/java")

    # Map of class name to file path
    classes = {}
    # Map of class name to references count
    references = defaultdict(int)

    # First pass: collect all class names
    for java_file in base_path.rglob('*.java'):
        with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()

        # Find public class/interface declarations
        class_matches = re.finditer(r'(?:public|protected)?\s+(?:class|interface|enum)\s+(\w+)', content)
        for match in class_matches:
            class_name = match.group(1)
            classes[class_name] = str(java_file)

    print(f"Found {len(classes)} classes/interfaces/enums")

    # Second pass: count references
    for java_file in base_path.rglob('*.java'):
        with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()

        for class_name in classes.keys():
            # Count occurrences (excluding the definition itself)
            pattern = r'\b' + re.escape(class_name) + r'\b'
            matches = len(re.findall(pattern, content))
            references[class_name] += matches

    # Find potentially unused classes (referenced only in their own file or test files)
    unused_classes = []
    for class_name, file_path in classes.items():
        # Skip test classes
        if 'test' in file_path.lower():
            continue

        # If referenced <= 2 times (class declaration + maybe one import), might be unused
        # We need to be more sophisticated - check if referenced outside its own file
        ref_count = references[class_name]

        # Read the class file to count references in it
        with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
            own_content = f.read()
        own_refs = len(re.findall(r'\b' + re.escape(class_name) + r'\b', own_content))

        external_refs = ref_count - own_refs

        if external_refs == 0:
            unused_classes.append({
                'class': class_name,
                'file': file_path,
                'total_refs': ref_count,
                'own_refs': own_refs,
                'external_refs': external_refs
            })

    print(f"\nPotentially unused classes ({len(unused_classes)}):")
    print("="*80)
    for cls in sorted(unused_classes, key=lambda x: x['class']):
        print(f"\nClass: {cls['class']}")
        print(f"File: {cls['file']}")
        print(f"Total references: {cls['total_refs']}")
        print(f"Own file references: {cls['own_refs']}")
        print(f"External references: {cls['external_refs']}")
        print("-"*80)

    return unused_classes

if __name__ == "__main__":
    find_unused_classes()
