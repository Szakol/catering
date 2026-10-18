package pl.piatek.catering.service.sender;

public interface SmsSender {
    void send(String phone, String message);
}
