package ua.edu.ukma.delivery;

public interface SmsGateway {
    void sendPinCode(String phoneNumber, String pinCode);
}
