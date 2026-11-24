import Models.Task;
import Models.TaskMetadata;
import Models.TaskView;
import Models.User;
import Observers.Task.TaskObserver;
import Services.Notification.InAppNotification;
import Services.Notification.TaskNotificationObserver;
import Services.Notification.UserNotification;
import Services.TaskAudit.TaskAuditService;
import Services.TaskAudit.TaskAuditServiceImpl;
import Services.TaskInventory.TaskInventoryService;
import Services.TaskInventory.TaskInventoryServiceImpl;
import Services.TaskSearch.TaskFilter;
import Services.TaskSearch.TaskSearchService;
import Services.TaskSearch.TaskSearchServiceImpl;
import Store.TaskStore;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskStore store = new TaskStore();
        TaskInventoryService inventoryService = new TaskInventoryServiceImpl(store);
        TaskSearchService searchService = new TaskSearchServiceImpl(store);
        TaskAuditService auditService = new TaskAuditServiceImpl();

        inventoryService.attach(new TaskNotificationObserver(new InAppNotification()));
        inventoryService.attach(auditService);

        User nikhil = User.builder().name("Nikhil").email("nikhilbabarjee@gmail.com").build();
        User rohit = User.builder().name("Rohit").email("rohit.deo@gmail.com").build();

        TaskMetadata data = TaskMetadata.builder()
                .title("LLD: Parking LOT")
                .description("Complete the lld design")
                .assignee(rohit)
                .dueDate(LocalDateTime.now().plusDays(1))
                .labels(List.of("LLD", "Machine Coding"))
                .build();

        TaskMetadata data2 = TaskMetadata.builder()
                .title("LLD: Movie Booking")
                .description("Complete the lld design")
                .assignee(nikhil)
                .labels(List.of("LLD", "System Design"))
                .dueDate(LocalDateTime.now().minusDays(2))
                .build();

        TaskView task = inventoryService.createTask(data, nikhil);
        TaskView task2 = inventoryService.createTask(data2, rohit);

        TaskFilter tasksPastDueDate = TaskFilter.builder().byAheadOfDueDate(true).build();

        System.out.println("Tasks passed due date: " + searchService.searchByFilter(tasksPastDueDate));

        TaskMetadata data3 = TaskMetadata.builder()
                .title("LLD: Movie Booking")
                .description("Complete the lld design")
                .assignee(nikhil)
                .labels(List.of("LLD", "System Design"))
                .dueDate(LocalDateTime.now().plusDays(4))
                .build();

        inventoryService.updateTask(task2.id(), data3, nikhil);

        System.out.println("Tasks passed due date after update: " + searchService.searchByFilter(tasksPastDueDate));
    }
}
