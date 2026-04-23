import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Manager manager = null;
        boolean firstSequence = true;
        boolean firstOutputInLine = true;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            String command = parts[0];

            try {
                if (command.equals("in")) {
                    if (!firstSequence) {
                        System.out.println();
                    }

                    manager = new Manager();
                    firstSequence = false;
                    firstOutputInLine = true;

                    printResult(manager.getRunningProcessID(), firstOutputInLine);
                    firstOutputInLine = false;
                    continue;
                }

                // if command appears before init, treat as error
                if (manager == null) {
                    printResult(-1, firstOutputInLine);
                    firstOutputInLine = false;
                    continue;
                }

                switch (command) {
                    case "cr": {
                        if (parts.length != 2) {
                            throw new IllegalArgumentException();
                        }

                        int priority = Integer.parseInt(parts[1]);
                        if (priority < 1 || priority > 2) {
                            throw new IllegalArgumentException();
                        }

                        manager.create(priority);
                        printResult(manager.getRunningProcessID(), firstOutputInLine);
                        firstOutputInLine = false;
                        break;
                    }

                    case "de": {
                        if (parts.length != 2) {
                            throw new IllegalArgumentException();
                        }

                        int processID = Integer.parseInt(parts[1]);
                        if (processID < 0 || processID >= 16) {
                            throw new IllegalArgumentException();
                        }

                        if (!manager.destroy(processID)) {
                            throw new IllegalArgumentException();
                        }

                        printResult(manager.getRunningProcessID(), firstOutputInLine);
                        firstOutputInLine = false;
                        break;
                    }

                    case "rq": {
                        if (parts.length != 3) {
                            throw new IllegalArgumentException();
                        }

                        int resourceID = Integer.parseInt(parts[1]);
                        int units = Integer.parseInt(parts[2]);

                        if (resourceID < 0 || resourceID >= 4 || units <= 0) {
                            throw new IllegalArgumentException();
                        }

                        if (!manager.request(resourceID, units)) {
                            throw new IllegalArgumentException();
                        }

                        printResult(manager.getRunningProcessID(), firstOutputInLine);
                        firstOutputInLine = false;
                        break;
                    }

                    case "rl": {
                        if (parts.length != 3) {
                            throw new IllegalArgumentException();
                        }

                        int resourceID = Integer.parseInt(parts[1]);
                        int units = Integer.parseInt(parts[2]);

                        if (resourceID < 0 || resourceID >= 4 || units <= 0) {
                            throw new IllegalArgumentException();
                        }

                        if (!manager.release(resourceID, units)) {
                            throw new IllegalArgumentException();
                        }

                        printResult(manager.getRunningProcessID(), firstOutputInLine);
                        firstOutputInLine = false;
                        break;
                    }

                    case "to": {
                        if (parts.length != 1) {
                            throw new IllegalArgumentException();
                        }

                        manager.timeout();
                        printResult(manager.getRunningProcessID(), firstOutputInLine);
                        firstOutputInLine = false;
                        break;
                    }

                    default:
                        throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                printResult(-1, firstOutputInLine);
                firstOutputInLine = false;
                manager = null; // next valid command should be "in"
            }
        }

        System.out.println();
        scanner.close();
    }

    private static void printResult(int value, boolean firstOutputInLine) {
        if (!firstOutputInLine) {
            System.out.print(" ");
        }
        System.out.print(value);
    }
}