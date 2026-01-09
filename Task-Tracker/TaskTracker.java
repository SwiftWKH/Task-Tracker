import java.util.Arrays;

public class TaskTracker{
    public static TaskManager taskManager;
    public static void main(String[] args) {
        taskManager = new TaskManager();

        if(args.length == 0){
            printUsage();
            return;
        }

        String command = args[0];

        try{
            switch(command){
                case "add":
                    if(args.length < 2){
                        System.err.println("Error: Please provide a task description");
                        break;
                    }
                    String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    taskManager.addTask(description);
                    break;
                
                case "update":
                    if(args.length < 3){
                        System.err.println("Error: Please provide task ID and new description");
                        break;
                    }
                    int updateId = Integer.parseInt(args[1]);
                    String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                    taskManager.updateTask(updateId, newDescription);
                    break;

                case "delete":
                    if(args.length < 2){
                        System.err.println("Error: Please provide task ID");
                        break;
                    }
                    int deleteId = Integer.parseInt(args[1]);
                    taskManager.deleteTask(deleteId);
                    break;
                
                case "mark-in-progress":
                    if(args.length < 2){
                        System.err.println("Error: Please provide task ID");
                        break;
                    }
                    int progressId = Integer.parseInt(args[1]);
                    taskManager.markInProgress(progressId);
                    break;

                case "mark-done":
                    if(args.length < 2){
                        System.err.println("Error: Please provide task ID");
                        break;
                    }
                    int doneId = Integer.parseInt(args[1]);
                    taskManager.markDone(doneId);
                    break;

                case "list":
                    if(args.length == 1){
                        taskManager.listAllTasks();
                    } else {
                        String status = args[1];
                        taskManager.listTasksByStatus(status);
                    }
                    break;

                default:
                    System.err.println("Error: Unknown Command '"+command+"'");
                    printUsage();
            }
        } catch (NumberFormatException e){
            System.err.println("Error: Invalid task ID. Please provide a number.");
        } catch (Exception e){
            System.err.println("Error: " +e.getMessage());
        }
    }

    public static void printUsage(){
        System.out.println("Task Tracker CLI");
        System.out.println("----------------");
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
