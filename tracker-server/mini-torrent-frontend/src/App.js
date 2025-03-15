import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";


import "bootstrap/dist/css/bootstrap.min.css";
import AppNavbar from "./components/Navbar";
import TorrentList from "./components/TorrentList";
import UploadTorrents from "./components/UploadTorrents";

function App() {
  return (
    <Router>
      {/* Navigation Bar */}
      <AppNavbar />

      {/* Main Content */}
      <div className="container mt-4">
        <Routes>
          <Route path="/" element={<TorrentList />} />
          <Route path="/upload" element={<UploadTorrents />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
