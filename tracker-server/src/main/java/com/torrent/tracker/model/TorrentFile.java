package com.torrent.tracker.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a torrent file in the system.
 */
@Entity
@Table(name = "torrents")  // Table name as 'torrents'
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TorrentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false, unique = true)  
    private String fileName;

    @Column(name = "hash_value", nullable = false, unique = true)  
    private String hashValue;

    @Column(name = "file_size", nullable = false)  
    private long fileSize;

    @Column(name = "description")  
    private String description;

    // Custom constructor for easy object creation (optional if Lombok is used)
    public TorrentFile(String fileName, String hashValue, long fileSize, String description) {
        this.fileName = fileName;
        this.hashValue = hashValue;
        this.fileSize = fileSize;
        this.description = description;
    }

    // Optionally, consider adding validation for fields like fileName, hashValue, etc.
}
