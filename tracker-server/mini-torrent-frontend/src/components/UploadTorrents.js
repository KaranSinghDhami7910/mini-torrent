import React, { useState } from "react";
import axios from "axios";
import { Container, Form, Button, Alert, Spinner } from "react-bootstrap";
import { FaCloudUploadAlt, FaFileAlt, FaTrash } from "react-icons/fa";

const UploadTorrents = () => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [fileName, setFileName] = useState("");
    const [uploadStatus, setUploadStatus] = useState({ message: "", type: "" });
    const [loading, setLoading] = useState(false);
    const [dragActive, setDragActive] = useState(false);

    // ✅ Handle file selection
    const handleFileChange = (event) => {
        const file = event.target.files[0];
        processFile(file);
    };

    // ✅ Handle drag & drop file upload
    const handleDragOver = (event) => {
        event.preventDefault();
        setDragActive(true);
    };

    const handleDragLeave = () => {
        setDragActive(false);
    };

    const handleDrop = (event) => {
        event.preventDefault();
        setDragActive(false);
        const file = event.dataTransfer.files[0];
        processFile(file);
    };

    // ✅ Process file selection
    const processFile = (file) => {
        if (!file) return;

        if (file.size === 0) {
            setUploadStatus({ message: "❌ Empty files cannot be uploaded!", type: "danger" });
            setSelectedFile(null);
            setFileName("");
            return;
        }

        setSelectedFile(file);
        setFileName(file.name);
        setUploadStatus({ message: "", type: "" });
    };

    // ✅ Handle file upload
    const handleUpload = async () => {
        if (!selectedFile) {
            setUploadStatus({ message: "⚠️ Please select a file!", type: "warning" });
            return;
        }

        setLoading(true);
        const formData = new FormData();
        formData.append("file", selectedFile);

        try {
            const response = await axios.post("http://localhost:8080/torrents/upload", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });

            setUploadStatus({ message: `✅ ${response.data.message}`, type: "success" });
            resetFile();
        } catch (error) {
            const errorMessage = error.response?.data?.message || "❌ Upload failed. Please try again!";
            setUploadStatus({ message: errorMessage, type: "danger" });
        }

        setLoading(false);
    };

    // ✅ Reset selected file
    const resetFile = () => {
        setSelectedFile(null);
        setFileName("");
        document.getElementById("fileUpload").value = "";
    };

    return (
        <Container className="mt-4">
            <h2 className="text-center">📤 Upload File</h2>

            {uploadStatus.message && (
                <Alert variant={uploadStatus.type} className="mt-3 text-center">
                    {uploadStatus.message}
                </Alert>
            )}

            <Form>
                {/* Drag & Drop File Upload */}
                <div
                    className={`drop-zone ${dragActive ? "drag-active" : ""}`}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                >
                    <FaCloudUploadAlt size={50} className="text-secondary" />
                    <p className="mb-0">
                        {fileName ? (
                            <span>
                                <FaFileAlt size={24} className="text-primary" /> {fileName}
                                <Button variant="danger" size="sm" className="ms-2" onClick={resetFile}>
                                    <FaTrash />
                                </Button>
                            </span>
                        ) : (
                            "Drag & Drop or Click to select a file"
                        )}
                    </p>
                </div>

                {/* File Input */}
                <Form.Group controlId="fileUpload" className="mt-3 text-center">
                    <Form.Control 
                        type="file" 
                        onChange={handleFileChange} 
                        id="fileUpload"
                        className="d-none" 
                    />
                    <Button variant="outline-primary" onClick={() => document.getElementById("fileUpload").click()}>
                        Select File
                    </Button>
                </Form.Group>

                {/* Upload Button */}
                <div className="text-center mt-3">
                    <Button
                        variant="success"
                        onClick={handleUpload}
                        disabled={loading || !selectedFile}
                    >
                        {loading ? <Spinner animation="border" size="sm" /> : "Upload"}
                    </Button>
                </div>
            </Form>

            {/* Styles for Drag & Drop */}
            <style>
                {`
                .drop-zone {
                    border: 2px dashed #ccc;
                    padding: 20px;
                    text-align: center;
                    border-radius: 10px;
                    cursor: pointer;
                    transition: 0.3s;
                }
                .drop-zone.drag-active {
                    background-color: #f8f9fa;
                    border-color: #007bff;
                }
                `}
            </style>
        </Container>
    );
};

export default UploadTorrents;
