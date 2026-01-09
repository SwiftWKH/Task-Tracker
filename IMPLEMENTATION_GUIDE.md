# Task Tracker CLI - Complete Implementation Guide

This guide will walk you through building a Task Tracker CLI application in both **Java** and **C#**. You'll learn how to work with command-line arguments, JSON files, and build a complete CLI application.

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Java Implementation](#java-implementation)
3. [C# Implementation](#c-implementation)
4. [Testing Your Application](#testing-your-application)
5. [Common Challenges & Solutions](#common-challenges--solutions)

---

## Project Overview

### What You'll Build
A command-line application that manages tasks with these features:
- Add, update, and delete tasks
- Mark tasks as "todo", "in-progress", or "done"
- List all tasks or filter by status
- Store tasks in a JSON file

### Core Concepts You'll Learn
- Command-line argument parsing
- File I/O operations
- JSON serialization/deserialization
- Data structures (Lists, Objects)
- Error handling
- Date/time handling

---

# Java Implementation

## Prerequisites
- Java Development Kit (JDK) 8 or higher
- Text editor or IDE (VS Code, IntelliJ IDEA, Eclipse)
- Basic understanding of Java syntax

## Project Structure
```
Task-Tracker/
├── TaskTracker.java      (Main class with CLI logic)
├── Task.java             (Task model class)
├── TaskManager.java      (Business logic for task operations)
└── tasks.json            (Generated automatically)
```

---

## Step 1: Create the Task Model Class

**File: `Task.java`**

```java
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String description;
    private String status; // "todo", "in-progress", "done"
    private String createdAt;
    private String updatedAt;

    // Constructor for creating new tasks
    public Task(int id, String description) {
        this.id = id;
        this.description = description;
        this.status = "todo";
        this.createdAt = getCurrentTimestamp();
        this.updatedAt = getCurrentTimestamp();
    }

    // Constructor for loading existing tasks (used when reading from JSON)
    public Task(int id, String description, String status, String createdAt, String updatedAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Default constructor (required for JSON parsing)
    public Task() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = getCurrentTimestamp();
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = getCurrentTimestamp();
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to get current timestamp
    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    // Method to display task information
    @Override
    public String toString() {
        return String.format("[%d] %s - Status: %s (Created: %s, Updated: %s)",
                id, description, status, createdAt, updatedAt);
    }
}
```

**What This Does:**
- Defines the structure of a task with all necessary fields
- Provides constructors for creating new tasks and loading existing ones
- Includes getters/setters for accessing and modifying task properties
- Auto-updates the `updatedAt` timestamp when description or status changes
- Overrides `toString()` for easy display

---

## Step 2: Create the Task Manager Class

**File: `TaskManager.java`**

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

public class TaskManager {
    private static final String FILE_NAME = "tasks.json";
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
        loadTasks();
    }

    // Load tasks from JSON file
    private void loadTasks() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                // Create empty JSON array if file doesn't exist
                saveTasks();
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
            if (content.trim().isEmpty()) {
                content = "[]";
            }

            JSONArray jsonArray = new JSONArray(content);
            tasks.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonTask = jsonArray.getJSONObject(i);
                Task task = new Task(
                    jsonTask.getInt("id"),
                    jsonTask.getString("description"),
                    jsonTask.getString("status"),
                    jsonTask.getString("createdAt"),
                    jsonTask.getString("updatedAt")
                );
                tasks.add(task);
            }
        } catch (Exception e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            tasks = new ArrayList<>();
        }
    }

    // Save tasks to JSON file
    private void saveTasks() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Task task : tasks) {
                JSONObject jsonTask = new JSONObject();
                jsonTask.put("id", task.getId());
                jsonTask.put("description", task.getDescription());
                jsonTask.put("status", task.getStatus());
                jsonTask.put("createdAt", task.getCreatedAt());
                jsonTask.put("updatedAt", task.getUpdatedAt());
                jsonArray.put(jsonTask);
            }

            Files.write(Paths.get(FILE_NAME), jsonArray.toString(2).getBytes());
        } catch (Exception e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    // Get next available ID
    private int getNextId() {
        if (tasks.isEmpty()) {
            return 1;
        }
        return tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
    }

    // Add a new task
    public void addTask(String description) {
        Task task = new Task(getNextId(), description);
        tasks.add(task);
        saveTasks();
        System.out.println("Task added successfully (ID: " + task.getId() + ")");
    }

    // Update task description
    public void updateTask(int id, String newDescription) {
        Task task = findTaskById(id);
        if (task != null) {
            task.setDescription(newDescription);
            saveTasks();
            System.out.println("Task updated successfully");
        } else {
            System.err.println("Task with ID " + id + " not found");
        }
    }

    // Delete a task
    public void deleteTask(int id) {
        Task task = findTaskById(id);
        if (task != null) {
            tasks.remove(task);
            saveTasks();
            System.out.println("Task deleted successfully");
        } else {
            System.err.println("Task with ID " + id + " not found");
        }
    }

    // Mark task as in-progress
    public void markInProgress(int id) {
        updateTaskStatus(id, "in-progress");
    }

    // Mark task as done
    public void markDone(int id) {
        updateTaskStatus(id, "done");
    }

    // Update task status
    private void updateTaskStatus(int id, String status) {
        Task task = findTaskById(id);
        if (task != null) {
            task.setStatus(status);
            saveTasks();
            System.out.println("Task marked as " + status);
        } else {
            System.err.println("Task with ID " + id + " not found");
        }
    }

    // Find task by ID
    private Task findTaskById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // List all tasks
    public void listAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found");
            return;
        }
        System.out.println("\nAll Tasks:");
        System.out.println("==========");
        tasks.forEach(System.out::println);
    }

    // List tasks by status
    public void listTasksByStatus(String status) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                filteredTasks.add(task);
            }
        }

        if (filteredTasks.isEmpty()) {
            System.out.println("No " + status + " tasks found");
            return;
        }

        System.out.println("\n" + status.toUpperCase() + " Tasks:");
        System.out.println("==========");
        filteredTasks.forEach(System.out::println);
    }
}
```

**What This Does:**
- Manages all task operations (CRUD operations)
- Handles file I/O with JSON serialization/deserialization
- Creates the JSON file if it doesn't exist
- Provides methods for adding, updating, deleting, and listing tasks
- Automatically saves changes to the file

**Important Note:** This code uses `org.json` library. You'll need to download it (see setup instructions below).

---

## Step 3: Create the Main CLI Class

**File: `TaskTracker.java`**

```java
public class TaskTracker {
    private static TaskManager taskManager;

