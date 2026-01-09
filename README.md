# Task-Tracker
Task tracker is a project used to track and manage your tasks. In this task, you will build a simple command line interface (CLI) to track what you need to do, what you have done, and what you are currently working on. This project will help you practice your programming skills, including working with the filesystem, handling user inputs, and building a simple CLI application.

## Requirements
The application should run from the command line, accept user actions and inputs as arguments, and store the tasks in a JSON file. The user should be able to:

- Add, Update, and Delete tasks
- Mark a task as in progress or done
- List all tasks
- List all tasks that are done
- List all tasks that are not done
- List all tasks that are in progress

## Constraints
- You can use any programming language to build this project
- Use positional arguments in command line to accept user inputs
- Use a JSON file to store the tasks in the current directory
- The JSON file should be created if it does not exist
- Use the native file system module of your programming language to interact with the JSON file
- **Do not use any external libraries or frameworks** to build this project
- Ensure to handle errors and edge cases gracefully

## Implementation (Java)

### Files Structure
```
Task-Tracker/
├── Task.java          # Task model class
├── TaskManager.java   # Core logic for task operations
├── TaskTracker.java   # Main CLI interface
├── tasks.json         # Data storage (auto-generated)
└── .gitignore         # Exclude compiled files
```

### Setup & Usage

1. **Compile the project:**
   ```bash
   javac *.java
   ```

2. **Run commands:**
   ```bash
   # Add a new task
   java TaskTracker add "Buy groceries"
   java TaskTracker add "Complete project documentation"
   
   # List tasks
   java TaskTracker list                    # All tasks
   java TaskTracker list todo               # Pending tasks
   java TaskTracker list in-progress        # In-progress tasks
   java TaskTracker list done               # Completed tasks
   
   # Update task status
   java TaskTracker mark-in-progress 1      # Mark task 1 as in-progress
   java TaskTracker mark-done 1             # Mark task 1 as done
   
   # Update task description
   java TaskTracker update 1 "Buy groceries and cook dinner"
   
   # Delete a task
   java TaskTracker delete 1
   ```

3. **Example workflow:**
   ```bash
   # Start fresh
   javac *.java
   
   # Add some tasks
   java TaskTracker add "Learn Java"
   java TaskTracker add "Build CLI app"
   java TaskTracker add "Write documentation"
   
   # Check all tasks
   java TaskTracker list
   
   # Start working on task 1
   java TaskTracker mark-in-progress 1
   
   # Complete task 1
   java TaskTracker mark-done 1
   
   # See what's left to do
   java TaskTracker list todo
   ```

### Available Commands
- `add <description>` - Add a new task
- `update <id> <description>` - Update a task
- `delete <id>` - Delete a task
- `mark-in-progress <id>` - Mark task as in-progress
- `mark-done <id>` - Mark task as done
- `list` - List all tasks
- `list done` - List completed tasks
- `list todo` - List pending tasks
- `list in-progress` - List in-progress tasks

### Key Implementation Notes
- **No external libraries**: Uses native Java string manipulation for JSON parsing instead of org.json
- **File handling**: Uses `java.nio.file.Files` for reading/writing JSON
- **Error handling**: Graceful handling of missing files, invalid IDs, and malformed data
- **Data persistence**: Tasks stored in `tasks.json` with auto-creation if missing
