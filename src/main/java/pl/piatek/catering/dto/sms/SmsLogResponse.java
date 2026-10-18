package pl.piatek.catering.dto.sms;

import pl.piatek.catering.entity.SmsLog;

import java.time.LocalDateTime;

public record SmsLogResponse(Long id, SmsType type, String phone, String message,
                             SmsStatus status, String error, LocalDateTime createdAt) {

    public static SmsLogResponse from(SmsLog l) {
        return new SmsLogResponse(l.getId(), l.getType(), l.getPhone(), l.getMessage(),
                l.getStatus(), l.getError(), l.getCreatedAt());
    }
}