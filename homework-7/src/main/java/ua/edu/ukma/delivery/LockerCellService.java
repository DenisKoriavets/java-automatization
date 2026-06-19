package ua.edu.ukma.delivery;

import java.util.List;

public interface LockerCellService {
    boolean hasAvailableCell(String size);
    
    String reserveCell(String parcelId, String size);
}

