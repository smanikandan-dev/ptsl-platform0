import os

def delete_classpath_and_project_files(directory):
    # List all items in the specified directory
    for item in os.listdir(directory):
        item_path = os.path.join(directory, item)

        # Check if the item is a directory
        if os.path.isdir(item_path):
            # Check for .classpath and .project files in the subdirectory
            classpath_file = os.path.join(item_path, '.classpath')
            project_file = os.path.join(item_path, '.project')

            if os.path.isfile(classpath_file):
                os.remove(classpath_file)
                print(f"Deleted .classpath: {classpath_file}")

            if os.path.isfile(project_file):
                os.remove(project_file)
                print(f"Deleted .project: {project_file}")

# Specify the directory to start the search from
base_directory = '.'  # Current directory, change as needed

delete_classpath_and_project_files(base_directory)
