package util;

import model.Reservation;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that handles all file I/O for reservation data.
 * Reservations are persisted as pipe-delimited lines in a text file.
 */
public class FileStorage {

    // The file will be created inside the application's working directory
    // (or the absolute path can be set externally via setFilePath).
    private static String filePath = null;

    /**
     * Allow the servlet context to inject the real absolute path at startup.
     * Call this once from a ServletContextListener or from the first servlet.
     */
    public static void setFilePath(String path) {
        filePath = path;
    }

    public static String getFilePath() {
        return filePath;
    }

    // ---------------------------------------------------------------
    // Internal helper – ensures the storage file exists
    // ---------------------------------------------------------------
    private static Path ensureFile() throws IOException {
        if (filePath == null) {
            throw new IOException("FileStorage path has not been configured.");
        }
        Path p = Paths.get(filePath);
        if (!Files.exists(p)) {
            Files.createDirectories(p.getParent());
            Files.createFile(p);
        }
        return p;
    }

    // ---------------------------------------------------------------
    // Generate the next unique reservation number
    // ---------------------------------------------------------------
    /**
     * Reads all existing reservations and returns the next RES-XXXX number.
     */
    public static synchronized String generateReservationNumber() throws IOException {
        List<Reservation> existing = getAllReservations();
        int max = 0;
        for (Reservation r : existing) {
            String num = r.getReservationNumber(); // e.g. "RES-0023"
            if (num != null && num.startsWith("RES-")) {
                try {
                    int n = Integer.parseInt(num.substring(4));
                    if (n > max) max = n;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("RES-%04d", max + 1);
    }

    // ---------------------------------------------------------------
    // CRUD operations
    // ---------------------------------------------------------------

    /**
     * Reads all reservations from the file and returns them as a list.
     */
    public static List<Reservation> getAllReservations() throws IOException {
        List<Reservation> list = new ArrayList<>();
        Path p = ensureFile();
        for (String line : Files.readAllLines(p)) {
            line = line.trim();
            if (line.isEmpty()) continue;
            Reservation r = Reservation.fromFileString(line);
            if (r != null) list.add(r);
        }
        return list;
    }

    /**
     * Appends a new reservation to the file.
     */
    public static synchronized void saveReservation(Reservation reservation) throws IOException {
        Path p = ensureFile();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(p.toFile(), true))) {
            bw.write(reservation.toFileString());
            bw.newLine();
        }
    }

    /**
     * Searches for a reservation by its reservation number.
     * Returns null if not found.
     */
    public static Reservation findByReservationNumber(String reservationNumber) throws IOException {
        for (Reservation r : getAllReservations()) {
            if (r.getReservationNumber().equalsIgnoreCase(reservationNumber.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Checks whether a given reservation number already exists.
     */
    public static boolean exists(String reservationNumber) throws IOException {
        return findByReservationNumber(reservationNumber) != null;
    }
}