    public static void main(String[] args) {
        taskManager = new TaskManager();

        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];

        try {
            switch (command) {
                case "add":
                    if (args.length < 2) {
                        System.err.println("Error: Please provide a task description");
                        break;
                    }
                    String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    taskManager.addTask(description);
                    break;

                case "update":
                    if (args.length < 3) {
                        System.err.println("Error: Please provide task ID and new description");
                        break;
                    }
                    int updateId = Integer.parseInt(args[1]);
                    String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    taskManager.updateTask(updateId, newDescription);
                    break;

                case "delete":
                    if (args.length < 2) {
                        System.err.println("Error: Please provide task ID");
                        break;
                    }
                    int deleteId = Integer.parseInt(args[1]);
                    taskManager.deleteTask(deleteId);
                    break;

                case "mark-in-progress":
                    if (args.length < 2) {
                        System.err.println("Error: Please provide task ID");
                        break;
                    }
                    int progressId = Integer.parseInt(args[1]);
                    taskManager.markInProgress(progressId);
                    break;

                case "mark-done":
                    if (args.length < 2) {
                        System.err.println("Error: Please provide task ID");
                        break;
                    }
                    int doneId = Integer.parseInt(args[1]);
                    taskManager.markDone(doneId);
                    break;

                case "list":
                    if (args.length == 1) {
                        taskManager.listAllTasks();
                    } else {
                        String status = args[1];
                        taskManager.listTasksByStatus(status);
                    }
                    break;

                default:
                    System.err.println("Error: Unknown command '" + command + "'");
                    printUsage();
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid task ID. Please provide a number.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("Task Tracker CLI");
        System.out.println("===============");
        System.out.println("\nUsage:");
        System.out.println("  java TaskTracker add <description>        - Add a new task");
        System.out.println("  java TaskTracker update <id> <description> - Update a task");
        System.out.println("  java TaskTracker delete <id>              - Delete a task");
        System.out.println("  java TaskTracker mark-in-progress <id>    - Mark task as in-progress");
        System.out.println("  java TaskTracker mark-done <id>           - Mark task as done");
        System.out.println("  java TaskTracker list                     - List all tasks");
        System.out.println("  java TaskTracker list done                - List done tasks");
        System.out.println("  java TaskTracker list todo                - List todo tasks");
        System.out.println("  java TaskTracker list in-progress         - List in-progress tasks");
    }
}
```

**What This Does:**
- Entry point of the application
- Parses command-line arguments
- Routes commands to appropriate TaskManager methods
- Handles errors and provides user feedback
- Displays usage information

---

## Step 4: Setup and Run (Java)

### Option 1: Using JSON.org Library (Recommended)

1. **Download the JSON library:**
   - Go to https://github.com/stleary/JSON-java
   - Download `json-20231013.jar` (or latest version)
   - Or use Maven Central: https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar

2. **Place the JAR file in your project directory:**
   ```
   Task-Tracker/
   ├── json-20231013.jar
   ├── TaskTracker.java
   ├── Task.java
   └── TaskManager.java
   ```

3. **Compile with classpath:**
   ```bash
   javac -cp ".;json-20231013.jar" *.java
   ```
   On Linux/Mac:
   ```bash
   javac -cp ".:json-20231013.jar" *.java
   ```

4. **Run with classpath:**
   ```bash
   java -cp ".;json-20231013.jar" TaskTracker add "Buy groceries"
   ```
   On Linux/Mac:
   ```bash
   java -cp ".:json-20231013.jar" TaskTracker add "Buy groceries"
   ```

### Option 2: Using Manual JSON Parsing (No External Libraries)

If you want to avoid external libraries, replace the TaskManager's load/save methods with manual JSON parsing:

```java
// In TaskManager.java, replace loadTasks() and saveTasks() with:

private void loadTasks() {
    try {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            saveTasks();
            return;
        }

        String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
        if (content.trim().isEmpty() || content.trim().equals("[]")) {
            return;
        }

        // Simple JSON parsing (array of objects)
        content = content.trim();
        content = content.substring(1, content.length() - 1); // Remove [ ]
        
        String[] taskStrings = content.split("\\},\\s*\\{");
        
        for (String taskStr : taskStrings) {
            taskStr = taskStr.replace("{", "").replace("}", "");
            Map<String, String> fields = new HashMap<>();
            
            String[] pairs = taskStr.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    fields.put(key, value);
                }
            }
            
