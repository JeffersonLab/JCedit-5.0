#!/usr/bin/env python3
"""
Dead Code Analyzer for Java files
Analyzes unused imports, private methods, private fields, and unreachable code
"""

import os
import re
from collections import defaultdict
from pathlib import Path

class DeadCodeAnalyzer:
    def __init__(self, base_path):
        self.base_path = Path(base_path)
        self.results = {
            'unused_imports': [],
            'unused_private_methods': [],
            'unused_private_fields': [],
            'dead_code_blocks': [],
            'unreachable_code': []
        }

    def is_protected_section(self, lines, line_num):
        """Check if line is in a protected section (DO NOT MODIFY, etc.)"""
        # Look backward for protection markers
        for i in range(max(0, line_num - 20), line_num):
            if i < len(lines):
                line = lines[i].lower()
                if any(marker in line for marker in [
                    'do not modify', 'do not edit', 'auto-generated',
                    'do not touch', 'generated code', 'gen-begin'
                ]):
                    # Check if there's a matching end marker
                    for j in range(line_num, min(len(lines), line_num + 50)):
                        if 'gen-end' in lines[j].lower() or 'end auto' in lines[j].lower():
                            return True
                    return True
        return False

    def analyze_imports(self, file_path):
        """Analyze unused imports in a Java file"""
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()
                content = ''.join(lines)

            # Skip if in protected section
            if self.is_protected_section(lines, 0):
                return

            # Find all imports
            import_pattern = r'^import\s+(static\s+)?([a-zA-Z0-9_.]+(?:\.\*)?);'
            imports = []

            for i, line in enumerate(lines, 1):
                match = re.match(import_pattern, line.strip())
                if match:
                    full_import = match.group(2)
                    imports.append((i, full_import, line.strip()))

            # Check each import for usage
            for line_num, import_path, import_line in imports:
                if self.is_protected_section(lines, line_num - 1):
                    continue

                # Skip wildcard imports for now (harder to analyze)
                if import_path.endswith('.*'):
                    continue

                # Get the class name
                class_name = import_path.split('.')[-1]

                # Check if class is used in the file (excluding the import line itself)
                # Look for the class name as a whole word
                pattern = r'\b' + re.escape(class_name) + r'\b'

                # Remove import lines from content
                content_without_imports = '\n'.join([
                    line for j, line in enumerate(lines)
                    if not line.strip().startswith('import ')
                ])

                if not re.search(pattern, content_without_imports):
                    self.results['unused_imports'].append({
                        'file': str(file_path),
                        'line': line_num,
                        'import': import_line,
                        'class': class_name,
                        'confidence': 'medium'
                    })
        except Exception as e:
            print(f"Error analyzing imports in {file_path}: {e}")

    def analyze_private_methods(self, file_path):
        """Analyze unused private methods"""
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()
                content = ''.join(lines)

            # Find private methods
            method_pattern = r'^\s*private\s+(?:static\s+)?(?:final\s+)?(?:<[^>]+>\s+)?(\w+)\s+(\w+)\s*\('

            private_methods = []
            for i, line in enumerate(lines, 1):
                if self.is_protected_section(lines, i - 1):
                    continue

                match = re.search(method_pattern, line)
                if match:
                    return_type = match.group(1)
                    method_name = match.group(2)
                    # Skip constructors
                    if return_type == method_name:
                        continue
                    private_methods.append((i, method_name, line.strip()))

            # Check usage of each private method
            for line_num, method_name, method_line in private_methods:
                # Count occurrences (should be at least 2: definition + call)
                pattern = r'\b' + re.escape(method_name) + r'\b'
                matches = list(re.finditer(pattern, content))

                # If only 1 match (the definition), it's unused
                if len(matches) <= 1:
                    self.results['unused_private_methods'].append({
                        'file': str(file_path),
                        'line': line_num,
                        'method': method_name,
                        'declaration': method_line,
                        'confidence': 'high'
                    })
        except Exception as e:
            print(f"Error analyzing private methods in {file_path}: {e}")

    def analyze_private_fields(self, file_path):
        """Analyze unused private fields"""
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()
                content = ''.join(lines)

            # Find private fields
            field_pattern = r'^\s*private\s+(?:static\s+)?(?:final\s+)?(?:transient\s+)?(?:volatile\s+)?([a-zA-Z0-9_<>\[\],\s]+)\s+(\w+)\s*[=;]'

            private_fields = []
            for i, line in enumerate(lines, 1):
                if self.is_protected_section(lines, i - 1):
                    continue

                match = re.search(field_pattern, line)
                if match:
                    field_type = match.group(1).strip()
                    field_name = match.group(2)
                    private_fields.append((i, field_name, line.strip()))

            # Check usage of each private field
            for line_num, field_name, field_line in private_fields:
                # Count occurrences
                pattern = r'\b' + re.escape(field_name) + r'\b'
                matches = list(re.finditer(pattern, content))

                # If only 1 match (the declaration), it's unused
                if len(matches) <= 1:
                    self.results['unused_private_fields'].append({
                        'file': str(file_path),
                        'line': line_num,
                        'field': field_name,
                        'declaration': field_line,
                        'confidence': 'high'
                    })
        except Exception as e:
            print(f"Error analyzing private fields in {file_path}: {e}")

    def analyze_unreachable_code(self, file_path):
        """Analyze unreachable code blocks"""
        try:
            with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
                lines = f.readlines()

            in_method = False
            brace_count = 0

            for i, line in enumerate(lines, 1):
                if self.is_protected_section(lines, i - 1):
                    continue

                stripped = line.strip()

                # Track braces to know if we're in a method
                brace_count += stripped.count('{') - stripped.count('}')

                # Check for code after return statement in same block
                if re.search(r'\breturn\b.*;', stripped):
                    # Check next non-empty, non-comment line
                    for j in range(i, min(i + 5, len(lines))):
                        next_line = lines[j].strip()
                        if next_line and not next_line.startswith('//') and not next_line.startswith('/*'):
                            # If it's not a closing brace or case/default, it might be unreachable
                            if not re.match(r'^[})]', next_line) and not re.match(r'^(case|default)', next_line):
                                if not next_line.startswith('*') and not next_line.startswith('//'):
                                    # Check if it's actual code
                                    if re.search(r'[a-zA-Z0-9_]+\s*[=\(]', next_line):
                                        self.results['unreachable_code'].append({
                                            'file': str(file_path),
                                            'line': j + 1,
                                            'code': next_line[:80],
                                            'reason': 'Code after return statement',
                                            'confidence': 'medium'
                                        })
                            break

                # Check for if (false) or similar
                if re.search(r'if\s*\(\s*false\s*\)', stripped):
                    self.results['dead_code_blocks'].append({
                        'file': str(file_path),
                        'line': i,
                        'code': stripped[:80],
                        'reason': 'Condition always false',
                        'confidence': 'high'
                    })

        except Exception as e:
            print(f"Error analyzing unreachable code in {file_path}: {e}")

    def analyze_file(self, file_path):
        """Analyze a single Java file"""
        print(f"Analyzing: {file_path}")
        self.analyze_imports(file_path)
        self.analyze_private_methods(file_path)
        self.analyze_private_fields(file_path)
        self.analyze_unreachable_code(file_path)

    def analyze_directory(self):
        """Analyze all Java files in directory"""
        java_files = list(self.base_path.rglob('*.java'))
        print(f"Found {len(java_files)} Java files")

        for java_file in java_files:
            self.analyze_file(java_file)

    def generate_report(self, output_file):
        """Generate detailed report"""
        with open(output_file, 'w') as f:
            f.write("=" * 80 + "\n")
            f.write("DEAD CODE ANALYSIS REPORT\n")
            f.write("=" * 80 + "\n\n")

            # Unused Imports
            f.write(f"\n{'=' * 80}\n")
            f.write(f"UNUSED IMPORTS ({len(self.results['unused_imports'])} found)\n")
            f.write(f"{'=' * 80}\n\n")

            for item in sorted(self.results['unused_imports'], key=lambda x: x['file']):
                f.write(f"File: {item['file']}\n")
                f.write(f"Line: {item['line']}\n")
                f.write(f"Import: {item['import']}\n")
                f.write(f"Class: {item['class']}\n")
                f.write(f"Confidence: {item['confidence']}\n")
                f.write("-" * 80 + "\n\n")

            # Unused Private Methods
            f.write(f"\n{'=' * 80}\n")
            f.write(f"UNUSED PRIVATE METHODS ({len(self.results['unused_private_methods'])} found)\n")
            f.write(f"{'=' * 80}\n\n")

            for item in sorted(self.results['unused_private_methods'], key=lambda x: x['file']):
                f.write(f"File: {item['file']}\n")
                f.write(f"Line: {item['line']}\n")
                f.write(f"Method: {item['method']}\n")
                f.write(f"Declaration: {item['declaration']}\n")
                f.write(f"Confidence: {item['confidence']}\n")
                f.write("-" * 80 + "\n\n")

            # Unused Private Fields
            f.write(f"\n{'=' * 80}\n")
            f.write(f"UNUSED PRIVATE FIELDS ({len(self.results['unused_private_fields'])} found)\n")
            f.write(f"{'=' * 80}\n\n")

            for item in sorted(self.results['unused_private_fields'], key=lambda x: x['file']):
                f.write(f"File: {item['file']}\n")
                f.write(f"Line: {item['line']}\n")
                f.write(f"Field: {item['field']}\n")
                f.write(f"Declaration: {item['declaration']}\n")
                f.write(f"Confidence: {item['confidence']}\n")
                f.write("-" * 80 + "\n\n")

            # Dead Code Blocks
            f.write(f"\n{'=' * 80}\n")
            f.write(f"DEAD CODE BLOCKS ({len(self.results['dead_code_blocks'])} found)\n")
            f.write(f"{'=' * 80}\n\n")

            for item in sorted(self.results['dead_code_blocks'], key=lambda x: x['file']):
                f.write(f"File: {item['file']}\n")
                f.write(f"Line: {item['line']}\n")
                f.write(f"Code: {item['code']}\n")
                f.write(f"Reason: {item['reason']}\n")
                f.write(f"Confidence: {item['confidence']}\n")
                f.write("-" * 80 + "\n\n")

            # Unreachable Code
            f.write(f"\n{'=' * 80}\n")
            f.write(f"UNREACHABLE CODE ({len(self.results['unreachable_code'])} found)\n")
            f.write(f"{'=' * 80}\n\n")

            for item in sorted(self.results['unreachable_code'], key=lambda x: x['file']):
                f.write(f"File: {item['file']}\n")
                f.write(f"Line: {item['line']}\n")
                f.write(f"Code: {item['code']}\n")
                f.write(f"Reason: {item['reason']}\n")
                f.write(f"Confidence: {item['confidence']}\n")
                f.write("-" * 80 + "\n\n")

            # Summary
            f.write(f"\n{'=' * 80}\n")
            f.write("SUMMARY\n")
            f.write(f"{'=' * 80}\n\n")
            f.write(f"Total unused imports: {len(self.results['unused_imports'])}\n")
            f.write(f"Total unused private methods: {len(self.results['unused_private_methods'])}\n")
            f.write(f"Total unused private fields: {len(self.results['unused_private_fields'])}\n")
            f.write(f"Total dead code blocks: {len(self.results['dead_code_blocks'])}\n")
            f.write(f"Total unreachable code: {len(self.results['unreachable_code'])}\n")

            total = (len(self.results['unused_imports']) +
                    len(self.results['unused_private_methods']) +
                    len(self.results['unused_private_fields']) +
                    len(self.results['dead_code_blocks']) +
                    len(self.results['unreachable_code']))
            f.write(f"\nTotal issues found: {total}\n")

if __name__ == "__main__":
    base_path = "/Users/gurjyan/Documents/Devel/JCedit-5.0/src/main/java"
    output_file = "/Users/gurjyan/Documents/Devel/JCedit-5.0/dead_code_report.txt"

    analyzer = DeadCodeAnalyzer(base_path)
    analyzer.analyze_directory()
    analyzer.generate_report(output_file)

    print(f"\nReport generated: {output_file}")
    print(f"Total issues found: {len(analyzer.results['unused_imports']) + len(analyzer.results['unused_private_methods']) + len(analyzer.results['unused_private_fields']) + len(analyzer.results['dead_code_blocks']) + len(analyzer.results['unreachable_code'])}")
