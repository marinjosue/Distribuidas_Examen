package  com.eval.conjunta.notification.repository;

import com.eval.conjunta.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByStatus(String status);
    
    List<Notification> findByNotificationType(String notificationType);
    
    List<Notification> findByPriority(String priority);
    
    List<Notification> findByEventType(String eventType);
    
    List<Notification> findByRecipient(String recipient);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.priority = :priority")
    List<Notification> findByStatusAndPriority(@Param("status") String status, @Param("priority") String priority);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.priority IN :priorities")
    List<Notification> findByStatusAndPriorityIn(@Param("status") String status, @Param("priorities") List<String> priorities);
    
    @Query("SELECT n FROM Notification n WHERE n.timestamp >= :startTime AND n.timestamp <= :endTime ORDER BY n.timestamp DESC")
    List<Notification> findByTimestampBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = 'SENT' AND n.timestamp >= :startTime")
    long countSentNotificationsAfter(@Param("startTime") Instant startTime);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = 'FAILED' AND n.timestamp >= :startTime")
    long countFailedNotificationsAfter(@Param("startTime") Instant startTime);
}
