package com.scc.ui;

import com.scc.model.User;

import java.util.Scanner;

public class SessionMenu {

    private final Scanner scanner;
    private final User user;

    public SessionMenu(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    public void run() {
        System.out.println("\nWelcome, " + user.username()
                + " | Role: " + user.roles()
                + " | Status: " + user.status());

        while (true) {
            System.out.println("\n1. Logout");
            System.out.print("Choose: ");
            if (scanner.nextLine().trim().equals("1")) {
                System.out.println("Logged out successfully.");
                return;
            }
            System.out.println("Invalid choice.");
        }
    }
}