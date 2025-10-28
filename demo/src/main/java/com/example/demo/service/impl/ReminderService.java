package com.example.demo.service.impl;

import com.example.demo.model.dto.ReminderInfo;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Vehicle;
import com.example.demo.model.modelEnum.BookingStatus;
import com.example.demo.repo.BookingRepo;
import com.example.demo.repo.VehicleRepo;
import com.example.demo.service.interfaces.IMailService;
import com.example.demo.service.interfaces.IReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService implements IReminderService {

    private final VehicleRepo  vehicleRepo;
    private final BookingRepo bookingRepo;
    private final IMailService mailService;

    private static final double FIXED_RATE = 40.0; // Đổi sang double
    private static final double NOTIFY_BEFORE_KM = 2000.0;

    @Override
    @Scheduled(cron = "0 0 8 * * *")
    public void scanAndNotify() {
        log.info("Scheduled scanAndNotify started at {}", LocalDateTime.now());
        List<Vehicle> vehicles = vehicleRepo.findAll();
        System.out.println(vehicles.size());
        for (Vehicle vehicle : vehicles) {
            User customer = vehicle.getUser();
            ReminderInfo info = getLastMaintenanceInfo(customer.getId(), vehicle.getVin(), vehicle);
            long daysSinceLastVisit = ChronoUnit.DAYS.between(
                    info.getLastDate().toLocalDate(),
                    LocalDateTime.now().toLocalDate()
            );
            double predictedKm = info.getLastOdometer() + (daysSinceLastVisit * FIXED_RATE);
            double nextThreshold = getNextThreshold(info.getLastOdometer());
            double notifyThreshold = nextThreshold - NOTIFY_BEFORE_KM;

            log.info("Vehicle: {}, Plate: {}, User: {}, Last maintenance: {}, Days: {}, Predicted Km: {}, Threshold: {}",
                    vehicle.getVin(), vehicle.getPlateNumber(), customer.getFullName(),
                    info.getLastDate(), daysSinceLastVisit, predictedKm, nextThreshold);

            if (predictedKm >= notifyThreshold && !alreadyNotified(customer, vehicle, nextThreshold)) {
                log.info("==> Sending reminder to {} at {}km (Predicted: {})", customer.getEmailAddress(), nextThreshold, predictedKm);
                sendNotification(customer, vehicle, nextThreshold);
                recordNotification(customer, vehicle, nextThreshold);
            } else {
                log.info("No reminder needed for vehicle:{} (predicted: {})", vehicle.getVin(), predictedKm);
            }
        }
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

    // Chú ý khai báo threshold và km là double cho toàn bộ logic
    private double getNextThreshold(double lastKm) {
        double[] thresholds = {10000.0, 20000.0, 30000.0, 40000.0, 50000.0, 60000.0};
        for (double t : thresholds) {
            if (t > lastKm) return t;
        }
        return thresholds[thresholds.length - 1];
    }

    private boolean alreadyNotified(User customer, Vehicle vehicle, double threshold) {
        // Xử lý như bình thường, chỉ kiểu threshold là double
        return false;
    }

    private void sendNotification(User customer, Vehicle vehicle, double threshold) {
        mailService.sendReminderMail(customer, vehicle, threshold);
    }

    private void recordNotification(User customer, Vehicle vehicle, double threshold) {
        // Lưu notification
    }
}
