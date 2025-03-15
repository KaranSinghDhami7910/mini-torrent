package com.torrent.tracker.controller;

import com.torrent.tracker.model.Peer;
import com.torrent.tracker.service.PeerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/peers")
@CrossOrigin(origins = "*")
public class PeerController {

    private final PeerService peerService;

    // Constructor injection of PeerService
    public PeerController(PeerService peerService) {
        this.peerService = peerService;
    }

    // Endpoint to register a peer
    @PostMapping("/register")
    public Peer registerPeer(@RequestBody Peer peer) {
        return peerService.registerPeer(peer);
    }

    // Endpoint to get all peers by torrent ID
    @GetMapping("/torrent/{torrentId}")
    public List<Peer> getPeersByTorrentId(@PathVariable Long torrentId) {
        return peerService.getPeersByTorrentId(torrentId);
    }

    // Endpoint to update the seeding status of a peer
    @PutMapping("/{peerId}/seeding")
    public Peer updateSeedingStatus(@PathVariable Long peerId, @RequestParam boolean seeding) {
        return peerService.updateSeedingStatus(peerId, seeding);
    }

    // Endpoint to delete a peer
    @DeleteMapping("/{peerId}")
    public void deletePeer(@PathVariable Long peerId) {
        peerService.deletePeer(peerId);
    }
}
