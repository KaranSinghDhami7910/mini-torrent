package com.torrent.tracker.repository;

import com.torrent.tracker.model.Peer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeerRepository extends JpaRepository<Peer, Long> {
    List<Peer> findByTorrentFileId(Long torrentId);
    // Custom queries can be added if needed, e.g., to find peers by torrent_id
}
