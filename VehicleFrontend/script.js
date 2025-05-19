document.addEventListener("DOMContentLoaded", () => {
    const ownerForm = document.getElementById("ownerForm");
    const ownerList = document.getElementById("ownerList");
    const searchButton = document.getElementById("searchButton");
    const searchQuery = document.getElementById("searchQuery");

    // Function to fetch and display owners
    async function loadOwners(query = "") {
        try {
            const url = query
                ? `http://localhost:8081/owners/search/${encodeURIComponent(query)}`
                : "http://localhost:8081/owners/getAllOwners";
            const res = await fetch(url);
            if (!res.ok) {
                throw new Error("Failed to fetch owners.");
            }

            const owners = await res.json();
            ownerList.innerHTML = ""; // Clear the table body before adding new data

            if (owners.length === 0) {
                ownerList.innerHTML = "<tr><td colspan='4'>No owners found.</td></tr>";
            } else {
                owners.forEach((owner) => {
                    const registrations = owner.registrations
                        .map(
                            (reg) =>
                                `${reg.registrationNumber} (${reg.vehicle.vehicleName || "Unknown Vehicle"})`
                        )
                        .join(", ");
                    const row = `
                        <tr>
                            <td>${owner.name}</td>
                            <td>${owner.address}</td>
                            <td>${owner.phone}</td>
                            <td>${registrations || "No Registrations"}</td>
                            <td>
                                <button onclick="window.location.href='editowner.html?id=${owner.id}'">Edit</button>
                            </td>
                        </tr>`;
                    ownerList.innerHTML += row;
                });
            }
        } catch (error) {
            alert("Error: " + error.message);
        }
    }

    // Load all owners on page load
    loadOwners();

    // Search functionality
    if (searchButton) {
        searchButton.addEventListener("click", () => {
            const query = searchQuery.value.trim();
            loadOwners(query);
        });
    }

    // Add owner functionality
    if (ownerForm) {
        ownerForm.addEventListener("submit", async (e) => {
            e.preventDefault();

            // Collect owner details
            const name = document.getElementById("name").value;
            const address = document.getElementById("address").value;
            const phone = document.getElementById("phone").value;

            // Collect registration details
            const registrationNumber = document.getElementById("registrationNumber").value;
            const registrationDateInput = document.getElementById("registrationDate").value;

            // Format the registration date to include time (defaulting to 00:00:00 if not provided)
            const registrationDate = `${registrationDateInput}T00:00:00`;

            // Collect vehicle details
            const licenseNumber = document.getElementById("licenseNumber").value;
            const vin = document.getElementById("vin").value;
            const make = document.getElementById("make").value;
            const model = document.getElementById("model").value;
            const vehicleName = document.getElementById("vehicleName").value;
            const vehicleType = document.getElementById("vehicleType").value;

            // Build the payload
            const payload = {
                name,
                address,
                phone,
                registrations: [
                    {
                        registrationNumber,
                        registrationDate,
                        vehicle: {
                            licenseNumber,
                            vin,
                            make,
                            model,
                            vehicleName,
                            vehicleType
                        }
                    }
                ]
            };

            try {
                // Send the payload to the backend
                const response = await fetch("http://localhost:8081/owners/save", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                });

                if (!response.ok) {
                    throw new Error("Failed to add owner.");
                }

                const data = await response.json();
                alert("Owner, registration, and vehicle added successfully.");
                ownerForm.reset();
                loadOwners(); // Reload all owners after adding a new one
            } catch (error) {
                alert("Error: " + error.message);
            }
        });
    }

    const vehicleForm = document.getElementById("vehicleForm");
    const vehicleList = document.getElementById("vehicleList");
    const registrationForm = document.getElementById("registrationForm");
    const registrationList = document.getElementById("registrationList");
  
    if (vehicleForm) {
      vehicleForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        const make = document.getElementById("make").value;
        const model = document.getElementById("model").value;
        const year = document.getElementById("year").value;
  
        const response = await fetch("http://localhost:8081/vehicles", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ make, model, year }),
        });
  
        const data = await response.json();
        alert("Vehicle added: " + data.make + " " + data.model);
        vehicleForm.reset();
        loadVehicles();
      });
  
      async function loadVehicles() {
        const res = await fetch("http://localhost:8081/vehicles");
        const vehicles = await res.json();
        vehicleList.innerHTML = "";
        vehicles.forEach((vehicle) => {
          const li = document.createElement("li");
          li.textContent = `${vehicle.make} ${vehicle.model} - ${vehicle.year}`;
          vehicleList.appendChild(li);
        });
      }
  
      loadVehicles();
    }
  
    if (registrationForm) {
      registrationForm.addEventListener("submit", async (e) => {
        e.preventDefault();
  
        const ownerName = document.getElementById("ownerName").value;
        const registrationNumber = document.getElementById("registrationNumber").value;
        const registrationDate = document.getElementById("registrationDate").value;
        const licenseNumber = document.getElementById("licenseNumber").value;
        const vin = document.getElementById("vin").value;
        const make = document.getElementById("make").value;
        const model = document.getElementById("model").value;
        const vehicleName = document.getElementById("vehicleName").value;
        const vehicleType = document.getElementById("vehicleType").value;
  
        const payload = {
          registrationNumber,
          registrationDate,
          vehicle: {
            licenseNumber,
            vin,
            make,
            model,
            vehicleName,
            vehicleType
          }
        };
  
        try {
          const response = await fetch(`http://localhost:8081/registrations/addRegsitration/${ownerName}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
          });
  
          if (!response.ok) {
            throw new Error("Failed to register vehicle");
          }
  
          const data = await response.json();
          alert("Registration successful: " + data.registrationNumber);
          registrationForm.reset();
          loadRegistrations();
        } catch (error) {
          alert("Error: " + error.message);
        }
      });
  
      async function loadRegistrations() {
        const res = await fetch("http://localhost:8081/registrations/getAllRegistrations");
        const registrations = await res.json();
        registrationList.innerHTML = "";
        registrations.forEach((reg) => {
          const li = document.createElement("li");
          li.textContent = `${reg.registrationNumber} - ${reg.vehicle?.vehicleName || "Unknown Vehicle"} - ${new Date(reg.registrationDate).toLocaleString()}`;
          registrationList.appendChild(li);
        });
      }
  
      loadRegistrations();
    }  
  });