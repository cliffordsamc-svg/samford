package com.scc.ui;

import com.scc.dao.UserDao;
import com.scc.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final Scanner scanner;
    private final UserDao userDao;
    private final User admin;

    public AdminMenu(Scanner scanner, UserDao userDao, User admin) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.admin = admin;
    }

    public void run() {
        System.out.println("\nWelcome, " + admin.username() + " (ADMIN)");

        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> viewAllUsers();
                case "2" -> viewPendingUsers();
                case "3" -> approveUser();
                case "4" -> {
                    System.out.println("Logged out successfully.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAllUsers() {
        try {
            System.out.println("\n--- All System Users ---");
            printUsers(userDao.getAllUsers());
        } catch (SQLException e) {
            System.out.println("Could not load users: " + e.getMessage());
        }
    }

    private void viewPendingUsers() {
        try {
            System.out.println("\n--- Pending Registrations ---");
            printUsers(userDao.getPendingUsers());
        } catch (SQLException e) {
            System.out.println("Could not load pending users: " + e.getMessage());
        }
    }

    private void approveUser() {
        List<User> pending;
        try {
            pending = userDao.getPendingUsers();
        } catch (SQLException e) {
            System.out.println("Could not load pending users: " + e.getMessage());
            return;
        }

        System.out.println("\n--- Approve User Account ---");
        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }
        printUsers(pending);

        while (true) {
            System.out.print("Enter Target User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();

            int targetId;
            try {
                targetId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (targetId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }
            if (!isInList(pending, targetId)) {
                System.out.println("ID " + targetId + " is not in the pending list. Try again.");
                continue;
            }

            try {
                if (userDao.approveUser(targetId)) {
                    System.out.println("User ID " + targetId + " has been APPROVED.");
                } else {
                    System.out.println("Approval failed. No account was updated.");
                }
            } catch (SQLException e) {
                System.out.println("Approval failed: " + e.getMessage());
            }
            return;
        }
    }

    private boolean isInList(List<User> users, int id) {
        for (User user : users) {
            if (user.id() == id) {
                return true;
            }
        }
        return false;
    }

    /** Prints any list of users as an aligned table. */
    private void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        String border = "+" + "-".repeat(7) + "+" + "-".repeat(17) + "+" + "-".repeat(17) + "+" + "-".repeat(12) + "+";
        String rowFormat = "| %-5s | %-15s | %-15s | %-10s |%n";

        System.out.println(border);
        System.out.printf(rowFormat, "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        for (User user : users) {
            System.out.printf(rowFormat, user.id(), user.username(), user.roles(), user.status());
        }
        System.out.println(border);
        System.out.println("Total: " + users.size());
    }
}