            if (fields.containsKey("id")) {
                Task task = new Task(
                    Integer.parseInt(fields.get("id")),
                    fields.get("description"),
                    fields.get("status"),
                    fields.get("createdAt"),
                    fields.get("updatedAt")
                );
                tasks.add(task);
            }
        }
    } catch (Exception e) {
        System.err.println("Error loading tasks: " + e.getMessage());
        tasks = new ArrayList<>();
    }
}

private void saveTasks() {
    try {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("  {\n");
            json.append("    \"id\": ").append(task.getId()).append(",\n");
            json.append("    \"description\": \"").append(task.getDescription()).append("\",\n");
            json.append("    \"status\": \"").append(task.getStatus()).append("\",\n");
            json.append("    \"createdAt\": \"").append(task.getCreatedAt()).append("\",\n");
            json.append("    \"updatedAt\": \"").append(task.getUpdatedAt()).append("\"\n");
            json.append("  }");
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        
        Files.write(Paths.get(FILE_NAME), json.toString().getBytes());
    } catch (Exception e) {
        System.err.println("Error saving tasks: " + e.getMessage());
    }
}
```

Then compile and run without external dependencies:
```bash
javac *.java
java TaskTracker add "Buy groceries"
```

---

## Example Usage (Java)

```bash
# Add tasks
java TaskTracker add "Buy groceries"
java TaskTracker add "Write blog post"
java TaskTracker add "Study Java"

# List all tasks
java TaskTracker list

# Update a task
java TaskTracker update 1 "Buy groceries and cook dinner"

# Mark task as in-progress
java TaskTracker mark-in-progress 2

