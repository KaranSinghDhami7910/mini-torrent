package com.torrent.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.torrent.tracker.model.TorrentFile;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional  // ✅ Ensures all methods execute within a transaction
public interface TorrentRepository extends JpaRepository<TorrentFile, Long> {

    // ✅ Delete all torrents with a specific file name (to handle duplicates)
    void deleteAllByFileName(String fileName);

    // ✅ Find torrents by file name (case-insensitive, supports partial matching)
    List<TorrentFile> findByFileNameContainingIgnoreCase(String fileName);

    // ✅ Find a torrent by hash value (exact match)
    Optional<TorrentFile> findByHashValue(String hashValue);
}
