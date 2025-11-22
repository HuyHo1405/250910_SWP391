package com.example.demo.controller;

import com.example.demo.model.dto.ReminderInfo;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IMailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminder Management", description = "APIs for testing and triggering maintenance reminders")
public class ReminderController {

    private final VehicleRepo vehicleRepo;
    private final BookingRepo bookingRepo;
    private final IMailService mailService;

    private static final double FIXED_RATE = 40.0;
    private static final double NOTIFY_BEFORE_KM = 2000.0;

    @PostMapping("/trigger-scan")
    @Operation(
            summary = "Manually trigger reminder scan (FOR DEMO/TESTING)",
            description = "Scans all vehicles and sends maintenance reminders to customers whose vehicles are approaching maintenance thresholds. " +
                    "Returns detailed information about which vehicles triggered reminders and which emails were sent."
    )
    public ResponseEntity<Map<String, Object>> triggerReminderScan() {
        log.info("=== MANUAL REMINDER SCAN TRIGGERED AT {} ===", LocalDateTime.now());
        
        List<Vehicle> vehicles = vehicleRepo.findAll();
        List<Map<String, Object>> triggeredReminders = new ArrayList<>();
        List<Map<String, Object>> skippedVehicles = new ArrayList<>();
        int emailsSent = 0;

        for (Vehicle vehicle : vehicles) {
            User customer = vehicle.getCustomer();
            ReminderInfo info = getLastMaintenanceInfo(customer.getId(), vehicle.getVin(), vehicle);
            
            long daysSinceLastVisit = ChronoUnit.DAYS.between(
                    info.getLastDate().toLocalDate(),
                    LocalDateTime.now().toLocalDate()
            );
            
            double predictedKm = info.getLastOdometer() + (daysSinceLastVisit * FIXED_RATE);
            double nextThreshold = getNextThreshold(info.getLastOdometer());
            double notifyThreshold = nextThreshold - NOTIFY_BEFORE_KM;

            Map<String, Object> vehicleInfo = new HashMap<>();
            vehicleInfo.put("vin", vehicle.getVin());
            vehicleInfo.put("plateNumber", vehicle.getPlateNumber());
            vehicleInfo.put("vehicleName", vehicle.getName());
            vehicleInfo.put("customerName", customer.getFullName());
            vehicleInfo.put("customerEmail", customer.getEmailAddress());
            vehicleInfo.put("lastMaintenanceKm", info.getLastOdometer());
            vehicleInfo.put("lastMaintenanceDate", info.getLastDate());
            vehicleInfo.put("daysSinceMaintenance", daysSinceLastVisit);
            vehicleInfo.put("predictedCurrentKm", predictedKm);
            vehicleInfo.put("nextThresholdKm", nextThreshold);
            vehicleInfo.put("notifyThresholdKm", notifyThreshold);

            if (predictedKm >= notifyThreshold) {
                log.info("✅ REMINDER TRIGGERED - Vehicle: {}, Customer: {}, Email: {}, Threshold: {}km, Predicted: {}km",
                        vehicle.getPlateNumber(), customer.getFullName(), customer.getEmailAddress(), nextThreshold, predictedKm);
                
                // Send email
                try {
                    mailService.sendReminderMail(customer, vehicle, nextThreshold);
                    vehicleInfo.put("emailSent", true);
                    vehicleInfo.put("emailSentTo", customer.getEmailAddress());
                    vehicleInfo.put("reminderMessage", String.format(
                            "Reminder sent: Your %s (%s) is approaching %,.0f km maintenance threshold. Current predicted: %,.0f km",
                            vehicle.getName(), vehicle.getPlateNumber(), nextThreshold, predictedKm
                    ));
                    emailsSent++;
                } catch (Exception e) {
                    log.error("Failed to send email to {}: {}", customer.getEmailAddress(), e.getMessage());
                    vehicleInfo.put("emailSent", false);
                    vehicleInfo.put("error", e.getMessage());
                }
                
                triggeredReminders.add(vehicleInfo);
            } else {
                log.info("⏭️  SKIPPED - Vehicle: {}, Predicted: {}km < Notify threshold: {}km",
                        vehicle.getPlateNumber(), predictedKm, notifyThreshold);
                vehicleInfo.put("reason", String.format("Predicted km (%.0f) is below notification threshold (%.0f)", predictedKm, notifyThreshold));
                skippedVehicles.add(vehicleInfo);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("scanTime", LocalDateTime.now());
        response.put("totalVehiclesScanned", vehicles.size());
        response.put("remindersTriggered", triggeredReminders.size());
        response.put("emailsSent", emailsSent);
        response.put("vehiclesSkipped", skippedVehicles.size());
        response.put("triggeredReminders", triggeredReminders);
        response.put("skippedVehicles", skippedVehicles);
        response.put("message", String.format("Scan complete: %d reminders sent out of %d vehicles scanned", emailsSent, vehicles.size()));

        log.info("=== SCAN COMPLETE: {} emails sent, {} vehicles scanned ===", emailsSent, vehicles.size());
        
        return ResponseEntity.ok(response);
    }

    private ReminderInfo getLastMaintenanceInfo(Long customerId, String vin, Vehicle vehicle) {
        Booking lastFinishedBooking = bookingRepo.findTopByCustomerIdAndVehicleVinAndBookingStatusOrderByScheduleDateDesc(
                customerId, vin, BookingStatus.MAINTENANCE_COMPLETE
        );

        if (lastFinishedBooking == null) {
            return new ReminderInfo(0.0, vehicle.getPurchasedAt());
        }
        return ReminderInfo.builder()
                .lastOdometer(lastFinishedBooking.getVehicle().getDistanceTraveledKm())
                .lastDate(lastFinishedBooking.getScheduleDate())
                .build();
    }

    private double getNextThreshold(double lastKm) {
        double[] thresholds = {10000.0, 20000.0, 30000.0, 40000.0, 50000.0, 60000.0};
        for (double t : thresholds) {
            if (t > lastKm) return t;
        }
        return thresholds[thresholds.length - 1];
    }
}
