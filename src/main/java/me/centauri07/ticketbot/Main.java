package me.centauri07.ticketbot;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        new TicketBot().enable();
        while (true) {
            new Scanner(System.in).next();
        }
    }
}
