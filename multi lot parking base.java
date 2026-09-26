import java.util.Scanner;

public class SmartParkingSystem {

    static String[] vehicleNumbers = new String[5];
    static int[] vehicleTypes = new int[5];

    static Scanner scanner = new Scanner(System.in);

    
    static void parkVehicle() {

        System.out.print("Enter vehicle number: ");
        String number = scanner.nextLine();

        System.out.println("Enter vehicle type:");
        System.out.println("1. Car");
        System.out.println("2. Bike");

        int type = scanner.nextInt();
        scanner.nextLine();

        
        for (int i = 0; i < 5; i++) {

            if (vehicleNumbers[i] == null) {

                vehicleNumbers[i] = number;
                vehicleTypes[i] = type;

                System.out.println("Vehicle parked successfully!");
                System.out.println("Parking Spot: " + (i + 1));

                return;
            }
        }

        System.out.println("Sorry! Parking is full.");
    }

    
    static void removeVehicle() {

        System.out.print("Enter vehicle number: ");
        String number = scanner.nextLine();

        for (int i = 0; i < 5; i++) {

            if (vehicleNumbers[i] != null &&
                vehicleNumbers[i].equals(number)) {

                vehicleNumbers[i] = null;
                vehicleTypes[i] = 0;

                System.out.println("Vehicle removed successfully!");
                return;
            }
        }

        System.out.println("Vehicle not found.");
    }

    
    static void displayStatus() {

        System.out.println("\n===== PARKING STATUS =====");

        for (int i = 0; i < 5; i++) {

            System.out.print("Spot " + (i + 1) + ": ");

            if (vehicleNumbers[i] == null) {

                System.out.println("Empty");

            } else {

                System.out.print(vehicleNumbers[i]);

                if (vehicleTypes[i] == 1) {
                    System.out.println(" - Car");
                } else {
                    System.out.println(" - Bike");
                }
            }
        }
    }

    public static void main(String[] args) {

        int choice = 0;

        while (choice != 4) {

            System.out.println("\n===== SMART PARKING SYSTEM =====");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Remove Vehicle");
            System.out.println("3. Display Parking Status");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    parkVehicle();
                    break;

                case 2:
                    removeVehicle();
                    break;

                case 3:
                    displayStatus();
                    break;

                case 4:
                    System.out.println("Thank you for using Smart Parking System!");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}
