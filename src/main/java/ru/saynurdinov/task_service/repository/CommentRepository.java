package ru.saynurdinov.task_service.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.saynurdinov.task_service.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Slice<Comment> findByTaskId(long id, Pageable pageable);
}
