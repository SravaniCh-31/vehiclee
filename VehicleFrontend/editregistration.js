document.addEventListener("DOMContentLoaded", () => {
    const editRegistrationForm = document.getElementById("editRegistrationForm");
    const registrationIdInput = document.getElementById("registrationId");
    const vehicleIdInput = document.getElementById("vehicleId");
    const ownerNameInput = document.getElementById("ownerName");

    let originalData = {}; // To store the original data fetched from the backend

    // Get the registration ID from the query parameters
    const urlParams = new URLSearchParams(window.location.search);
    const registrationId = urlParams.get("id");

    if (!registrationId) {
        alert("No registration ID provided.");
        return;
    }

    // Fetch the registration data by ID
    function fetchRegistrationById() {
        fetch(`http://localhost:8081/registrations/${registrationId}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Failed to fetch registration.");
                }
                return res.json();
            })
            .then((registration) => {
                // Populate the form with the fetched registration data
                registrationIdInput.value = registration.id;
                vehicleIdInput.value = registration.vehicle.id; // Store vehicle ID
                document.getElementById("registrationNumber").value = registration.registrationNumber;
                document.getElementById("registrationDate").value = registration.registrationDate.split("T")[0] + "T" + registration.registrationDate.split("T")[1];
                document.getElementById("licenseNumber").value = registration.vehicle.licenseNumber;
                document.getElementById("vin").value = registration.vehicle.vin;
                document.getElementById("make").value = registration.vehicle.make;
                document.getElementById("model").value = registration.vehicle.model;
                document.getElementById("vehicleName").value = registration.vehicle.vehicleName;
                document.getElementById("vehicleType").value = registration.vehicle.vehicleType;

                // Fetch and populate the owner's name using the registration number
                fetchOwnerByRegistrationNumber(registration.registrationNumber);

                // Store the original data for comparison
                originalData = { ...registration };
            })
            .catch((error) => {
                console.error("Error fetching registration:", error);
                alert("Failed to fetch registration.");
            });
    }

    // Fetch the owner's name using the registration number
    function fetchOwnerByRegistrationNumber(registrationNumber) {
        fetch(`http://localhost:8081/registrations/searchOwner/${registrationNumber}`)
            .then((res) => {
                if (!res.ok) {
                    throw new Error("Failed to fetch owner.");
                }
                return res.text(); // Use .text() since the response is plain text (e.g., "John Doe")
            })
            .then((ownerName) => {
                // Populate the owner name field
                ownerNameInput.value = ownerName;
            })
            .catch((error) => {
                console.error("Error fetching owner:", error);
                alert("Failed to fetch owner.");
            });
    }

    // Handle form submission for updating registration and owner name
    editRegistrationForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const registrationNumber = document.getElementById("registrationNumber").value;
        const newOwnerName = document.getElementById("ownerName").value;

        if (!registrationNumber || !newOwnerName) {
            alert("Please provide both registration number and new owner name.");
            return;
        }

        try {
            // Send update request for owner name
            const ownerUpdateResponse = await fetch(`http://localhost:8081/registrations/updateOwner/${registrationNumber}/${newOwnerName}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
            });

            if (!ownerUpdateResponse.ok) {
                throw new Error("Failed to update owner name.");
            }

            alert("Owner name updated successfully!");
        } catch (error) {
            console.error("Error updating owner name check weather it exists or not:", error);
            alert("Failed to update owner name check weather it exists or not.");
        }

        // Proceed with updating registration details
        const registrationPayload = {};
        if (document.getElementById("registrationDate").value !== originalData.registrationDate) {
            registrationPayload.registrationDate = document.getElementById("registrationDate").value;
        }

        if (Object.keys(registrationPayload).length > 0) {
            try {
                const response = await fetch(`http://localhost:8081/registrations/patch/${registrationId}`, {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(registrationPayload),
                });

                if (!response.ok) {
                    throw new Error("Failed to update registration.");
                }

                alert("Registration updated successfully!");
                window.location.href = "registrations.html"; // Redirect after update
            } catch (error) {
                console.error("Error updating registration:", error);
                alert("Failed to update registration.");
            }
        }
    });

    // Fetch registration details on page load
    fetchRegistrationById();
});
