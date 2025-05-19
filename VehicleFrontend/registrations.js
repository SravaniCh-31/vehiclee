document.addEventListener("DOMContentLoaded", () => {
    const registrationForm = document.getElementById("registrationForm");
    const registrationTableBody = document.getElementById("registrationTableBody");
    const searchQueryInput = document.getElementById("searchQuery");
    const searchButton = document.getElementById("searchButton");
    const clearSearchButton = document.getElementById("clearSearchButton");

    if (!registrationForm || !registrationTableBody) {
        console.error("Form or registration table body element not found!");
        return;
    }

    // Function to load registrations from the backend
    function loadRegistrations() {
        fetch("http://localhost:8081/registrations/getAllRegistrations")
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Failed to fetch registrations.");
                }
                return res.json();
            })
            .then((registrations) => {
                // Clear the table body
                registrationTableBody.innerHTML = "";

                // Populate the table with registration data
                registrations.forEach((registration) => {
                    const row = document.createElement("tr");

                    row.innerHTML = `
                        <td>${registration.registrationNumber}</td>
                        <td>${new Date(registration.registrationDate).toLocaleString()}</td>
                        <td>${registration.vehicle.licenseNumber}</td>
                        <td>${registration.vehicle.vin}</td>
                        <td>${registration.vehicle.make}</td>
                        <td>${registration.vehicle.model}</td>
                        <td>${registration.vehicle.vehicleName}</td>
                        <td>${registration.vehicle.vehicleType}</td>
                        <td>
                            <button onclick="window.location.href='editregistration.html?id=${registration.id}'">Edit</button>
                        </td>
                    `;

                    registrationTableBody.appendChild(row);
                });
            })
            .catch((error) => {
                console.error("Error loading registrations:", error);
                alert("Failed to load registrations.");
            });
    }

    // Function to search for a registration by registration number
    function searchRegistration(query) {
        fetch(`http://localhost:8081/registrations/search?query=${encodeURIComponent(query)}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Failed to search for registration.");
                }
                return res.json();
            })
            .then((registrations) => {
                // Check if the response is an object and convert it to an array
                if (!Array.isArray(registrations)) {
                    registrations = [registrations];
                }

                // Display the registrations
                displayRegistrations(registrations);
            })
            .catch((error) => {
                console.error("Error searching for registration:", error);
                alert("Failed to search for registration.");
            });
    }

    // Handle search button click
    searchButton.addEventListener("click", () => {
        const query = searchQueryInput.value.trim();
        if (query) {
            searchRegistration(query);
        } else {
            alert("Please enter a registration number to search.");
        }
    });

    // Handle clear search button click
    clearSearchButton.addEventListener("click", () => {
        searchQueryInput.value = "";
        loadRegistrations(); // Reload all registrations
    });

    // Handle form submission
    registrationForm.addEventListener("submit", (e) => {
        e.preventDefault();

        // Collect form data
        const ownerName = document.getElementById("ownerName").value;
        const registrationNumber = document.getElementById("registrationNumber").value;
        const registrationDate = document.getElementById("registrationDate").value;
        const licenseNumber = document.getElementById("licenseNumber").value;
        const vin = document.getElementById("vin").value;
        const make = document.getElementById("make").value;
        const model = document.getElementById("model").value;
        const vehicleName = document.getElementById("vehicleName").value;
        const vehicleType = document.getElementById("vehicleType").value;

        // Build the payload
        const payload = {
            registrationNumber,
            registrationDate,
            vehicle: {
                licenseNumber,
                vin,
                make,
                model,
                vehicleName,
                vehicleType,
            },
        };

        // Send the data to the backend
        fetch(`http://localhost:8081/registrations/addRegsitration/${encodeURIComponent(ownerName)}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Failed to add registration.");
                }
                return res.json();
            })
            .then((data) => {
                alert("Registration added successfully!");

                // Reload the registrations table
                loadRegistrations();

                // Clear the form
                registrationForm.reset();
            })
            .catch((error) => {
                alert("Error: " + error.message);
            });
    });

    function displayRegistrations(registrations) {
        registrationTableBody.innerHTML = ""; // Clear the table
    
        if (!registrations || registrations.length === 0) {
            registrationTableBody.innerHTML = "<tr><td colspan='8'>No registrations found.</td></tr>";
            return;
        }
    
        registrations.forEach((registration) => {
            const row = document.createElement("tr");
    
            row.innerHTML = `
                <td>${registration.registrationNumber}</td>
                <td>${new Date(registration.registrationDate).toLocaleString()}</td>
                <td>${registration.vehicle.licenseNumber}</td>
                <td>${registration.vehicle.vin}</td>
                <td>${registration.vehicle.make}</td>
                <td>${registration.vehicle.model}</td>
                <td>${registration.vehicle.vehicleName}</td>
                <td>${registration.vehicle.vehicleType}</td>
                <td>
                    <button onclick="window.location.href='editregistration.html?id=${registration.id}'">Edit</button>
                </td>
            `;
    
            registrationTableBody.appendChild(row);
        });
    }
    

    // Load registrations on page load
    loadRegistrations();
});