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

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, "public")));

// API endpoint for file upload
app.post("/api/uploads", (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: "No file uploaded." });
  }
  console.log(req.file);
  res.status(200).json({
    message: "File uploaded successfully.",
    filename: req.file.originalname,
  });
});
// app.post("/api/uploads", upload.single("pdfFile"), (req, res) => {
//   if (!req.file) {
//     return res.status(400).json({ error: "No file uploaded." });
//   }
//   res.status(200).json({
//     message: "File uploaded successfully.",
//     filename: req.file.originalname,
//   });
// });

// GET endpoint
app.get("/api/data/:id", (req, res) => {
  // Handle the parameters as needed
  res
    .status(200)
    .json({ message: "GET request received.", ...req.query, ...req.params });
});

// Start the server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
