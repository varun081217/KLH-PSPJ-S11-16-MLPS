import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Enum to define vehicle types
enum VehicleType {
    CAR, BIKE
}

// Vehicle class to store vehicle details
class Vehicle {
    private String licensePlate;
    private VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
}

// ParkingSpot class to manage individual spots
class ParkingSpot {
    private String spotId;
    private VehicleType supportedType;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, VehicleType supportedType) {
        this.spotId = spotId;
        this.supportedType = supportedType;
        this.parkedVehicle = null;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean canFitVehicle(Vehicle vehicle) {
        return isAvailable() && vehicle.getType() == supportedType;
    }

    public void park(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
    }

    public void unpark() {
        this.parkedVehicle = null;
    }

    public String getSpotId() { return spotId; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }
}

// ParkingLot class to manage a collection of spots
class ParkingLot {
    private String lotName;
    private List<ParkingSpot> spots;

    public ParkingLot(String lotName, int carCapacity, int bikeCapacity) {
        this.lotName = lotName;
        this.spots = new ArrayList<>();
        
        // Initialize Bike Spots
        for (int i = 1; i <= bikeCapacity; i++) {
            spots.add(new ParkingSpot(lotName + "-B" + i, VehicleType.BIKE));
        }
        // Initialize Car Spots
        for (int i = 1; i <= carCapacity; i++) {
            spots.add(new ParkingSpot(lotName + "-C" + i, VehicleType.CAR));
        }
    }

    public String getLotName() { return lotName; }

    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null; // No spot available in this lot
    }

    public void displayStatus() {
        System.out.println("\n--- Status for " + lotName + " ---");
        int availableCars = 0, availableBikes = 0;
        
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) {
                if (spot.getSpotId().contains("-C")) availableCars++;
                else availableBikes++;
            }
        }
        System.out.println("Available Car Spots: " + availableCars);
        System.out.println("Available Bike Spots: " + availableBikes);
    }
}

// Main System Class
public class SmartParkingSystem {
    private List<ParkingLot> lots;

    public SmartParkingSystem() {
        lots = new ArrayList<>();
    }

    public void addLot(ParkingLot lot) {
        lots.add(lot);
    }

    public void parkVehicle(Vehicle vehicle) {
        for (ParkingLot lot : lots) {
            ParkingSpot spot = lot.findAvailableSpot(vehicle);
            if (spot != null) {
                spot.park(vehicle);
                System.out.println("Success! " + vehicle.getType() + " [" + vehicle.getLicensePlate() + 
                                   "] parked at spot: " + spot.getSpotId() + " in " + lot.getLotName());
                return;
            }
        }
        System.out.println("Sorry, no available spots for " + vehicle.getType() + " across all lots.");
    }

    public void removeVehicle(String licensePlate) {
        for (ParkingLot lot : lots) {
            // Accessing internal spots for demonstration (could be further encapsulated)
            for (java.lang.reflect.Field field : lot.getClass().getDeclaredFields()) {
                if (field.getName().equals("spots")) {
                    try {
                        field.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        List<ParkingSpot> spots = (List<ParkingSpot>) field.get(lot);
                        for (ParkingSpot spot : spots) {
                            if (!spot.isAvailable() && spot.getParkedVehicle().getLicensePlate().equalsIgnoreCase(licensePlate)) {
                                spot.unpark();
                                System.out.println("Vehicle [" + licensePlate + "] has been unparked from " + spot.getSpotId());
                                return;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Vehicle [" + licensePlate + "] not found in any lot.");
    }

    public void showAllStatus() {
        for (ParkingLot lot : lots) {
            lot.displayStatus();
        }
    }

    public static void main(String[] args) {
        SmartParkingSystem system = new SmartParkingSystem();
        Scanner scanner = new Scanner(System.in);

        // Setup lots
        system.addLot(new ParkingLot("North Wing", 2, 2)); // Small capacity for testing
        system.addLot(new ParkingLot("South Wing", 3, 5));

        boolean running = true;
        System.out.println("=== Welcome to the Smart Multi-Lot Parking System ===");

        while (running) {
            System.out.println("\n1. Park Vehicle");
            System.out.println("2. Unpark Vehicle");
            System.out.println("3. View Parking Status");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter License Plate: ");
                    String plate = scanner.nextLine();
                    System.out.print("Enter Vehicle Type (CAR/BIKE): ");
                    String typeStr = scanner.nextLine().toUpperCase();
                    
                    try {
                        VehicleType type = VehicleType.valueOf(typeStr);
                        system.parkVehicle(new Vehicle(plate, type));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid vehicle type! Please enter CAR or BIKE.");
                    }
                    break;
                case 2:
                    System.out.print("Enter License Plate to unpark: ");
                    String unparkPlate = scanner.nextLine();
                    system.removeVehicle(unparkPlate);
                    break;
                case 3:
                    system.showAllStatus();
                    break;
                case 4:
                    running = false;
                    System.out.println("Shutting down system...");
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
        scanner.close();
    }
}
