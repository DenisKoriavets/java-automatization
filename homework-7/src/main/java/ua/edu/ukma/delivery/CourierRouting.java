package ua.edu.ukma.delivery;

import java.util.List;

public interface CourierRouting {
    List<String> getDeliveryRoute(String areaCode);
}
