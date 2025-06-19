package com.app.repo;

import com.app.model.Report;
import com.app.model.User;
import com.app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndReportedMessage(User reporter, Message reportedMessage);

    //this funciton checks if a report exists for a specific reporter and reported message
    Optional<Report> findByReporterAndReportedMessage(User reporter, Message reportedMessage);

    List<Report> findAllByReporter(User user);
}