# Mark task as done
java TaskTracker mark-done 1

# List tasks by status
java TaskTracker list done
java TaskTracker list todo
java TaskTracker list in-progress

# Delete a task
java TaskTracker delete 3
```

---

# C# Implementation

## Prerequisites
- .NET SDK 6.0 or higher (or .NET Framework 4.7.2+)
- Text editor or IDE (VS Code, Visual Studio, Rider)
- Basic understanding of C# syntax

## Project Structure
```
Task-Tracker/
├── Program.cs           (Main entry point)
├── Task.cs              (Task model class)
├── TaskManager.cs       (Business logic)
├── TaskTracker.csproj   (Project file)
└── tasks.json           (Generated automatically)
```

---

## Step 1: Create the Project

Using .NET CLI:
```bash
dotnet new console -n TaskTracker
cd TaskTracker
```

This creates a basic console application structure.

---

## Step 2: Create the Task Model Class

**File: `Task.cs`**

```csharp
using System;
using System.Text.Json.Serialization;

namespace TaskTracker
{
    public class Task
    {
        [JsonPropertyName("id")]
        public int Id { get; set; }

        [JsonPropertyName("description")]
        public string Description { get; set; }

        [JsonPropertyName("status")]
        public string Status { get; set; } // "todo", "in-progress", "done"

        [JsonPropertyName("createdAt")]
        public string CreatedAt { get; set; }

        [JsonPropertyName("updatedAt")]
        public string UpdatedAt { get; set; }

        // Constructor for creating new tasks
        public Task(int id, string description)
        {
            Id = id;
            Description = description;
            Status = "todo";
            CreatedAt = GetCurrentTimestamp();
            UpdatedAt = GetCurrentTimestamp();
        }

        // Parameterless constructor (required for JSON deserialization)
        public Task()
        {
        }

        // Helper method to get current timestamp
        private string GetCurrentTimestamp()
        {
            return DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss");
        }

        // Method to update description
        public void UpdateDescription(string newDescription)
        {
            Description = newDescription;
            UpdatedAt = GetCurrentTimestamp();
        }

        // Method to update status
        public void UpdateStatus(string newStatus)
        {
            Status = newStatus;
            UpdatedAt = GetCurrentTimestamp();
        }

        // Method to display task information
        public override string ToString()
        {
            return $"[{Id}] {Description} - Status: {Status} (Created: {CreatedAt}, Updated: {UpdatedAt})";
        }
    }
}
```

**What This Does:**
- Defines the task structure with properties instead of fields
- Uses C# properties with automatic getters/setters
- Includes JSON attributes for proper serialization
- Provides methods to update task details with automatic timestamp updates
- Overrides `ToString()` for easy display

---

## Step 3: Create the Task Manager Class

**File: `TaskManager.cs`**

```csharp
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json;

namespace TaskTracker
{
    public class TaskManager
    {
        private const string FileName = "tasks.json";
        private List<Task> tasks;

        public TaskManager()
        {
            tasks = new List<Task>();
            LoadTasks();
        }

        // Load tasks from JSON file
        private void LoadTasks()
        {
            try
            {
                if (!File.Exists(FileName))
                {
                    // Create empty JSON array if file doesn't exist
                    SaveTasks();
                    return;
                }

                string jsonContent = File.ReadAllText(FileName);
                
                if (string.IsNullOrWhiteSpace(jsonContent))
                {
                    jsonContent = "[]";
                }

                var options = new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true,
                    WriteIndented = true
                };

                tasks = JsonSerializer.Deserialize<List<Task>>(jsonContent, options) ?? new List<Task>();
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error loading tasks: {ex.Message}");
                tasks = new List<Task>();
            }
        }

        // Save tasks to JSON file
        private void SaveTasks()
        {
            try
            {
                var options = new JsonSerializerOptions
                {
                    WriteIndented = true
                };

                string jsonContent = JsonSerializer.Serialize(tasks, options);
                File.WriteAllText(FileName, jsonContent);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error saving tasks: {ex.Message}");
            }
        }

        // Get next available ID
        private int GetNextId()
        {
            if (tasks.Count == 0)
            {
                return 1;
            }
            return tasks.Max(t => t.Id) + 1;
        }

