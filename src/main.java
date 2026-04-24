import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class main {
    private static FileWriter outputWriter;

    public static void main(String[] args) {
        String outputFile = "output.txt";

        try {
            Scanner scanner;

            // If input file is provided, read from file.
            // Otherwise, read from terminal/System.in.
            if (args.length >= 1) {
                scanner = new Scanner(new File(args[0]));
            } else {
                scanner = new Scanner(System.in);
            }

            outputWriter = new FileWriter(outputFile);

            manager manager = null;
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
                            outputWriter.write("\n");
                            System.out.println();
                        }

                        manager = new manager();
                        firstSequence = false;
                        firstOutputInLine = true;

                        printResult(manager.getRunningProcessID(), firstOutputInLine);
                        firstOutputInLine = false;
                        continue;
                    }

                    if (manager == null) {
                        throw new IllegalArgumentException();
                    }

                    switch (command) {
                        case "cr": {
                            if (parts.length != 2) throw new IllegalArgumentException();

                            int priority = Integer.parseInt(parts[1]);

                            if (priority < 1 || priority > 2) {
                                throw new IllegalArgumentException();
                            }

                            if (!manager.create(priority)) {
                                throw new IllegalArgumentException();
                            }

                            printResult(manager.getRunningProcessID(), firstOutputInLine);
                            firstOutputInLine = false;
                            break;
                        }

                        case "de": {
                            if (parts.length != 2) throw new IllegalArgumentException();

                            int processID = Integer.parseInt(parts[1]);

                            if (!manager.destroy(processID)) {
                                throw new IllegalArgumentException();
                            }

                            printResult(manager.getRunningProcessID(), firstOutputInLine);
                            firstOutputInLine = false;
                            break;
                        }

                        case "rq": {
                            if (parts.length != 3) throw new IllegalArgumentException();

                            int resourceID = Integer.parseInt(parts[1]);
                            int units = Integer.parseInt(parts[2]);

                            if (!manager.request(resourceID, units)) {
                                throw new IllegalArgumentException();
                            }

                            printResult(manager.getRunningProcessID(), firstOutputInLine);
                            firstOutputInLine = false;
                            break;
                        }

                        case "rl": {
                            if (parts.length != 3) throw new IllegalArgumentException();

                            int resourceID = Integer.parseInt(parts[1]);
                            int units = Integer.parseInt(parts[2]);

                            if (!manager.release(resourceID, units)) {
                                throw new IllegalArgumentException();
                            }

                            printResult(manager.getRunningProcessID(), firstOutputInLine);
                            firstOutputInLine = false;
                            break;
                        }

                        case "to": {
                            if (parts.length != 1) throw new IllegalArgumentException();

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

                    // After error, ignore state until next "in"
                    manager = null;
                }
            }

            outputWriter.write("\n");
            outputWriter.close();
            scanner.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printResult(int value, boolean firstOutputInLine) throws IOException {
        if (!firstOutputInLine) {
            System.out.print(" ");
            outputWriter.write(" ");
        }

        System.out.print(value);
        outputWriter.write(String.valueOf(value));
    }
}