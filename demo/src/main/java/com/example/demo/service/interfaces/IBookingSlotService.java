package com.example.demo.service.interfaces;

import com.example.demo.model.dto.DailyBookedSlot;

import java.util.List;

public interface IBookingSlotService {
    public List<DailyBookedSlot> getBookedSlot();
}
