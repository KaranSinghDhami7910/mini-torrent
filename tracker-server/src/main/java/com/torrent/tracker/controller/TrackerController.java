package com.torrent.tracker.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.torrent.tracker.model.TorrentFile;
import com.torrent.tracker.repository.TorrentRepository;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/torrents")
public class TrackerController {

    private static final Logger logger = LoggerFactory.getLogger(TrackerController.class);
    private final TorrentRepository torrentRepository;
    private static final String UPLOAD_DIR = "C:/torrents/"; // ✅ Define storage path

    public TrackerController(TorrentRepository torrentRepository) {
        this.torrentRepository = torrentRepository;
    }

    // ✅ TEST ENDPOINT: Check if API is running
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("✅ Mini Torrent API is running successfully!");
    }

    // ✅ Initialize Sample Data if Empty
    @PostConstruct
    public void init() {
        logger.info("✅ TrackerController Initialized!");

        if (torrentRepository.count() == 0) {
            logger.info("🔹 Adding Sample Files...");
            List<TorrentFile> sampleTorrents = Arrays.asList(
                new TorrentFile("Sample1.txt", "HASH_123ABC", 1024L, "Sample text file"),
                new TorrentFile("Sample2.pdf", "HASH_456DEF", 2048L, "Sample PDF file"),
                new TorrentFile("Sample3.mp4", "HASH_789GHI", 4096L, "Sample video file")
            );
            torrentRepository.saveAll(sampleTorrents);
            logger.info("✅ Sample Files Added!");
        }
    }

    // ✅ FILE UPLOAD ENDPOINT
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        logger.info("🔹 Received File Upload Request: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("❌ No file selected for upload!");
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "No file selected!"));
        }

        try {
            File uploadPath = new File(UPLOAD_DIR);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs(); // ✅ Create directory if not exists
            }

            // ✅ Save file to directory
            File savedFile = new File(UPLOAD_DIR + file.getOriginalFilename());
            file.transferTo(savedFile);

            // ✅ Save file metadata to database
            TorrentFile torrentFile = new TorrentFile(
                file.getOriginalFilename(),  
                "HASH_" + System.currentTimeMillis(), // Placeholder hash
                file.getSize(), 
                "Uploaded file"
            );
            torrentRepository.save(torrentFile);
            logger.info("✅ File Saved: {}", torrentFile);

            return ResponseEntity.ok(Collections.singletonMap("message", "File uploaded successfully!"));
        } catch (IOException e) {
            logger.error("❌ Error saving file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "File upload failed!"));
        }
    }

    // ✅ Get All Files
    @GetMapping
    public ResponseEntity<List<TorrentFile>> getAllFiles() {
        logger.info("🔹 Fetching All Files from Database...");

        List<TorrentFile> files = torrentRepository.findAll();

        if (files.isEmpty()) {
            logger.warn("❌ No Files Found in Database.");
            return ResponseEntity.noContent().build();
        } else {
            logger.info("📄 Retrieved Files: {}", files);
            return ResponseEntity.ok(files);
        }
    }

    // ✅ DOWNLOAD FILE
    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                logger.info("✅ Downloading File: {}", fileName);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                logger.warn("❌ File Not Found: {}", fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (MalformedURLException e) {
            logger.error("❌ Error Downloading File: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ✅ DELETE FILE
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            File file = filePath.toFile();

            // ✅ Check if the file exists in the database
            List<TorrentFile> files = torrentRepository.findByFileNameContainingIgnoreCase(fileName);

            if (!files.isEmpty()) {
                // ✅ Delete the file from the server
                if (file.exists() && file.delete()) {
                    torrentRepository.deleteAll(files); // ✅ Delete all matching records from DB
                    logger.info("✅ Deleted File: {}", fileName);
                    return ResponseEntity.ok(Collections.singletonMap("message", "File deleted successfully!"));
                } else {
                    logger.warn("❌ File not found on disk, but present in DB: {}", fileName);
                    torrentRepository.deleteAll(files); // ✅ Still delete from DB
                    return ResponseEntity.ok(Collections.singletonMap("message", "File deleted from database, but missing on disk!"));
                }
            } else {
                logger.warn("❌ File Not Found in Database: {}", fileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "File not found in database!"));
            }
        } catch (Exception e) {
            logger.error("❌ Error Deleting File: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "File deletion failed!"));
        }
    }
}
