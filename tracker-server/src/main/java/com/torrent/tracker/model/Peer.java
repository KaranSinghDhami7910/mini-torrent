package com.torrent.tracker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "peers")
public class Peer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "peer_ip")
    private String peerIp;

    @Column(name = "peer_port")
    private int peerPort;

    @Column(name = "seeding")
    private boolean seeding;

    @ManyToOne
    @JoinColumn(name = "torrent_id", referencedColumnName = "id", nullable = false)
    private TorrentFile torrentFile;

    // Constructors
    public Peer() {}

    public Peer(String peerIp, int peerPort, boolean seeding, TorrentFile torrentFile) {
        this.peerIp = peerIp;
        this.peerPort = peerPort;
        this.seeding = seeding;
        this.torrentFile = torrentFile;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getPeerIp() {
        return peerIp;
    }

    public int getPeerPort() {
        return peerPort;
    }

    public boolean isSeeding() {
        return seeding;
    }

    public TorrentFile getTorrentFile() {
        return torrentFile;
    }

    public void setPeerIp(String peerIp) {
        this.peerIp = peerIp;
    }

    public void setPeerPort(int peerPort) {
        this.peerPort = peerPort;
    }

    public void setSeeding(boolean seeding) {
        this.seeding = seeding;
    }

    public void setTorrentFile(TorrentFile torrentFile) {
        this.torrentFile = torrentFile;
    }
}
