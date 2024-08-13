package ru.saynurdinov.task_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.saynurdinov.task_service.entity.Task;
import ru.saynurdinov.task_service.entity.enums.TaskPriority;
import ru.saynurdinov.task_service.entity.enums.TaskStatus;


public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(t.creator.id = :creatorId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    Slice<Task> findByCreatorId(@Param("creatorId") long creatorId,
                                @Param("status") TaskStatus status,
                                @Param("priority") TaskPriority priority,
                                Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
            "(t.executor.id = :executorId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    Slice<Task> findByExecutorId(@Param("executorId") long executorId ,
                                 @Param("status") TaskStatus status,
                                 @Param("priority") TaskPriority priority,
                                 Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
            "(t.executor.id = :userId OR t.creator.id = :userId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    Slice<Task> findAllByUserId(@Param("userId") long userId ,
                                @Param("status") TaskStatus status,
                                @Param("priority") TaskPriority priority,
                                Pageable pageable);


}

