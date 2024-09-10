# Restaurant PDA System - Android App
This Android app is part of a restaurant's PDA (Personal Digital Assistant) system, developed in Java using Android Studio. The app allows restaurant staff to manage products, categories, and items, and send orders to the desktop application running in the kitchen. This ensures seamless communication between the waitstaff and the kitchen, improving order management and efficiency.

## Features

### Product Management:
Add, update, and delete products, categories, and items in the system.
Set product prices and customize items with possible toppings or variations.
### Order Management:
Take customer orders and send them directly to the kitchen desktop app.
Modify existing orders in real-time.
### Kitchen Communication:
Orders are instantly sent to the kitchen desktop app, ensuring the kitchen is up-to-date with new and modified orders.
### Simple Configuration:
Configure the kitchen laptop's IP address to establish communication.
Add products, prices, and toppings directly from the app interface.

## Installation

**Clone the repository:**

git clone <repository-url>

**Open in Android Studio:**

**Launch Android Studio.**
Select Open an existing project and navigate to the cloned repository folder.
Sync the Gradle files when prompted.

**Build the Project:**

Connect your Android device via USB or set up an Android emulator.
Click Run in Android Studio to build and install the app on your device.

## Requirements

**Android Version:** Android 6.0 (API level 23) or higher.
**Network Access:** Ensure the Android device and the kitchen desktop are on the same local network.

## Key Features and Modules

**1. Product Management**
**Categories:** Add, update, or remove categories (e.g., Drinks, Appetizers, Main Courses).
**Products:** Manage products under each category with their respective prices and descriptions.
**Toppings/Variations:** Add possible toppings or variations (e.g., extra cheese, different sauces) to individual products.

**2. Order Management**
**Order Taking:** Staff can take customer orders, select products, add any required toppings, and send the order to the kitchen.
**Modify Orders:** Orders can be modified in real-time, and the kitchen will be notified of the changes immediately.

**3. Kitchen Communication**

Orders placed through the app are sent directly to the desktop app in the kitchen via the configured IP address.
The kitchen receives a clear and organized breakdown of each order, including any modifications or special requests.

## Configuration

**1. Set the Kitchen Desktop IP Address**
Go to the Settings>Network Settings section of the app.
Enter the IP address of the kitchen desktop where the PDA system's kitchen app is running.
**Example:** 192.168.1.10

**2. Add Products to the Menu**
Open the Product Management section.
Create a new Category (e.g., Beverages, Starters).
Add Products under each category with their name, price, and possible toppings if needed.
**Example:**
Category: Wraps
Product: Pita
Price: $3.50
Toppings: potatoes, tzatziki, tomato, onions

## Usage

### Add New Products:

Go to the Settings > Products and Categories section.
Add new products or categories as needed.
Update prices, descriptions, and possible toppings for each product.

### Take Orders:

Select a table, choose the products from the menu, and send the order directly to the kitchen.
The app will notify the kitchen desktop app in real-time.

## Troubleshooting

**App Not Communicating with Kitchen:**
Ensure that both the Android device and the kitchen desktop are connected to the same network.
Verify that the correct IP address of the kitchen desktop has been entered in the app's settings.
**Products Not Updating:**
Check the network connection to ensure the app can sync with the PDA system's backend.
Make sure that the product details are correctly filled in, including price and categories.
