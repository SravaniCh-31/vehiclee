document.addEventListener("DOMContentLoaded", () => {
    const vehicleForm = document.getElementById("vehicleForm");
    const vehicleList = document.getElementById("vehicleList");

    if (!vehicleForm || !vehicleList) {
        console.error("Form or vehicle list element not found!");
        return;
    }

    // Function to fetch vehicle data from backend
    async function loadVehicles() {
        try {
            const response = await fetch("http://localhost:8081/vehicles");

            if (!response.ok) {
                throw new Error("Failed to fetch vehicle data.");
            }

            const vehicles = await response.json(); // Parse JSON response

            // Clear the vehicle list before adding new data
            vehicleList.innerHTML = "";

            // Populate the vehicle list dynamically
            vehicles.forEach((vehicle) => {
                const listItem = document.createElement("li");
                listItem.innerHTML = `
                    <strong>${vehicle.make} ${vehicle.model}</strong> (${vehicle.vehicleType})<br>
                    <small>License: ${vehicle.licenseNumber} | VIN: ${vehicle.vin}</small>
                `;
                vehicleList.appendChild(listItem);
            });
        } catch (error) {
            console.error("Error fetching vehicles:", error);
            alert("Failed to load vehicle data.");
        }
    }

    // Handle form submission (Adding new vehicle manually)
    vehicleForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const make = document.getElementById("make").value;
        const model = document.getElementById("model").value;
        const year = document.getElementById("year").value;

        const newVehicle = { make, model, vehicleType: year }; // Adjusted based on structure

        try {
            const response = await fetch("http://localhost:8081/vehicles", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(newVehicle),
            });

            if (!response.ok) {
                throw new Error("Failed to add vehicle.");
            }

            alert("Vehicle added successfully!");
            loadVehicles(); // Reload vehicle list with updated data
            vehicleForm.reset();
        } catch (error) {
            console.error("Error adding vehicle:", error);
            alert("Failed to add vehicle.");
        }
    });

    // Load vehicles on page load
    loadVehicles();
});
