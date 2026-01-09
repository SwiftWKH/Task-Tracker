import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static final String FILE_NAME = "tasks.json";
    private List<Task> tasks;

    public TaskManager(){
        this.tasks = new ArrayList<>();
        loadTasks();
    }

    private void loadTasks(){
        try{
            File file = new File(FILE_NAME);
            if(!file.exists()){
                saveTask();
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
            if(content.trim().isEmpty() || content.trim().equals("[]")){
                return;
            }

            tasks.clear();
            parseJsonArray(content);
        } catch(Exception e){
            System.err.println("Error loading tasks: "+e.getMessage());
            tasks = new ArrayList<>();
        }
    }

    private void parseJsonArray(String json) {
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) return;
        
        json = json.substring(1, json.length() - 1).trim();
        if (json.isEmpty()) return;
        
        // Find complete JSON objects by counting braces
        int braceCount = 0;
        int start = 0;
        boolean inString = false;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i-1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                if (c == '{') {
                    if (braceCount == 0) start = i;
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        parseTask(json.substring(start, i + 1));
                    }
                }
            }
        }
    }

    private void parseTask(String taskJson) {
        int id = extractInt(taskJson, "id");
        String description = extractString(taskJson, "description");
        String status = extractString(taskJson, "status");
        String createdAt = extractString(taskJson, "createdAt");
        String updatedAt = extractString(taskJson, "updatedAt");
        
        Task task = new Task(id, description, status, createdAt, updatedAt);
        tasks.add(task);
    }

    private int extractInt(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return 0;
        
        start += pattern.length();
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }
        
        if (start == end) return 0;
        return Integer.parseInt(json.substring(start, end));
    }

    private String extractString(String json, String key) {
        String pattern = "\"" + key + "\"";
        int keyStart = json.indexOf(pattern);
        if (keyStart == -1) return "";
        
        // Find the colon after the key
        int colonPos = json.indexOf(":", keyStart + pattern.length());
        if (colonPos == -1) return "";
        
        // Find the opening quote of the value
        int quoteStart = json.indexOf("\"", colonPos);
        if (quoteStart == -1) return "";
        
        // Find the closing quote of the value
        int quoteEnd = json.indexOf("\"", quoteStart + 1);
        if (quoteEnd == -1) return "";
        
        return json.substring(quoteStart + 1, quoteEnd);
    }

    private void saveTask(){
        try{
            StringBuilder json = new StringBuilder("[\n");
            for(int i = 0; i < tasks.size(); i++){
                Task task = tasks.get(i);
                json.append("  {\n");
                json.append("    \"id\": ").append(task.getId()).append(",\n");
                json.append("    \"description\": \"").append(task.getDescription()).append("\",\n");
                json.append("    \"status\": \"").append(task.getStatus()).append("\",\n");
                json.append("    \"createdAt\": \"").append(task.getCreatedAt()).append("\",\n");
                json.append("    \"updatedAt\": \"").append(task.getUpdatedAt()).append("\"\n");
                json.append("  }");
                if(i < tasks.size() - 1) json.append(",");
                json.append("\n");
            }
            json.append("]");

            Files.write(Paths.get(FILE_NAME), json.toString().getBytes());
        } catch (Exception e){
            System.err.println("Error saving tasks: "+ e.getMessage());
        }
    }

    private int getNextId(){
        if(tasks.isEmpty()){
            return 1;
        }
        return tasks.stream().mapToInt(Task::getId).max().orElse(0) + 1;
    }

    public void addTask(String description){
        Task task = new Task(getNextId(), description);
        tasks.add(task);
        saveTask();
        System.out.println("Task added successfully (ID:"+task.getId()+")");
    }

    public void updateTask(int id, String newDescription){
        Task task = findTaskById(id);
        if(task != null){
            task.setDescription(newDescription);
            saveTask();
            System.out.println("Task updated successfully");
        } else {
            System.err.println("Task with ID "+id+" not found");
        }
    }

    public void deleteTask(int id){
        Task task = findTaskById(id);
        if(task != null){
            tasks.remove(task);
            saveTask();
            System.out.println("Task deleted successfully");
        } else {
            System.err.println("Task with ID "+id+" not found");
        }
    }

    public void markInProgress(int id){
        updateTaskStatus(id, "in-progress");
    }

    public void markDone(int id){
        updateTaskStatus(id, "done");
    }

    private void updateTaskStatus(int id, String status){
        Task task = findTaskById(id);
        if(task != null){
            task.setStatus(status);
            saveTask();
            System.out.println("Task marked as "+status);
        } else {
            System.err.println("Task with ID "+id+" not found");
        }
    }

    private Task findTaskById(int id){
        return tasks.stream().filter(t -> t.getId() == id).findFirst().orElse(null);
    }

    public void listAllTasks(){
        if(tasks.isEmpty()){
            System.out.println("No tasks found");
            return;
        }
        System.out.println("\nAll Tasks:");
        System.out.println("------------");
        tasks.forEach(System.out::println);
    }

    public void listTasksByStatus(String status){
        List<Task> filteredTasks = new ArrayList<>();
        for(Task task : tasks){
            if(task.getStatus().equals(status)){
                filteredTasks.add(task);
            }
        }

        if(filteredTasks.isEmpty()){
            System.out.println("No "+status+ " tasks found");
            return;
        }

        System.out.println("\n"+status.toUpperCase()+" Tasks:");
        System.out.println("----------------------");
        filteredTasks.forEach(System.out::println);
    }
}
