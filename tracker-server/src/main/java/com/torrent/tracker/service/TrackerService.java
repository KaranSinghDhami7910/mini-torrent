package com.torrent.tracker.service;

import com.torrent.tracker.model.TorrentFile;
import com.torrent.tracker.repository.TorrentRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrackerService {

    private final TorrentRepository torrentRepository;

    public TrackerService(TorrentRepository torrentRepository) {
        this.torrentRepository = torrentRepository;
    }

    // ✅ Save Torrent
    public TorrentFile saveTorrent(TorrentFile torrentFile) {
        return torrentRepository.save(torrentFile);
    }

    // ✅ Get Torrent by ID
    public Optional<TorrentFile> getTorrentById(Long id) {
        return torrentRepository.findById(id);
    }

    // ✅ Get All Torrents
    public List<TorrentFile> getAllTorrents() {
        return torrentRepository.findAll();
    }

    // ✅ Check if Torrent Exists
    public boolean torrentExists(Long id) {
        return torrentRepository.existsById(id);
    }

    // ✅ Delete Torrent by ID
    public void deleteTorrent(Long id) {
        torrentRepository.deleteById(id);
    }
}
