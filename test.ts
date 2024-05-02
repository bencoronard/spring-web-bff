import axios from 'axios';

// Define the API endpoint URL
const apiUrl = 'http://localhost:3000/api/data';

// Function to fetch data from the API
const fetchData = async () => {
  try {
    const response = await axios.get(apiUrl);
    console.log('API Response:', response.data);
  } catch (error) {
    console.error('Error fetching data:', (error as Error).message);
  }
};

// Call the fetchData function
fetchData();
