package com.torrent.tracker.service;

import com.torrent.tracker.model.Peer;
import com.torrent.tracker.model.TorrentFile;
import com.torrent.tracker.repository.PeerRepository;
import com.torrent.tracker.repository.TorrentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PeerService {

    private static final Logger logger = LoggerFactory.getLogger(PeerService.class);

    private final PeerRepository peerRepository;
    private final TorrentRepository torrentRepository;

    public PeerService(PeerRepository peerRepository, TorrentRepository torrentRepository) {
        this.peerRepository = peerRepository;
        this.torrentRepository = torrentRepository;
    }

    // Method to register a peer for a specific torrent
    public Peer registerPeer(Peer peer) {
        try {
            // Check if the torrent exists
            TorrentFile torrentFile = torrentRepository.findById(peer.getTorrentFile().getId())
                    .orElseThrow(() -> new RuntimeException("Torrent not found"));

            // Save the peer with the associated torrent
            peer.setTorrentFile(torrentFile);
            logger.info("Successfully registered peer: {}", peer.getPeerIp());
            return peerRepository.save(peer);
        } catch (Exception e) {
            // Log the error message and stack trace
            logger.error("Error registering peer with IP {}: {}", peer.getPeerIp(), e.getMessage(), e);
            throw e;  // Re-throw the exception
        }
    }

    // Method to get peers by torrent ID
    public List<Peer> getPeersByTorrentId(Long torrentId) {
        try {
            logger.info("Fetching peers for torrent ID: {}", torrentId);
            List<Peer> peers = peerRepository.findByTorrentFileId(torrentId);
            logger.info("Found {} peers for torrent ID: {}", peers.size(), torrentId);
            return peers;
        } catch (Exception e) {
            // Log the error
            logger.error("Error fetching peers for torrent ID {}: {}", torrentId, e.getMessage(), e);
            throw e;
        }
    }

    // Method to update the seeding status of a peer
    public Peer updateSeedingStatus(Long peerId, boolean seeding) {
        try {
            Peer peer = peerRepository.findById(peerId)
                    .orElseThrow(() -> new RuntimeException("Peer not found"));

            // Update the seeding status of the peer
            peer.setSeeding(seeding);
            logger.info("Updated seeding status of peer ID: {} to {}", peerId, seeding);

            // Save the updated peer object and return it
            return peerRepository.save(peer);
        } catch (Exception e) {
            // Log the error
            logger.error("Error updating seeding status for peer ID {}: {}", peerId, e.getMessage(), e);
            throw e;
        }
    }

    // Method to delete a peer
    public void deletePeer(Long peerId) {
        try {
            Peer peer = peerRepository.findById(peerId)
                    .orElseThrow(() -> new RuntimeException("Peer not found"));

            peerRepository.delete(peer);
            logger.info("Successfully deleted peer ID: {}", peerId);
        } catch (Exception e) {
            // Log the error
            logger.error("Error deleting peer ID {}: {}", peerId, e.getMessage(), e);
            throw e;
        }
    }
}
