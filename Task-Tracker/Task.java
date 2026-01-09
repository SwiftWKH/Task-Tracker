import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private int id;
    private String description;
    private String status;
    private String createdAt;
    private String updatedAt;

    public Task(int id, String description){
        this.id = id;
        this.description = description;
        this.status = "todo";
        this.createdAt = getCurrentTimestamp();
        this.updatedAt = getCurrentTimestamp();
    }

    public Task(int id, String description, String status, String createdAt, String updatedAt){
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId(){
        return id;
    }

    public String getDescription(){
        return description;
    }

    public String getStatus(){
        return status;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public String getUpdatedAt(){
        return updatedAt;
    }

    public void setDescription(String description){
        this.description = description;
        this.updatedAt = getCurrentTimestamp();
    }

    public void setStatus(String status){
        this.status = status;
        this.updatedAt = getCurrentTimestamp();
    }

    private String getCurrentTimestamp(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    @Override
    public String toString(){
        return String.format("[%d] %s - Status: %s (Created: %s, Updated: %s)",
                id, description, status, createdAt, updatedAt);
    }
}
