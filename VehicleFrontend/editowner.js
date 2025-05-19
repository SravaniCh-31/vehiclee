document.addEventListener("DOMContentLoaded", () => {
    const saveOwnerButton = document.getElementById("saveOwnerButton");
    const deleteOwnerButton = document.getElementById("deleteOwnerButton"); // Delete owner button
    const registrationList = document.getElementById("registrationList");

    let originalData = {}; // To store the original data fetched from the backend

    // Get the owner ID from the query parameters
    const urlParams = new URLSearchParams(window.location.search);
    const ownerId = urlParams.get("id");

    if (!ownerId) {
        alert("No owner ID provided.");
        return;
    }

    // Fetch owner details and registrations
    async function fetchOwnerDetails() {
        try {
            const res = await fetch(`http://localhost:8081/owners/${ownerId}`);
            if (!res.ok) {
                throw new Error("Failed to fetch owner details.");
            }

            const owner = await res.json();

            // Populate owner details
            document.getElementById("ownerId").value = owner.id;
            document.getElementById("name").value = owner.name;
            document.getElementById("address").value = owner.address;
            document.getElementById("phone").value = owner.phone;

            // Display registrations
            displayRegistrations(owner.registrations);
        } catch (error) {
            console.error("Error fetching owner details:", error);
            alert("Failed to fetch owner details.");
        }
    }

    // Function to save owner details
    saveOwnerButton.addEventListener("click", async () => {
        const ownerId = document.getElementById("ownerId").value;
        const name = document.getElementById("name").value;
        const address = document.getElementById("address").value;
        const phone = document.getElementById("phone").value;

        const payload = { id: ownerId, name, address, phone };

        try {
            const response = await fetch(`http://localhost:8081/owners/${ownerId}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                throw new Error("Failed to update owner details.");
            }

            alert("Owner details updated successfully.");
        } catch (error) {
            console.error("Error updating owner details:", error);
            alert("Failed to update owner details.");
        }
    });

    // Function to delete a registration
    async function deleteRegistration(registrationId) {
        const confirmDelete = confirm("Are you sure you want to delete this registration?");
        if (!confirmDelete) return;
    
        try {
            const response = await fetch(`http://localhost:8081/registrations/${registrationId}`, {
                method: "DELETE",
            });
    
            if (!response.ok) {
                throw new Error("Failed to delete registration.");
            }
    
            alert("Registration deleted successfully.");
    
            // Use browser history to go back to the previous page
            window.location.href = `editowner.html?id=${ownerId}`;
        } catch (error) {
            console.error("Error deleting registration:", error);
            alert("Failed to delete registration.");
        }
    }
    

    // Function to display registrations
    function displayRegistrations(registrations) {
        registrationList.innerHTML = ""; // Clear the container

        if (!registrations || registrations.length === 0) {
            registrationList.innerHTML = "<p>No registrations found.</p>";
            return;
        }

        registrations.forEach((registration, index) => {
            const registrationDiv = document.createElement("div");
            registrationDiv.classList.add("registration-item");
            registrationDiv.innerHTML = `
                <h4>Registration ${index + 1}</h4>
                <p><strong>Registration Number:</strong> ${registration.registrationNumber}</p>
                <p><strong>Registration Date:</strong> ${registration.registrationDate.split("T")[0]}</p>
                <h5>Vehicle Details</h5>
                <p><strong>License Number:</strong> ${registration.vehicle.licenseNumber}</p>
                <p><strong>VIN:</strong> ${registration.vehicle.vin}</p>
                <p><strong>Make:</strong> ${registration.vehicle.make}</p>
                <p><strong>Model:</strong> ${registration.vehicle.model}</p>
                <p><strong>Vehicle Name:</strong> ${registration.vehicle.vehicleName}</p>
                <p><strong>Vehicle Type:</strong> ${registration.vehicle.vehicleType}</p>
                <button class="deleteButton" data-id="${registration.id}">Delete Registration</button>
                <hr />
            `;
            registrationList.appendChild(registrationDiv);
        });

        // Attach event listeners for delete buttons AFTER elements are created
        document.querySelectorAll(".deleteButton").forEach(button => {
            button.addEventListener("click", async function () {
                const registrationId = this.getAttribute("data-id");
                await deleteRegistration(registrationId);
            });
        });
    }

    // Fetch owner details on page load
    fetchOwnerDetails();
});
