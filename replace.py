import os
import xml.etree.ElementTree as ET

def replace_group_id_in_pom(file_path, old_group_id, new_group_id):
    try:
        # Parse the XML file
        tree = ET.parse(file_path)
        root = tree.getroot()

        # Define the namespaces (if any)
        namespaces = {'maven': 'http://maven.apache.org/POM/4.0.0'}

        # Find all groupId elements
        for group_id in root.findall('.//maven:groupId', namespaces):
            if group_id.text == old_group_id:
                group_id.text = new_group_id
                print(f"Replaced in: {file_path}")

        # Write back to the file
        tree.write(file_path, encoding='utf-8', xml_declaration=True)
    except ET.ParseError as e:
        print(f"Error parsing {file_path}: {e}")

def replace_in_pom_files(base_directory, old_group_id, new_group_id):
    # Walk through the directory
    for root, dirs, files in os.walk(base_directory):
        for file in files:
            if file == 'pom.xml':
                file_path = os.path.join(root, file)
                replace_group_id_in_pom(file_path, old_group_id, new_group_id)

# Define the base directory, old groupId, and new groupId
base_directory = '.'  # Current directory or specify your path
old_group_id = 'com.ptsl.beacon.commonlib'
new_group_id = 'your.new.groupId'

# Execute the replacement function
replace_in_pom_files(base_directory, old_group_id, new_group_id)
