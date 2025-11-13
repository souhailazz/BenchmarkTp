#!/usr/bin/env python3
"""
Generate test data CSV files for JMeter
- Categories: 2000 rows (CAT0001 to CAT2000)
- Items: 100,000 rows (~50 items per category)
"""

import csv
import random
from datetime import datetime

# Generate Categories
print("Generating categories.csv...")
with open('jmeter/data/categories.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['id', 'code', 'name'])
    
    for i in range(1, 2001):
        code = f"CAT{i:04d}"
        name = f"Category {i:04d}"
        writer.writerow([i, code, name])

print("[OK] Generated 2000 categories")

# Generate Items
print("Generating items.csv...")
item_names = [
    "Laptop", "Mouse", "Keyboard", "Monitor", "Headset", "Webcam", "Speaker",
    "Tablet", "Smartphone", "Charger", "Cable", "Adapter", "Router", "Switch",
    "Printer", "Scanner", "Microphone", "Camera", "Projector", "Hard Drive",
    "SSD", "RAM", "CPU", "GPU", "Motherboard", "Power Supply", "Case", "Fan",
    "Thermal Paste", "Screwdriver", "USB Hub", "Dock", "Stand", "Bag", "Sleeve",
    "Screen Protector", "Stylus", "Battery", "Card Reader", "Controller",
    "Earbuds", "Smartwatch", "Fitness Tracker", "VR Headset", "Drone",
    "Action Camera", "Gimbal", "Tripod", "Light Ring", "Microphone Stand"
]

with open('jmeter/data/items.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['id', 'name', 'price', 'stock', 'categoryId'])
    
    item_id = 1
    for cat_id in range(1, 2001):
        # Random number of items per category (40-60, avg 50)
        num_items = random.randint(40, 60)
        
        for j in range(num_items):
            name = f"{random.choice(item_names)} {random.randint(1, 999):03d}"
            price = round(random.uniform(9.99, 999.99), 2)
            stock = random.randint(0, 500)
            
            writer.writerow([item_id, name, price, stock, cat_id])
            item_id += 1

print(f"[OK] Generated {item_id-1} items")

# Generate payloads for POST/PUT
print("Generating payload files...")

# Small payload (0.5-1 KB) for categories
categories_post = []
for i in range(100):
    code = f"NEWCAT{i:04d}"
    name = f"New Category {i:04d}"
    categories_post.append(f'{{"code":"{code}","name":"{name}"}}')

with open('jmeter/data/category-payload-light.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['payload'])
    for payload in categories_post:
        writer.writerow([payload])

# Small payload (1 KB) for items
items_post = []
for i in range(100):
    name = f"{random.choice(item_names)} {random.randint(1000, 9999)}"
    price = round(random.uniform(9.99, 999.99), 2)
    stock = random.randint(0, 500)
    cat_id = random.randint(1, 2000)
    items_post.append(f'{{"name":"{name}","price":{price},"stock":{stock},"categoryId":{cat_id}}}')

with open('jmeter/data/item-payload-light.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['payload'])
    for payload in items_post:
        writer.writerow([payload])

# Heavy payload (5 KB) - simulated longer description
heavy_items = []
description_base = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " * 20  # ~1KB
for i in range(100):
    name = f"{random.choice(item_names)} Premium {random.randint(1000, 9999)}"
    description = description_base * 5  # ~5KB
    price = round(random.uniform(99.99, 9999.99), 2)
    stock = random.randint(0, 1000)
    cat_id = random.randint(1, 2000)
    payload = f'{{"name":"{name}","price":{price},"stock":{stock},"categoryId":{cat_id},"description":"{description[:4500]}"}}'
    heavy_items.append(payload)

with open('jmeter/data/item-payload-heavy.csv', 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['payload'])
    for payload in heavy_items:
        writer.writerow([payload])

print("[OK] Generated payload files")
print("\n=== Test Data Generation Complete ===")
print("Files created:")
print("  - jmeter/data/categories.csv (2000 rows)")
print("  - jmeter/data/items.csv (~100,000 rows)")
print("  - jmeter/data/category-payload-light.csv")
print("  - jmeter/data/item-payload-light.csv")
print("  - jmeter/data/item-payload-heavy.csv")