        // Add a new task
        public void AddTask(string description)
        {
            var task = new Task(GetNextId(), description);
            tasks.Add(task);
            SaveTasks();
            Console.WriteLine($"Task added successfully (ID: {task.Id})");
        }

        // Update task description
        public void UpdateTask(int id, string newDescription)
        {
            var task = tasks.FirstOrDefault(t => t.Id == id);
            if (task != null)
            {
                task.UpdateDescription(newDescription);
                SaveTasks();
                Console.WriteLine("Task updated successfully");
            }
            else
            {
                Console.Error.WriteLine($"Task with ID {id} not found");
            }
        }

        // Delete a task
        public void DeleteTask(int id)
        {
            var task = tasks.FirstOrDefault(t => t.Id == id);
            if (task != null)
            {
                tasks.Remove(task);
                SaveTasks();
                Console.WriteLine("Task deleted successfully");
            }
            else
            {
                Console.Error.WriteLine($"Task with ID {id} not found");
            }
        }

        // Mark task as in-progress
        public void MarkInProgress(int id)
        {
            UpdateTaskStatus(id, "in-progress");
        }

        // Mark task as done
        public void MarkDone(int id)
        {
            UpdateTaskStatus(id, "done");
        }

        // Update task status
        private void UpdateTaskStatus(int id, string status)
        {
            var task = tasks.FirstOrDefault(t => t.Id == id);
            if (task != null)
            {
                task.UpdateStatus(status);
                SaveTasks();
                Console.WriteLine($"Task marked as {status}");
            }
            else
            {
                Console.Error.WriteLine($"Task with ID {id} not found");
            }
        }

        // List all tasks
        public void ListAllTasks()
        {
            if (tasks.Count == 0)
            {
                Console.WriteLine("No tasks found");
                return;
            }

            Console.WriteLine("\nAll Tasks:");
            Console.WriteLine("==========");
            foreach (var task in tasks)
            {
                Console.WriteLine(task);
            }
        }

        // List tasks by status
        public void ListTasksByStatus(string status)
        {
            var filteredTasks = tasks.Where(t => t.Status == status).ToList();

            if (filteredTasks.Count == 0)
            {
                Console.WriteLine($"No {status} tasks found");
                return;
            }

            Console.WriteLine($"\n{status.ToUpper()} Tasks:");
            Console.WriteLine("==========");
            foreach (var task in filteredTasks)
            {
                Console.WriteLine(task);
            }
        }
    }
}
```

**What This Does:**
- Uses System.Text.Json for JSON operations (built into .NET)
- Manages all task operations with LINQ for cleaner code
- Handles file I/O with proper error handling
- Automatically creates JSON file if it doesn't exist
- Uses C# conventions (properties, LINQ, null-coalescing operators)

---

## Step 4: Create the Main Program

**File: `Program.cs`**

```csharp
using System;
using System.Linq;

namespace TaskTracker
{
    class Program
    {
        static void Main(string[] args)
        {
            var taskManager = new TaskManager();

            if (args.Length == 0)
            {
                PrintUsage();
                return;
            }

            string command = args[0];

            try
            {
                switch (command.ToLower())
                {
                    case "add":
                        if (args.Length < 2)
                        {
                            Console.Error.WriteLine("Error: Please provide a task description");
                            break;
                        }
                        string description = string.Join(" ", args.Skip(1));
                        taskManager.AddTask(description);
                        break;

                    case "update":
                        if (args.Length < 3)
                        {
                            Console.Error.WriteLine("Error: Please provide task ID and new description");
                            break;
                        }
                        int updateId = int.Parse(args[1]);
                        string newDescription = string.Join(" ", args.Skip(2));
                        taskManager.UpdateTask(updateId, newDescription);
                        break;

                    case "delete":
                        if (args.Length < 2)
                        {
                            Console.Error.WriteLine("Error: Please provide task ID");
                            break;
                        }
                        int deleteId = int.Parse(args[1]);
                        taskManager.DeleteTask(deleteId);
                        break;

                    case "mark-in-progress":
                        if (args.Length < 2)
                        {
                            Console.Error.WriteLine("Error: Please provide task ID");
                            break;
                        }
                        int progressId = int.Parse(args[1]);
                        taskManager.MarkInProgress(progressId);
                        break;

                    case "mark-done":
                        if (args.Length < 2)
                        {
                            Console.Error.WriteLine("Error: Please provide task ID");
                            break;
                        }
                        int doneId = int.Parse(args[1]);
                        taskManager.MarkDone(doneId);
                        break;

                    case "list":
                        if (args.Length == 1)
                        {
                            taskManager.ListAllTasks();
                        }
                        else
                        {
                            string status = args[1];
                            taskManager.ListTasksByStatus(status);
                        }
                        break;

                    default:
                        Console.Error.WriteLine($"Error: Unknown command '{command}'");
                        PrintUsage();
                        break;
                }
            }
            catch (FormatException)
            {
                Console.Error.WriteLine("Error: Invalid task ID. Please provide a number.");
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"Error: {ex.Message}");
            }
        }

