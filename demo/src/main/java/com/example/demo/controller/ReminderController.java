package com.example.demo.controller;

import com.example.demo.service.impl.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/booking/")
@RequiredArgsConstructor
public class ReminderController {
    private final ReminderService reminderService;

    @GetMapping("/run-reminder")
    public String runReminderManually() {
        reminderService.scanAndNotify();
        return "Reminder task executed!";
    }

}
