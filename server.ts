import express from "express";
import multer from "multer";
import path from "path";

const app = express();
const PORT = 3000;

// Middleware to parse JSON bodies
app.use(express.json());

// Multer storage configuration
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, path.join(__dirname, "uploads")); // Uploads will be saved in 'uploads' directory
  },
  filename: (req, file, cb) => {
    cb(null, file.originalname); // Use the original filename
  },
});

// Multer upload instance
const upload = multer({ storage });

// API endpoint for file upload
app.post("/api/uploads", upload.single("pdfFile"), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: "No file uploaded." });
  }
  res.status(200).json({
    message: "File uploaded successfully.",
    filename: req.file.originalname,
  });
});

// Another API endpoint that expects JSON data
app.post("/api/data", (req, res) => {
  const { name, age } = req.body;
  if (!name || !age) {
    return res.status(400).json({ error: "Name and age are required." });
  }
  res.status(200).json({ message: "Data received successfully.", name, age });
});

// GET endpoint
app.get("/api/data", (req, res) => {
  const { param1, param2 } = req.query; // Access query parameters

  // Handle the parameters as needed
  res.status(200).json({ message: "GET request received.", param1, param2 });
});

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
