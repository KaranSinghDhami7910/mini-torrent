package com.torrent.tracker; // Ensure the package matches your folder structure

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrackerServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackerServerApplication.class, args);
        System.out.println("✅ Mini Torrent Tracker Server is Running...");
    }
}

