# Restaurant PDA System - Android App

## Overview

This Android app is a core component of a restaurant PDA (Personal Digital Assistant) system, designed to streamline restaurant operations. The app is developed in Java using Android Studio and is used by restaurant staff to manage and send orders to the kitchen.

The app integrates with a desktop application (written in C# .NET) that runs in the kitchen to display and print orders. Orders and menu data are stored in a SQL database hosted on a XAMPP server, ensuring a centralized management system.

## Features

**Menu Management:** Add new products, categories, and items to the restaurant's menu, including pricing and optional toppings.
**Order Management:** Send customer orders to the kitchen desktop application.
**Configuration:** Configure the kitchen's desktop IP address for communication.
**SQL Database Integration:** Orders and menu data are sent and retrieved from a SQL database hosted on a XAMPP server.
**Real-Time Communication:** Ensures that orders are sent in real time to the kitchen for preparation.

## Requirements

**Android Device:** Running Android 5.0 (Lollipop) or higher.
**Development Environment:** Android Studio.
**Database:** XAMPP server hosting the SQL database.
**Network:** Ensure that both the Android device and the kitchen laptop are connected to the same network for proper communication.

## Installation and Setup

### Set up XAMPP Server

1. Install XAMPP on the designated server (typically the kitchen laptop).
2. Create a MySQL database that will store the menu and orders.
3. Import the required database structure from the provided .sql file.
4. Configure the Kitchen Desktop IP:

### Add Menu Items:

1. Open the Menu Management section.
2. Add categories, products, prices, and optional toppings.

The data is automatically saved to the SQL database on the XAMPP server.

### Sending Orders:

Select items from the menu and send the order.
The order is stored in the SQL database and displayed on the kitchen desktop app.

### SQL Database Integration

The app uses the SQL database hosted on the XAMPP server to store and manage menu items and orders.
The database ensures synchronization between the Android app and the desktop application.

### Configuration

Set the kitchen desktop's IP address within the app.
Ensure proper network connectivity to facilitate communication between the Android app and the SQL database via XAMPP.
This README provides detailed instructions on how to set up and use the Android app for the restaurant PDA system.
