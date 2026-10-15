import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// ============================================================================
// CO4: CUSTOM EXCEPTION HIERARCHY FOR ROBUST ERROR HANDLING
// ============================================================================
class ParkingException extends Exception {
    public ParkingException(String message) {
        super(message);
    }
}

class ParkingFullException extends ParkingException {
    public ParkingFullException(String message) {
        super(message);
    }
}

class VehicleNotFoundException extends ParkingException {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

// ============================================================================
// CO6: FUNCTIONAL INTERFACE (Custom Functional Interface)
// ============================================================================
@FunctionalInterface
interface SpotFilter {
    boolean matches(ParkingSpot spot);
}

// ============================================================================
// ENUM: VEHICLE TYPE CLASSIFICATION
// ============================================================================
enum VehicleType {
    CAR(20.0), BIKE(10.0);

    private final double hourlyRate; // CO1: Primitive data type double

    VehicleType(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}

// ============================================================================
// CO4: VEHICLE CLASS (Implements Comparable for CO5: Sorting & Comparison)
// ============================================================================
class Vehicle implements Comparable<Vehicle> {
    // Encapsulation: Private fields
    private final String licensePlate;
    private final VehicleType type;
    private final String entryTime;

    public Vehicle(String licensePlate, VehicleType type) {
        // CO5: String processing
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.type = type;
        this.entryTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Vehicle(String licensePlate, VehicleType type, String entryTime) {
        this.licensePlate = licensePlate.trim().toUpperCase();
        this.type = type;
        this.entryTime = entryTime;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
    public String getEntryTime() { return entryTime; }

    // CO5: Sorting and comparison (Natural ordering by License Plate)
    @Override
    public int compareTo(Vehicle other) {
        return this.licensePlate.compareTo(other.licensePlate);
    }

    @Override
    public String toString() {
        return String.format("[%s | %-4s | Entered: %s]", licensePlate, type, entryTime);
    }
}

// ============================================================================
// CO4: PARKING SPOT CLASS (Encapsulation and State Management)
// ============================================================================
class ParkingSpot {
    private final String spotId;
    private final VehicleType supportedType;
    private final int row; // CO3: Grid coordinates for 2D layout
    private final int col;
    private Vehicle parkedVehicle; // CO4: Object relationship (has-a)

    public ParkingSpot(String spotId, VehicleType supportedType, int row, int col) {
        this.spotId = spotId;
        this.supportedType = supportedType;
        this.row = row;
        this.col = col;
        this.parkedVehicle = null;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean canFitVehicle(Vehicle vehicle) {
        return isAvailable() && vehicle.getType() == this.supportedType;
    }

    public void park(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
    }

    public void unpark() {
        this.parkedVehicle = null;
    }

    public String getSpotId() { return spotId; }
    public VehicleType getSupportedType() { return supportedType; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }
    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public String toString() {
        if (isAvailable()) {
            return String.format("%s (%s): [EMPTY]", spotId, supportedType);
        } else {
            return String.format("%s (%s): [OCCUPIED by %s]", spotId, supportedType, parkedVehicle.getLicensePlate());
        }
    }
}

// ============================================================================
// CO4: PARKING LOT CLASS (Includes 1-D & 2-D Arrays, Recursion, Encapsulation)
// ============================================================================
class ParkingLot {
    private final String lotName;
    private final int rows; // CO1: Primitives
    private final int cols;

    // CO3: 2-D Array representing the physical layout grid (rows x bays)
    private final ParkingSpot[][] spotGrid;

    // CO3: 1-D Array representing flat array of spots
    private final ParkingSpot[] spotArray;

    // CO6: Collections framework (List of spots for Collections/Streams)
    private final List<ParkingSpot> spotList;

    public ParkingLot(String lotName, int bikeRows, int carRows, int spotsPerRow) {
        this.lotName = lotName;
        this.rows = bikeRows + carRows;
        this.cols = spotsPerRow;

        // Initialize 2-D Array
        this.spotGrid = new ParkingSpot[rows][cols];

        // Initialize 1-D Array
        this.spotArray = new ParkingSpot[rows * cols];

        // Initialize Collection List
        this.spotList = new ArrayList<>();

        int flatIndex = 0;

        // Populate Bike Rows first
        for (int r = 0; r < bikeRows; r++) {
            for (int c = 0; c < spotsPerRow; c++) {
                String spotId = String.format("%s-B%d-%d", lotName, (r + 1), (c + 1));
                ParkingSpot spot = new ParkingSpot(spotId, VehicleType.BIKE, r, c);
                spotGrid[r][c] = spot;
                spotArray[flatIndex++] = spot;
                spotList.add(spot);
            }
        }

        // Populate Car Rows next
        for (int r = bikeRows; r < rows; r++) {
            for (int c = 0; c < spotsPerRow; c++) {
                String spotId = String.format("%s-C%d-%d", lotName, (r - bikeRows + 1), (c + 1));
                ParkingSpot spot = new ParkingSpot(spotId, VehicleType.CAR, r, c);
                spotGrid[r][c] = spot;
                spotArray[flatIndex++] = spot;
                spotList.add(spot);
            }
        }
    }

    public String getLotName() { return lotName; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public List<ParkingSpot> getSpotList() { return spotList; }
    public ParkingSpot[][] getSpotGrid() { return spotGrid; }
    public ParkingSpot[] getSpotArray() { return spotArray; }

    // CO5: Searching for an available spot matching vehicle type (Linear search)
    public ParkingSpot findAvailableSpot(Vehicle vehicle) {
        for (ParkingSpot spot : spotList) {
            if (spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        return null; // None available
    }

    // CO4: Encapsulated removal method (No reflection needed!)
    public boolean removeVehicle(String licensePlate) {
        for (ParkingSpot spot : spotList) {
            if (!spot.isAvailable() && spot.getParkedVehicle().getLicensePlate().equalsIgnoreCase(licensePlate)) {
                spot.unpark();
                return true;
            }
        }
        return false;
    }

    // Search for where a vehicle is parked
    public ParkingSpot findSpotByVehicle(String licensePlate) {
        for (ParkingSpot spot : spotList) {
            if (!spot.isAvailable() && spot.getParkedVehicle().getLicensePlate().equalsIgnoreCase(licensePlate)) {
                return spot;
            }
        }
        return null;
    }

    // ========================================================================
    // CO3: RECURSION - Recursive calculation of available spots on 2-D Grid
    // ========================================================================
    /**
     * Recursively traverses the 2-D parking grid from (row, col) to the end.
     * Base Case: When row index reaches the total number of rows.
     * Recursive Step: Count current cell (1 if available, 0 if occupied) + count remainder.
     */
    public int countAvailableSpotsRecursively(int row, int col) {
        // Base case: beyond last row
        if (row >= rows) {
            return 0;
        }

        // Advance indices: next column, or next row if end of current row
        int nextRow = (col + 1 >= cols) ? row + 1 : row;
        int nextCol = (col + 1 >= cols) ? 0 : col + 1;

        int currentAvailable = (spotGrid[row][col] != null && spotGrid[row][col].isAvailable()) ? 1 : 0;
        return currentAvailable + countAvailableSpotsRecursively(nextRow, nextCol);
    }

    // Helper wrapper for the recursive method
    public int countAvailableSpotsRecursively() {
        return countAvailableSpotsRecursively(0, 0);
    }

    // ========================================================================
    // CO6: STREAMS & LAMBDAS - Counting available spots using Streams API
    // ========================================================================
    public long countAvailableByStream(VehicleType type) {
        return spotList.stream()
                .filter(ParkingSpot::isAvailable)
                .filter(s -> s.getSupportedType() == type)
                .count();
    }

    public long countOccupiedByStream(VehicleType type) {
        return spotList.stream()
                .filter(s -> !s.isAvailable())
                .filter(s -> s.getSupportedType() == type)
                .count();
    }

    // CO6: Functional Interface & Lambda filtering
    public List<ParkingSpot> filterSpots(SpotFilter filter) {
        List<ParkingSpot> result = new ArrayList<>();
        for (ParkingSpot spot : spotList) {
            if (filter.matches(spot)) { // Invoking functional interface
                result.add(spot);
            }
        }
        return result;
    }

    // ========================================================================
    // CO5: STRINGBUILDER - Generate Visual 2-D Grid Layout & Report
    // ========================================================================
    public String getVisualGridLayout() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Visual Layout Grid: %s ===%n", lotName));

        for (int r = 0; r < rows; r++) {
            sb.append(String.format("Row %2d: ", (r + 1)));
            for (int c = 0; c < cols; c++) {
                ParkingSpot spot = spotGrid[r][c];
                if (spot.isAvailable()) {
                    sb.append(String.format("[ %-7s: FREE ] ", spot.getSpotId()));
                } else {
                    sb.append(String.format("[ %-7s: OCCU ] ", spot.getSpotId()));
                }
            }
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

    public void displayStatus() {
        long availCars = countAvailableByStream(VehicleType.CAR);
        long availBikes = countAvailableByStream(VehicleType.BIKE);
        long occCars = countOccupiedByStream(VehicleType.CAR);
        long occBikes = countOccupiedByStream(VehicleType.BIKE);
        int totalAvailRecursive = countAvailableSpotsRecursively();

        System.out.printf("%n--- Status for Lot: %-12s ---%n", lotName);
        System.out.printf("  Cars  -> Available: %d | Occupied: %d%n", availCars, occCars);
        System.out.printf("  Bikes -> Available: %d | Occupied: %d%n", availBikes, occBikes);
        System.out.printf("  Total Available (Verified via CO3 Recursion): %d%n", totalAvailRecursive);
    }
}

// ============================================================================
// MAIN SYSTEM CLASS: SmartParkingSystem
// ============================================================================
public class SmartParkingSystem {
    // CO6: Collections (List of lots)
    private final List<ParkingLot> lots;

    // CO3: 1-D Array for recent transaction history buffer
    private final String[] recentActivityLog;
    private int logIndex;
    private static final int MAX_LOG_SIZE = 10;

    public SmartParkingSystem() {
        this.lots = new ArrayList<>();
        this.recentActivityLog = new String[MAX_LOG_SIZE];
        this.logIndex = 0;
    }

    public void addLot(ParkingLot lot) {
        lots.add(lot);
    }

    // CO3: 1-D Array logging helper
    private void recordActivity(String activity) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        recentActivityLog[logIndex % MAX_LOG_SIZE] = String.format("[%s] %s", timestamp, activity);
        logIndex++;
    }

    // ========================================================================
    // FEATURE 1-4 & 8: VEHICLE ENTRY MANAGEMENT & MULTI-LOT COORDINATION
    // ========================================================================
    public void parkVehicle(Vehicle vehicle) throws ParkingFullException {
        // CO6: Stream API to check if vehicle is already parked anywhere
        boolean alreadyParked = lots.stream()
                .flatMap(lot -> lot.getSpotList().stream())
                .anyMatch(spot -> !spot.isAvailable() &&
                        spot.getParkedVehicle().getLicensePlate().equalsIgnoreCase(vehicle.getLicensePlate()));

        if (alreadyParked) {
            System.out.println("Warning: Vehicle with plate [" + vehicle.getLicensePlate() + "] is ALREADY parked in the system!");
            return;
        }

        // Search across lots to allocate available spot
        for (ParkingLot lot : lots) {
            ParkingSpot spot = lot.findAvailableSpot(vehicle);
            if (spot != null) {
                spot.park(vehicle);
                recordActivity("PARKED: " + vehicle.getLicensePlate() + " at " + spot.getSpotId());
                System.out.println("\n>>> SUCCESSFUL ALLOCATION <<<");
                System.out.println("Vehicle Type  : " + vehicle.getType());
                System.out.println("License Plate : " + vehicle.getLicensePlate());
                System.out.println("Allocated Lot : " + lot.getLotName());
                System.out.println("Assigned Spot : " + spot.getSpotId());
                System.out.println("Entry Time    : " + vehicle.getEntryTime());
                return;
            }
        }

        // CO4: Exception Handling if all lots are full
        throw new ParkingFullException("Allocation Failed: No available spots for " + vehicle.getType() + " in any parking lot.");
    }

    // ========================================================================
    // FEATURE 5: VEHICLE EXIT MANAGEMENT (ENCAPSULATED, NO REFLECTION)
    // ========================================================================
    public void removeVehicle(String licensePlate) throws VehicleNotFoundException {
        for (ParkingLot lot : lots) {
            ParkingSpot spot = lot.findSpotByVehicle(licensePlate);
            if (spot != null) {
                Vehicle v = spot.getParkedVehicle();
                double fee = v.getType().getHourlyRate(); // Base fee calculation
                lot.removeVehicle(licensePlate); // CO4: Clean encapsulated call!
                recordActivity("EXITED: " + licensePlate + " from " + spot.getSpotId());

                System.out.println("\n>>> VEHICLE EXIT RECEIPT <<<");
                System.out.println("License Plate : " + v.getLicensePlate());
                System.out.println("Vehicle Type  : " + v.getType());
                System.out.println("Released Spot : " + spot.getSpotId() + " (" + lot.getLotName() + ")");
                System.out.printf("Standard Rate : $%.2f/hr%n", fee);
                System.out.println("Status        : Spot released and marked AVAILABLE.");
                return;
            }
        }

        // CO4: Custom exception thrown if vehicle was not found
        throw new VehicleNotFoundException("Error: Vehicle with plate [" + licensePlate + "] not found in any lot.");
    }

    // ========================================================================
    // FEATURE 6: REAL-TIME STATUS & OVERVIEW
    // ========================================================================
    public void showAllStatus() {
        System.out.println("\n================ REAL-TIME PARKING STATUS ================");
        for (ParkingLot lot : lots) {
            lot.displayStatus();
        }
    }

    public void displayVisualGrids() {
        System.out.println("\n================ PHYSICAL LOT LAYOUT MAPS ================");
        for (ParkingLot lot : lots) {
            System.out.print(lot.getVisualGridLayout());
        }
    }

    // ========================================================================
    // CO5: SEARCHING (Search vehicle by plate)
    // ========================================================================
    public void searchVehicle(String licensePlate) {
        System.out.println("\nSearching for vehicle: " + licensePlate + "...");
        for (ParkingLot lot : lots) {
            ParkingSpot spot = lot.findSpotByVehicle(licensePlate);
            if (spot != null) {
                System.out.println("FOUND! Vehicle " + spot.getParkedVehicle() + " is parked at Spot: " +
                        spot.getSpotId() + " in " + lot.getLotName());
                return;
            }
        }
        System.out.println("Vehicle [" + licensePlate + "] is NOT parked in any lot.");
    }

    // ========================================================================
    // CO5: SORTING AND COMPARISON & CO6: STREAMS
    // ========================================================================
    public void displaySortedParkedVehicles() {
        System.out.println("\n================ PARKED VEHICLES (SORTED ALPHABETICALLY) ================");

        // CO6: Stream API + CO5: Sorting using natural ordering (Comparable)
        List<Vehicle> sortedVehicles = lots.stream()
                .flatMap(lot -> lot.getSpotList().stream())
                .filter(spot -> !spot.isAvailable())
                .map(ParkingSpot::getParkedVehicle)
                .sorted() // Uses Vehicle's compareTo()
                .collect(Collectors.toList());

        if (sortedVehicles.isEmpty()) {
            System.out.println("No vehicles currently parked.");
            return;
        }

        int count = 1;
        for (Vehicle v : sortedVehicles) {
            System.out.println((count++) + ". " + v);
        }

        // CO6: Demonstration of Consumer functional interface with lambda
        System.out.println("\nVehicle Type Breakdown (via Streams):");
        long carCount = sortedVehicles.stream().filter(v -> v.getType() == VehicleType.CAR).count();
        long bikeCount = sortedVehicles.stream().filter(v -> v.getType() == VehicleType.BIKE).count();
        System.out.println("  Total Cars Parked  : " + carCount);
        System.out.println("  Total Bikes Parked : " + bikeCount);
    }

    // ========================================================================
    // CO5: STRINGBUILDER - GENERATE DIGITAL AUDIT REPORT
    // ========================================================================
    public String generateDetailedReport() {
        StringBuilder report = new StringBuilder();
        String divider = "------------------------------------------------------------" + System.lineSeparator();

        report.append(divider);
        report.append("          SMART MULTI-LOT PARKING AUDIT REPORT              ").append(System.lineSeparator());
        report.append("  Generated At: ")
              .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
              .append(System.lineSeparator());
        report.append(divider);

        int grandTotalSpots = 0;
        int grandAvailableSpots = 0;

        for (ParkingLot lot : lots) {
            int lotTotal = lot.getRows() * lot.getCols();
            // Using CO3 recursive count
            int lotAvailable = lot.countAvailableSpotsRecursively();
            int lotOccupied = lotTotal - lotAvailable;

            grandTotalSpots += lotTotal;
            grandAvailableSpots += lotAvailable;

            report.append(String.format("Lot Name: %-15s | Total: %2d | Available: %2d | Occupied: %2d%n",
                    lot.getLotName(), lotTotal, lotAvailable, lotOccupied));
        }

        report.append(divider);
        report.append(String.format("System Totals: Capacity: %d | Available: %d | Occupied: %d%n",
                grandTotalSpots, grandAvailableSpots, (grandTotalSpots - grandAvailableSpots)));
        report.append(divider);

        report.append("Recent Activity Logs (CO3 1-D Array Buffer):").append(System.lineSeparator());
        int logsFound = 0;
        for (String log : recentActivityLog) {
            if (log != null) {
                report.append("  * ").append(log).append(System.lineSeparator());
                logsFound++;
            }
        }
        if (logsFound == 0) {
            report.append("  (No recent activity recorded)").append(System.lineSeparator());
        }
        report.append(divider);

        return report.toString();
    }

    // ========================================================================
    // CO5: FILE HANDLING - PERSISTENCE (FileWriter & FileReader)
    // ========================================================================
    public void saveRecordsToFile(String filename) {
        System.out.println("Saving digital parking records to file: " + filename + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write structured data: LOT,SPOT_ID,TYPE,STATUS,PLATE,ENTRY_TIME
            for (ParkingLot lot : lots) {
                for (ParkingSpot spot : lot.getSpotList()) {
                    if (spot.isAvailable()) {
                        writer.printf("%s,%s,%s,EMPTY,NONE,NONE%n",
                                lot.getLotName(), spot.getSpotId(), spot.getSupportedType());
                    } else {
                        Vehicle v = spot.getParkedVehicle();
                        writer.printf("%s,%s,%s,OCCUPIED,%s,%s%n",
                                lot.getLotName(), spot.getSpotId(), spot.getSupportedType(),
                                v.getLicensePlate(), v.getEntryTime());
                    }
                }
            }
            System.out.println("Data successfully saved to " + filename);
        } catch (IOException e) {
            System.err.println("File Save Error: " + e.getMessage());
        }
    }

    public void loadRecordsFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Notice: File [" + filename + "] does not exist yet. Please save data first.");
            return;
        }

        System.out.println("Loading digital parking records from: " + filename + "...");
        int restoredCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Line format: LOT,SPOT_ID,TYPE,STATUS,PLATE,ENTRY_TIME
                String[] parts = line.split(","); // CO5 String processing & 1-D array
                if (parts.length >= 6 && parts[3].equalsIgnoreCase("OCCUPIED")) {
                    String lotName = parts[0];
                    String spotId = parts[1];
                    VehicleType vType = VehicleType.valueOf(parts[2]);
                    String plate = parts[4];
                    String entryTime = parts[5];

                    // Find matching lot and spot
                    for (ParkingLot lot : lots) {
                        if (lot.getLotName().equalsIgnoreCase(lotName)) {
                            for (ParkingSpot spot : lot.getSpotList()) {
                                if (spot.getSpotId().equalsIgnoreCase(spotId) && spot.isAvailable()) {
                                    Vehicle v = new Vehicle(plate, vType, entryTime);
                                    spot.park(v);
                                    restoredCount++;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Records loaded successfully! Restored " + restoredCount + " vehicle(s).");
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("File Load Error: " + e.getMessage());
        }
    }

    // ========================================================================
    // CO3: RECURSION DEMONSTRATION ACROSS MULTI-LOTS
    // ========================================================================
    public void runRecursiveSpotAudit() {
        System.out.println("\n=== CO3 Recursive Available Spot Audit ===");
        int totalAvailableRecursive = 0;
        for (ParkingLot lot : lots) {
            int lotAvail = lot.countAvailableSpotsRecursively();
            System.out.println("Lot [" + lot.getLotName() + "] Available Spots (Calculated Recursively): " + lotAvail);
            totalAvailableRecursive += lotAvail;
        }
        System.out.println("Total System-wide Available Spots: " + totalAvailableRecursive);
    }

    // ========================================================================
    // CO1 & CO2: CONSOLE-BASED MENU INTERFACE (Scanner, Switch-Case, Loops)
    // ========================================================================
    public static void main(String[] args) {
        SmartParkingSystem system = new SmartParkingSystem();
        Scanner scanner = new Scanner(System.in);
        String dataFilename = "parking_records.txt";

        // Setup parking lots with 2D layouts (Bike rows, Car rows, Spots per row)
        // North Wing: 1 bike row x 2 spots = 2 bike spots; 1 car row x 2 spots = 2 car spots (Total: 4)
        system.addLot(new ParkingLot("North Wing", 1, 1, 2));

        // South Wing: 1 bike row x 3 spots = 3 bike spots; 2 car rows x 3 spots = 6 car spots (Total: 9)
        system.addLot(new ParkingLot("South Wing", 1, 2, 3));

        boolean running = true;
        System.out.println("==========================================================");
        System.out.println("   WELCOME TO SMART MULTI-LOT PARKING MANAGEMENT SYSTEM   ");
        System.out.println("==========================================================");

        while (running) {
            System.out.println("\n----------------- MAIN MENU -----------------");
            System.out.println(" 1. Park Vehicle (Entry)");
            System.out.println(" 2. Unpark Vehicle (Exit)");
            System.out.println(" 3. View Real-Time Status");
            System.out.println(" 4. View 2-D Parking Grid Layout");
            System.out.println(" 5. Search Vehicle by License Plate");
            System.out.println(" 6. View Parked Vehicles (Sorted Alphabetically) [Admin]");
            System.out.println(" 7. Run Recursive Available Spot Audit (CO3) [Admin]");
            System.out.println(" 8. Generate & Display Detailed Report (StringBuilder) [Admin]");
            System.out.println(" 9. Save Parking Records to File (CO5) [Admin]");
            System.out.println("10. Load Parking Records from File (CO5) [Admin]");
            System.out.println("11. Exit System");
            System.out.print("Choose an option (1-11): ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a numerical menu option.");
                scanner.nextLine(); // Clear buffer
                continue;
            }

            if (choice >= 6 && choice <= 10) {
                System.out.print("Enter admin passcode: ");
                String passcode = scanner.nextLine().trim();
                if (!"2008".equals(passcode)) {
                    System.out.println("Access denied! Admin authorization is required.");
                    continue;
                }
                System.out.println("Admin access granted.");
            }

            switch (choice) {
                case 1: // Vehicle Entry
                    System.out.print("Enter License Plate (e.g. MH-12-AB-1234): ");
                    String plate = scanner.nextLine().trim();
                    if (plate.isEmpty()) {
                        System.out.println("License plate cannot be blank!");
                        break;
                    }

                    System.out.print("Enter Vehicle Type (CAR / BIKE): ");
                    String typeStr = scanner.nextLine().trim().toUpperCase();

                    try {
                        // CO1 & CO4: Enum validation & custom exception
                        VehicleType type = VehicleType.valueOf(typeStr);
                        Vehicle vehicle = new Vehicle(plate, type);
                        system.parkVehicle(vehicle);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Validation Error: Invalid vehicle type! Only 'CAR' or 'BIKE' permitted.");
                    } catch (ParkingFullException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 2: // Vehicle Exit
                    System.out.print("Enter License Plate to unpark: ");
                    String unparkPlate = scanner.nextLine().trim();
                    try {
                        system.removeVehicle(unparkPlate);
                    } catch (VehicleNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                    break;

                case 3: // Real-time Status
                    system.showAllStatus();
                    break;

                case 4: // 2-D Visual Grid
                    system.displayVisualGrids();
                    break;

                case 5: // Search
                    System.out.print("Enter License Plate to search: ");
                    String searchPlate = scanner.nextLine().trim();
                    system.searchVehicle(searchPlate);
                    break;

                case 6: // Sorting & Streams
                    system.displaySortedParkedVehicles();
                    break;

                case 7: // Recursion
                    system.runRecursiveSpotAudit();
                    break;

                case 8: // StringBuilder Report
                    System.out.print(system.generateDetailedReport());
                    break;

                case 9: // Save File
                    system.saveRecordsToFile(dataFilename);
                    break;

                case 10: // Load File
                    system.loadRecordsFromFile(dataFilename);
                    break;

                case 11: // Exit
                    running = false;
                    System.out.println("\nWould you like to auto-save before shutting down? (Y/N): ");
                    String saveChoice = scanner.nextLine().trim();
                    if (saveChoice.equalsIgnoreCase("Y")) {
                        system.saveRecordsToFile(dataFilename);
                    }
                    System.out.println("System shut down successfully. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option! Please select a valid number from 1 to 11.");
            }
        }

        scanner.close();
    }
}
