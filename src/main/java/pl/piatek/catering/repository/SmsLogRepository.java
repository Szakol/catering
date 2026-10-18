package pl.piatek.catering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piatek.catering.dto.sms.SmsStatus;
import pl.piatek.catering.dto.sms.SmsType;
import pl.piatek.catering.entity.SmsLog;

import java.util.List;

public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {

    List<SmsLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    boolean existsByOrderIdAndTypeAndStatus(Long orderId, SmsType type, SmsStatus status);
}
