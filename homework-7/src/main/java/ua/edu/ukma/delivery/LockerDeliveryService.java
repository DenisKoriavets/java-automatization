package ua.edu.ukma.delivery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LockerDeliveryService {

    private final LockerCellService cellService;
    private final SmsGateway smsGateway;
    private final CourierRouting courierRouting;

    public LockerDeliveryService(LockerCellService cellService,
                                 SmsGateway smsGateway, 
                                 CourierRouting courierRouting) {
        this.cellService = cellService;
        this.smsGateway = smsGateway;
        this.courierRouting = courierRouting;
    }

    public boolean processDelivery(String parcelId, String size, double weight, String phoneNumber) {
        if (weight > 30.0) {
            return false;
        }

        if (!cellService.hasAvailableCell(size)) {
            return false;
        }

        String generatedPin = cellService.reserveCell(parcelId, size);
        smsGateway.sendPinCode(phoneNumber, generatedPin);
        
        return true;
    }

    public List<String> getOptimizedRoute(String areaCode) {
        List<String> route = courierRouting.getDeliveryRoute(areaCode);
        
        if (route == null || route.isEmpty()) {
            return new ArrayList<>();
        }
        
        return route.stream()
                .filter(point -> !point.contains("MAINTENANCE"))
                .collect(Collectors.toList());
    }
}