        static void PrintUsage()
        {
            Console.WriteLine("Task Tracker CLI");
            Console.WriteLine("===============");
            Console.WriteLine("\nUsage:");
            Console.WriteLine("  dotnet run add <description>        - Add a new task");
            Console.WriteLine("  dotnet run update <id> <description> - Update a task");
            Console.WriteLine("  dotnet run delete <id>              - Delete a task");
            Console.WriteLine("  dotnet run mark-in-progress <id>    - Mark task as in-progress");
            Console.WriteLine("  dotnet run mark-done <id>           - Mark task as done");
            Console.WriteLine("  dotnet run list                     - List all tasks");
            Console.WriteLine("  dotnet run list done                - List done tasks");
            Console.WriteLine("  dotnet run list todo                - List todo tasks");
            Console.WriteLine("  dotnet run list in-progress         - List in-progress tasks");
        }
    }
}
```

**What This Does:**
- Entry point of the C# application
- Parses command-line arguments using LINQ
- Routes commands to TaskManager methods
- Handles errors with try-catch blocks
- Provides usage information

---

## Step 5: Build and Run (C#)

### Build the Project

```bash
dotnet build
```

### Run the Application

```bash
# Add tasks
dotnet run add "Buy groceries"
dotnet run add "Write blog post"
dotnet run add "Study C#"

# List all tasks
dotnet run list

# Update a task
dotnet run update 1 "Buy groceries and cook dinner"

# Mark task as in-progress
dotnet run mark-in-progress 2

# Mark task as done
dotnet run mark-done 1

# List tasks by status
dotnet run list done
dotnet run list todo
dotnet run list in-progress

# Delete a task
dotnet run delete 3
```

### Create a Standalone Executable (Optional)

To create an executable that can run without `dotnet run`:

```bash
dotnet publish -c Release -r win-x64 --self-contained false
```

This creates an executable in `bin/Release/net6.0/win-x64/publish/TaskTracker.exe`

You can then run it directly:
```bash
TaskTracker.exe add "Buy groceries"
```

For different platforms:
- Windows: `-r win-x64`
- Linux: `-r linux-x64`
- macOS: `-r osx-x64`

---

# Testing Your Application

## Test Cases

### Test 1: Add Tasks
```bash
# Java
java TaskTracker add "Task 1"
java TaskTracker add "Task 2"

# C#
dotnet run add "Task 1"
dotnet run add "Task 2"
```

**Expected:** Two tasks created with IDs 1 and 2

### Test 2: List All Tasks
```bash
# Java
java TaskTracker list

# C#
dotnet run list
```

**Expected:** Display all tasks with their details

### Test 3: Update Task
```bash
# Java
java TaskTracker update 1 "Updated Task 1"

# C#
dotnet run update 1 "Updated Task 1"
```

**Expected:** Task 1 description updated, timestamp changed

### Test 4: Mark Task Status
```bash
# Java
java TaskTracker mark-in-progress 1
java TaskTracker mark-done 2

# C#
dotnet run mark-in-progress 1
dotnet run mark-done 2
```

**Expected:** Task statuses updated

### Test 5: Filter Tasks
```bash
# Java
java TaskTracker list done
java TaskTracker list in-progress

# C#
dotnet run list done
dotnet run list in-progress
```

**Expected:** Only tasks with specified status displayed

### Test 6: Delete Task
```bash
# Java
java TaskTracker delete 1

