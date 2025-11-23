# Reminder Service Demo Guide

## Overview
This guide shows how to demonstrate the **Automated Maintenance Reminder System** that sends email notifications to customers when their vehicles are approaching maintenance thresholds.

---

## 🎯 Demo Flow (Happy Path)

### **Step 1: Explain the Business Logic**

**Tell your audience:**
> "Our system automatically monitors all vehicles and predicts when they need maintenance based on:
> - Last maintenance odometer reading
> - Days since last maintenance
> - Fixed daily driving rate of 40 km/day
> 
> When a vehicle reaches within 2,000 km of the next maintenance threshold (10k, 20k, 30k, etc.), we automatically send a reminder email to the customer."

---

### **Step 2: Show the Test Vehicle Data**

**Navigate to the database or show the seed data:**

```sql
-- Test Vehicle in data.sql
Vehicle: VF 5 Test Reminder
Plate: 51K-TEST.01
VIN: VFVF51TEST000001
Customer: Nguyễn Thu Thảo (thao.nguyen@gmail.com)
Last Maintenance: 100 days ago
Last Odometer: 14,000 km
```

**Explain the calculation:**
```
Days since last visit: 100 days
Predicted current km: 14,000 + (100 × 40) = 18,000 km
Next threshold: 20,000 km
Notification threshold: 20,000 - 2,000 = 18,000 km
Result: 18,000 >= 18,000 ✅ TRIGGER REMINDER
```

---

### **Step 3: Trigger the Reminder Manually**

**Open Swagger UI:**
```
http://localhost:8080/swagger-ui/index.html
```

**Find the endpoint:**
- Navigate to **"Reminder Management"** section
- Find `POST /api/reminders/trigger-scan`
- Click **"Try it out"**
- Click **"Execute"**

---

### **Step 4: Show the Response (Evidence)**

**The API will return detailed information:**

```json
{
  "scanTime": "2025-11-22T15:40:00",
  "totalVehiclesScanned": 15,
  "remindersTriggered": 1,
  "emailsSent": 1,
  "vehiclesSkipped": 14,
  "triggeredReminders": [
    {
      "vin": "VFVF51TEST000001",
      "plateNumber": "51K-TEST.01",
      "vehicleName": "VF 5 Test Reminder",
      "customerName": "Nguyễn Thu Thảo",
      "customerEmail": "thao.nguyen@gmail.com",
      "lastMaintenanceKm": 14000.0,
      "lastMaintenanceDate": "2025-08-14T09:00:00",
      "daysSinceMaintenance": 100,
      "predictedCurrentKm": 18000.0,
      "nextThresholdKm": 20000.0,
      "notifyThresholdKm": 18000.0,
      "emailSent": true,
      "emailSentTo": "thao.nguyen@gmail.com",
      "reminderMessage": "Reminder sent: Your VF 5 Test Reminder (51K-TEST.01) is approaching 20,000 km maintenance threshold. Current predicted: 18,000 km"
    }
  ],
  "message": "Scan complete: 1 reminders sent out of 15 vehicles scanned"
}
```

**Point out the key evidence:**
- ✅ `emailSent: true`
- ✅ `emailSentTo: "thao.nguyen@gmail.com"`
- ✅ Clear reminder message with threshold details

---

### **Step 5: Show the Email (Ultimate Proof)**

**Check the email inbox** (or show email logs if using a test SMTP service):

**Option A: If using real email service**
- Open the customer's email inbox
- Show the reminder email that was sent

**Option B: If using test SMTP (like Mailtrap, Mailhog)**
- Open the test inbox
- Show the captured email

**Option C: Show application logs**
```
2025-11-22 15:40:00 INFO  - ✅ REMINDER TRIGGERED - Vehicle: 51K-TEST.01, Customer: Nguyễn Thu Thảo, Email: thao.nguyen@gmail.com, Threshold: 20000.0km, Predicted: 18000.0km
2025-11-22 15:40:01 INFO  - Email sent successfully to thao.nguyen@gmail.com
```

---

## 📊 Additional Demo Points

### **Show Why Other Vehicles Were Skipped**

In the response, show the `skippedVehicles` array:
```json
"skippedVehicles": [
  {
    "vin": "VFVF82AD3PA000101",
    "plateNumber": "51K-111.22",
    "predictedCurrentKm": 16200.0,
    "notifyThresholdKm": 18000.0,
    "reason": "Predicted km (16200) is below notification threshold (18000)"
  }
]
```

**Explain:** "This vehicle is only at 16,200 km predicted, so it's not yet within 2,000 km of the 20,000 km threshold."

---

### **Explain the Scheduled Job**

> "In production, this scan runs automatically every day at 8:00 AM using Spring's `@Scheduled` annotation. We just triggered it manually for demonstration purposes."

Show the code:
```java
@Scheduled(cron = "0 0 8 * * *")
public void scanAndNotify() {
    // Runs daily at 8 AM
}
```

---

## 🎬 Demo Script (30 seconds)

1. **"Let me show you our automated reminder system."**
2. **"Here's a test vehicle that's due for maintenance soon."** (Show data)
3. **"The system calculates it's at 18,000 km, approaching the 20,000 km threshold."**
4. **"Let me trigger the reminder scan..."** (Execute API)
5. **"As you can see, the system sent an email to the customer."** (Show response)
6. **"And here's the actual email that was sent."** (Show email/logs)
7. **"This runs automatically every morning at 8 AM."**

---

## ✅ Success Criteria

Your demo is successful if you can show:
- ✅ The calculation logic (predicted km vs threshold)
- ✅ The API response showing `emailSent: true`
- ✅ The actual email (or email logs)
- ✅ Why other vehicles were skipped

---

## 🔧 Troubleshooting

**If email doesn't send:**
- Check `IMailService` implementation
- Verify SMTP configuration in `application.properties`
- Check application logs for errors

**If no reminders trigger:**
- Verify test vehicle data exists in database
- Check the calculation manually
- Review the `skippedVehicles` array for reasons

---

## 📝 Notes for Production

- Remove or secure the manual trigger endpoint before production
- Consider adding notification history table for audit trail
- Add rate limiting to prevent duplicate emails
- Implement unsubscribe functionality
