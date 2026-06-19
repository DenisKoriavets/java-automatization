package ua.edu.ukma.delivery;

import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.SoftAssertions;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockerDeliveryServiceTest {

    @Mock
    private LockerCellService cellService;

    @Mock
    private SmsGateway smsGateway;

    @Mock
    private CourierRouting courierRouting;

    @InjectMocks
    private LockerDeliveryService deliveryService;

    @Test
    @DisplayName("Сценарій 1: Відмова через перевищення ваги (перевірка never)")
    void processDelivery_ShouldReturnFalse_WhenWeightExceedsLimit() {
        boolean result = deliveryService.processDelivery("PARCEL-001", "L", 35.0, "+380991234567");

        assertFalse(result, "Посилка важча за 30 кг не має бути прийнята");

        verify(smsGateway, never()).sendPinCode(anyString(), anyString());
    }

    @Test
    @DisplayName("Сценарій 2: Відмова через відсутність вільних комірок (перевірка when/thenReturn)")
    void processDelivery_ShouldReturnFalse_WhenNoCellsAvailable() {
        when(cellService.hasAvailableCell("M")).thenReturn(false);

        boolean result = deliveryService.processDelivery("PARCEL-002", "M", 15.0, "+380991234567");

        assertFalse(result, "Якщо немає вільних комірок, метод має повернути false");
        verify(smsGateway, never()).sendPinCode(anyString(), anyString());
    }

    @Test
    @DisplayName("Сценарій 3: Успішна доставка (перевірка times)")
    void processDelivery_ShouldReturnTrue_WhenAllConditionsMet() {
        String expectedPin = "7788";
        when(cellService.hasAvailableCell("S")).thenReturn(true);
        when(cellService.reserveCell("PARCEL-003", "S")).thenReturn(expectedPin);

        boolean result = deliveryService.processDelivery("PARCEL-003", "S", 5.0, "+380990000000");

        assertTrue(result, "Якщо всі умови виконані, метод має повернути true");

        verify(smsGateway, times(1)).sendPinCode("+380990000000", expectedPin);
    }

    @Test
    @DisplayName("Сценарій 4: Використання SoftAssertions для перевірки маршруту")
    void getOptimizedRoute_ShouldFilterCorrectly_UsingSoftAssertions() {
        when(courierRouting.getDeliveryRoute("02000")).thenReturn(
            Arrays.asList("LOCKER-1", "LOCKER-2-MAINTENANCE", "LOCKER-3")
        );

        List<String> result = deliveryService.getOptimizedRoute("02000");

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(result)
            .as("Перевірка на відсутність null")
            .isNotNull();

        softly.assertThat(result.size())
            .as("Розмір списку має бути 2, бо зламаний поштомат викинуто")
            .isEqualTo(2);

        softly.assertThat(result.getFirst())
            .as("Першим у списку має бути LOCKER-1")
            .isEqualTo("LOCKER-1");

        softly.assertAll();
    }

    @Test
    @DisplayName("Сценарій 5: Ланцюжкова перевірка списків (Fluent API)")
    void getOptimizedRoute_ShouldReturnValidList_WithAssertJListVerifications() {
        when(courierRouting.getDeliveryRoute("03000")).thenReturn(
            Arrays.asList("L-A", "L-B-MAINTENANCE", "L-C")
        );

        List<String> result = deliveryService.getOptimizedRoute("03000");

        assertThat(result)
            .isNotEmpty()
            .hasSize(2)
            .contains("L-A", "L-C")
            .doesNotContain("L-B-MAINTENANCE")
            .containsExactlyInAnyOrder("L-C", "L-A");
    }

    @Test
    @DisplayName("Сценарій 6: Захист від null")
    void getOptimizedRoute_ShouldReturnEmptyList_WhenRouteIsNull() {
        when(courierRouting.getDeliveryRoute("ERROR-ZONE")).thenReturn(null);

        List<String> result = deliveryService.getOptimizedRoute("ERROR-ZONE");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Сценарій 7: Захист від порожнього маршруту")
    void getOptimizedRoute_ShouldReturnEmptyList_WhenRouteIsEmpty() {
        when(courierRouting.getDeliveryRoute("EMPTY-ZONE")).thenReturn(new ArrayList<>());

        List<String> result = deliveryService.getOptimizedRoute("EMPTY-ZONE");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Слабкий тест: Перевірка ваги без урахування граничного значення")
    void processDelivery_WeakTestForWeight() {
        boolean resultTooHeavy = deliveryService.processDelivery("P-WEAK-1", "L", 35.0, "+380990000000");
        assertFalse(resultTooHeavy, "35 кг має бути відхилено");

        when(cellService.hasAvailableCell("L")).thenReturn(true);
        boolean resultLight = deliveryService.processDelivery("P-WEAK-2", "L", 10.0, "+380990000000");
        assertTrue(resultLight, "10 кг має бути прийнято");
    }

    @Test
    @DisplayName("Виправлений тест: Перевірка граничного значення ваги")
    void processDelivery_StrongTestForWeightBoundary() {
        when(cellService.hasAvailableCell("L")).thenReturn(true);

        boolean resultBoundary = deliveryService.processDelivery("P-STRONG", "L", 30.0, "+380991111111");

        assertTrue(resultBoundary, "Посилка вагою рівно 30.0 кг має бути прийнята");
    }
}