# C#
dotnet run delete 1
```

**Expected:** Task removed from list

### Test 7: Error Handling
```bash
# Java
java TaskTracker delete 999
java TaskTracker update abc "Test"

# C#
dotnet run delete 999
dotnet run update abc "Test"
```

**Expected:** Appropriate error messages

---

# Common Challenges & Solutions

## Challenge 1: JSON File Not Found
**Problem:** Application crashes when tasks.json doesn't exist

**Solution:** Both implementations automatically create the file if it doesn't exist. Check the `LoadTasks()` method:
```java
// Java
if (!file.exists()) {
    saveTasks();
    return;
}
```

```csharp
// C#
if (!File.Exists(FileName))
{
    SaveTasks();
    return;
}
```

## Challenge 2: Command-Line Arguments with Spaces
**Problem:** Descriptions with spaces are split into multiple arguments

**Solution:** Use quotes around descriptions:
```bash
java TaskTracker add "Buy groceries and milk"
```

Or join all arguments after the command:
```java
String.join(" ", Arrays.copyOfRange(args, 1, args.length))
```

## Challenge 3: ID Auto-Increment
**Problem:** Duplicate IDs after deletion

**Solution:** Use the maximum existing ID + 1:
```java
// Java
return tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
```

```csharp
// C#
return tasks.Max(t => t.Id) + 1;
```

## Challenge 4: Timestamp Format
**Problem:** Inconsistent date formats

**Solution:** Use ISO 8601 format:
```java
// Java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
```

```csharp
// C#
DateTime.Now.ToString("yyyy-MM-ddTHH:mm:ss");
```

## Challenge 5: JSON Parsing Errors
**Problem:** Malformed JSON causes crashes

**Solution:** Wrap JSON operations in try-catch blocks and validate content:
```java
if (content.trim().isEmpty()) {
    content = "[]";
}
```

---

# Enhancements You Can Add

Once you've completed the basic implementation, try these enhancements:

1. **Priority Levels:** Add low, medium, high priority to tasks
2. **Due Dates:** Add deadline tracking
3. **Categories/Tags:** Organize tasks by category
4. **Search Functionality:** Search tasks by keyword
5. **Statistics:** Show task completion statistics
6. **Export/Import:** Export tasks to CSV or import from file
7. **Task History:** Keep track of all changes to a task
8. **Recurring Tasks:** Support for recurring tasks
9. **Color-Coded Output:** Use ANSI colors in terminal
10. **Task Dependencies:** Link tasks that depend on others

---

# Learning Resources

## Java
- Oracle Java Documentation: https://docs.oracle.com/en/java/
- JSON Processing: https://github.com/stleary/JSON-java
- File I/O: https://docs.oracle.com/javase/tutorial/essential/io/

## C#
- Microsoft .NET Documentation: https://docs.microsoft.com/en-us/dotnet/
- System.Text.Json: https://docs.microsoft.com/en-us/dotnet/standard/serialization/system-text-json-overview
- File I/O: https://docs.microsoft.com/en-us/dotnet/standard/io/

---

# Troubleshooting

## Java Issues

**Problem:** `ClassNotFoundException` for JSON library
**Solution:** Ensure JSON JAR is in classpath when compiling and running

**Problem:** `NoSuchFileException`
**Solution:** Run from the directory containing your .class files and ensure write permissions

## C# Issues

**Problem:** `System.Text.Json` not found
**Solution:** Ensure you're using .NET Core 3.0+ or .NET 5+. For older versions, install the NuGet package:
```bash
dotnet add package System.Text.Json
```

**Problem:** `JsonException` when parsing
**Solution:** Check that your JSON file is valid. Delete it and let the app recreate it.

---

# Final Notes

**Congratulations!** You now have a complete understanding of how to build a Task Tracker CLI in both Java and C#. 

**Key Takeaways:**
- Command-line argument parsing
- File I/O operations
- JSON serialization/deserialization
- Object-oriented design (classes, methods, encapsulation)
- Error handling and validation
- CRUD operations (Create, Read, Update, Delete)

**Next Steps:**
1. Implement the basic version in your preferred language
2. Test all commands thoroughly
3. Add enhancements to make it your own
4. Share your project on GitHub!

Good luck with your implementation! 🚀
