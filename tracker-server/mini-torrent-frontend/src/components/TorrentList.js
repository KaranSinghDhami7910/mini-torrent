import React, { useEffect, useState } from "react";
import axios from "axios";
import { Container, Table, Alert, Spinner, Button, ProgressBar } from "react-bootstrap";

const API_BASE_URL = "http://localhost:8080/torrents"; // ✅ Backend API URL

const TorrentList = () => {
    const [torrents, setTorrents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [downloadProgress, setDownloadProgress] = useState({}); // ✅ Track progress for each file

    // ✅ Fetch torrents from backend
    useEffect(() => {
        const fetchTorrents = async () => {
            try {
                const response = await axios.get(API_BASE_URL);
                setTorrents([...response.data]); // ✅ Update state with fetched data
            } catch (error) {
                console.error("❌ Error Fetching Torrents:", error);
                setError("Failed to fetch torrents. Please try again!");
            }
            setLoading(false);
        };

        fetchTorrents();
    }, []);

    // ✅ Format file size
    const formatFileSize = (sizeInBytes) => {
        if (!sizeInBytes || isNaN(sizeInBytes) || sizeInBytes <= 0) {
            return "Unknown Size";
        }
    
        const sizeInMB = sizeInBytes / (1024 * 1024); // ✅ Convert Bytes to MB
        const sizeInKB = sizeInBytes / 1024; // ✅ Convert Bytes to KB
    
        return sizeInMB >= 1 
            ? sizeInMB.toFixed(2) + " MB" 
            : sizeInKB.toFixed(2) + " KB"; 
    };
    
    // ✅ Download Torrent File with Progress
    const downloadTorrent = (fileName) => {
        setDownloadProgress((prev) => ({ ...prev, [fileName]: 0 })); // ✅ Initialize progress

        axios({
            url: `${API_BASE_URL}/download/${fileName}`,
            method: "GET",
            responseType: "blob",
            onDownloadProgress: (progressEvent) => {
                const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                setDownloadProgress((prev) => ({ ...prev, [fileName]: percentCompleted }));
            },
        })
        .then((response) => {
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", fileName);
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            setTimeout(() => {
                setDownloadProgress((prev) => ({ ...prev, [fileName]: null })); // ✅ Remove progress after completion
            }, 2000);
        })
        .catch((error) => {
            console.error("❌ Error downloading file:", error);
            setDownloadProgress((prev) => ({ ...prev, [fileName]: null }));
        });
    };

    // ✅ Delete Torrent File
    const deleteTorrent = (fileName) => {
        axios.delete(`${API_BASE_URL}/delete/${fileName}`)
            .then((response) => {
                alert(response.data.message);
                setTorrents(torrents.filter(torrent => torrent.fileName !== fileName)); // ✅ Remove from UI
            })
            .catch((error) => console.error("❌ Error deleting torrent:", error));
    };

    return (
        <Container className="mt-4">
            <h2>📂 Available Torrents</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            {loading ? (
                <Spinner animation="border" />
            ) : (
                <Table striped bordered hover className="mt-3">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>File Name</th>
                            <th>Description</th>
                            <th>Size</th>
                            <th>Hash Value</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {torrents.length === 0 ? (
                            <tr>
                                <td colSpan="6" className="text-center">No torrents found</td>
                            </tr>
                        ) : (
                            torrents.map((torrent) => (
                                <tr key={torrent.id}>
                                    <td>{torrent.id}</td>
                                    <td>{torrent.fileName}</td>
                                    <td>{torrent.description}</td>
                                    <td>{formatFileSize(torrent.fileSize)}</td>
                                    <td>{torrent.hashValue}</td>
                                    <td>
                                        <Button 
                                            variant="primary" 
                                            size="sm" 
                                            className="me-2"
                                            onClick={() => downloadTorrent(torrent.fileName)}
                                            disabled={downloadProgress[torrent.fileName] !== undefined && downloadProgress[torrent.fileName] !== null}
                                        >
                                            ⬇ Download
                                        </Button>
                                        <Button 
                                            variant="danger" 
                                            size="sm"
                                            onClick={() => deleteTorrent(torrent.fileName)}
                                        >
                                            ❌ Delete
                                        </Button>

                                        {/* ✅ Show Progress Bar if Download is Active */}
                                        {downloadProgress[torrent.fileName] !== undefined && downloadProgress[torrent.fileName] !== null && (
                                            <ProgressBar 
                                                animated 
                                                now={downloadProgress[torrent.fileName]} 
                                                label={`${downloadProgress[torrent.fileName]}%`} 
                                                className="mt-2"
                                            />
                                        )}
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </Table>
            )}
        </Container>
    );
};

export default TorrentList;
