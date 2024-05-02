import express from 'express';
import path from 'path';
import bodyParser from 'body-parser';

const app = express();
const PORT = 3000;

// Middleware to parse JSON bodies
app.use(bodyParser.json());

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// API endpoint example
app.get('/api/data', (req, res) => {
  // Log timestamp
  const currentTime = new Date();
  console.log(`Request received at ${currentTime.toLocaleTimeString()}`);
  // Dummy data for demonstration
  const data = {
    message: `${currentTime.toLocaleTimeString()}: Hello from the API!`,
  };
  res.json(data);
});

// Start the server
app.listen(PORT, () => {
  console.log(`PDF Server is running on port ${PORT}`);
});
