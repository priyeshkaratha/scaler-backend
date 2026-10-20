#!/usr/bin/env python3
"""
Seed product catalog with realistic data.

Flow:
1) Login to get bearer token
2) Create 10 categories
3) Create 100 products with varied prices across categories
"""

import argparse
import json
import random
import sys
import urllib.error
import urllib.parse
import urllib.request
from dataclasses import dataclass
from typing import List, Optional


@dataclass(frozen=True)
class CategoryTemplate:
    name: str
    brands: List[str]
    nouns: List[str]
    descriptors: List[str]
    min_price: int
    max_price: int


CATEGORY_TEMPLATES = [
    CategoryTemplate(
        name="Smartphones",
        brands=["Apple", "Samsung", "Google", "OnePlus", "Xiaomi"],
        nouns=["Phone", "Pro", "Ultra", "Max", "SE"],
        descriptors=["5G", "OLED", "AI Camera", "Fast Charge", "128GB"],
        min_price=14999,
        max_price=129999,
    ),
    CategoryTemplate(
        name="Laptops",
        brands=["Dell", "HP", "Lenovo", "Apple", "ASUS"],
        nouns=["Notebook", "ProBook", "ThinkPad", "Air", "Creator"],
        descriptors=["16GB RAM", "SSD", "Backlit Keyboard", "Metal Build", "Long Battery"],
        min_price=34999,
        max_price=239999,
    ),
    CategoryTemplate(
        name="Audio",
        brands=["Sony", "Bose", "JBL", "Sennheiser", "Boat"],
        nouns=["Headphones", "Earbuds", "Speaker", "Soundbar", "Studio"],
        descriptors=["Noise Cancelling", "Bluetooth 5.3", "Hi-Res", "Deep Bass", "Water Resistant"],
        min_price=999,
        max_price=39999,
    ),
    CategoryTemplate(
        name="Gaming",
        brands=["Logitech", "Razer", "HyperX", "SteelSeries", "Corsair"],
        nouns=["Mouse", "Keyboard", "Controller", "Headset", "Gamepad"],
        descriptors=["RGB", "Low Latency", "Ergonomic", "Mechanical", "Wireless"],
        min_price=1299,
        max_price=29999,
    ),
    CategoryTemplate(
        name="Home Appliances",
        brands=["LG", "Samsung", "Whirlpool", "IFB", "Panasonic"],
        nouns=["Microwave", "Vacuum", "Purifier", "Mixer", "Air Fryer"],
        descriptors=["Energy Efficient", "Silent", "Smart Control", "Compact", "Family Size"],
        min_price=2499,
        max_price=89999,
    ),
    CategoryTemplate(
        name="Kitchen Essentials",
        brands=["Prestige", "Philips", "Hawkins", "Wonderchef", "Pigeon"],
        nouns=["Cookware Set", "Toaster", "Blender", "Pressure Cooker", "Induction"],
        descriptors=["Non-Stick", "Stainless Steel", "Auto Shutoff", "Durable", "Easy Clean"],
        min_price=499,
        max_price=17999,
    ),
    CategoryTemplate(
        name="Books",
        brands=["HarperCollins", "Penguin", "OReilly", "Oxford", "Bloomsbury"],
        nouns=["Guide", "Workbook", "Handbook", "Primer", "Companion"],
        descriptors=["Beginner Friendly", "Updated Edition", "Illustrated", "Practical", "Best Seller"],
        min_price=199,
        max_price=3999,
    ),
    CategoryTemplate(
        name="Fitness",
        brands=["Nike", "Adidas", "Decathlon", "Puma", "Reebok"],
        nouns=["Yoga Mat", "Dumbbell Set", "Resistance Band", "Kettlebell", "Smart Scale"],
        descriptors=["Premium Grip", "Anti-Slip", "Adjustable", "Lightweight", "Durable"],
        min_price=349,
        max_price=24999,
    ),
    CategoryTemplate(
        name="Furniture",
        brands=["IKEA", "Urban Ladder", "Home Centre", "Nilkamal", "Durian"],
        nouns=["Office Chair", "Study Table", "Bookshelf", "Sofa", "TV Unit"],
        descriptors=["Engineered Wood", "Ergonomic", "Minimal", "Modern", "Space Saving"],
        min_price=1999,
        max_price=89999,
    ),
    CategoryTemplate(
        name="Beauty & Personal Care",
        brands=["LOréal", "Nivea", "Mamaearth", "The Body Shop", "Minimalist"],
        nouns=["Face Wash", "Serum", "Moisturizer", "Shampoo", "Sunscreen"],
        descriptors=["Dermat Tested", "Paraben Free", "Hydrating", "Vitamin C", "Sensitive Skin"],
        min_price=249,
        max_price=5999,
    ),
]


def _request_json(method: str, url: str, payload: Optional[dict] = None, token: Optional[str] = None):
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    data = None if payload is None else json.dumps(payload).encode("utf-8")
    req = urllib.request.Request(url=url, data=data, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            response_body = resp.read().decode("utf-8") if resp.length != 0 else ""
            parsed = json.loads(response_body) if response_body else None
            return resp, parsed
    except urllib.error.HTTPError as err:
        detail = err.read().decode("utf-8", errors="replace")
        raise RuntimeError(f"{method} {url} failed: {err.code} {detail}") from err
    except urllib.error.URLError as err:
        raise RuntimeError(f"Unable to reach {url}: {err}") from err


def login(base_url: str, email: str, password: str) -> str:
    login_url = urllib.parse.urljoin(base_url, "/auth/login")
    resp, _ = _request_json("POST", login_url, {"email": email, "password": password})
    auth_header = resp.headers.get("Authorization")
    if not auth_header or not auth_header.startswith("Bearer "):
        raise RuntimeError("Login succeeded but Authorization bearer token was not returned")
    return auth_header.split(" ", 1)[1].strip()


def signup(base_url: str, email: str, password: str, first_name: str, last_name: str) -> None:
    signup_url = urllib.parse.urljoin(base_url, "/auth/signup")
    _request_json(
        "POST",
        signup_url,
        {
            "email": email,
            "password": password,
            "firstName": first_name,
            "lastName": last_name,
        },
    )


def create_categories(base_url: str, token: str, categories: List[str]) -> None:
    category_url = urllib.parse.urljoin(base_url, "/category")
    for category in categories:
        _, created = _request_json("POST", category_url, {"title": category}, token=token)
        title = created.get("title") if isinstance(created, dict) else category
        print(f"[category] created: {title}")


def price_in_band(min_price: int, max_price: int) -> float:
    raw = random.uniform(min_price, max_price)
    return round(raw, 2)


def make_product_payload(template: CategoryTemplate, index: int) -> dict:
    brand = random.choice(template.brands)
    noun = random.choice(template.nouns)
    descriptor = random.choice(template.descriptors)
    model_code = f"{random.randint(100, 999)}{random.choice(['A', 'X', 'Z', 'Pro', 'Lite'])}"
    title = f"{brand} {noun} {model_code}"
    description = (
        f"{title} with {descriptor}. Designed for everyday use with reliable performance "
        f"and quality materials."
    )
    image_slug = urllib.parse.quote_plus(f"{template.name}-{index}-{brand}-{noun}".lower())
    image_url = f"https://picsum.photos/seed/{image_slug}/640/480"
    return {
        "title": title,
        "description": description,
        "price": price_in_band(template.min_price, template.max_price),
        "category": template.name,
        "imageUrl": image_url,
    }


def create_products(base_url: str, token: str, total_products: int) -> None:
    product_url = urllib.parse.urljoin(base_url, "/products")
    count = 0
    templates_count = len(CATEGORY_TEMPLATES)
    per_category = total_products // templates_count
    remainder = total_products % templates_count

    for i, template in enumerate(CATEGORY_TEMPLATES):
        to_create = per_category + (1 if i < remainder else 0)
        for j in range(to_create):
            payload = make_product_payload(template, count + j + 1)
            _, created = _request_json("POST", product_url, payload, token=token)
            count += 1
            created_title = created.get("title") if isinstance(created, dict) else payload["title"]
            created_price = created.get("price") if isinstance(created, dict) else payload["price"]
            print(f"[product] {count:03d}/{total_products} created: {created_title} (Rs. {created_price})")


def main() -> int:
    parser = argparse.ArgumentParser(description="Seed 10 categories and 100 products into productcatalog API")
    parser.add_argument("--base-url", default="http://localhost:8080", help="API base URL")
    parser.add_argument("--email", required=True, help="Login email of an ADMIN user (see ADMIN_EMAIL)")
    parser.add_argument("--password", required=True, help="Login password")
    parser.add_argument("--signup-if-missing", action="store_true", help="Signup user if login fails (new users are NON_ADMIN_USER and cannot create data)")
    parser.add_argument("--first-name", default="Catalog", help="First name for signup fallback")
    parser.add_argument("--last-name", default="Seeder", help="Last name for signup fallback")
    parser.add_argument("--products", type=int, default=100, help="Number of products to create")
    parser.add_argument("--seed", type=int, default=42, help="Random seed for reproducible data")
    args = parser.parse_args()

    random.seed(args.seed)

    try:
        token = login(args.base_url, args.email, args.password)
    except Exception as err:
        if not args.signup_if_missing:
            print(f"Login failed: {err}", file=sys.stderr)
            return 1
        print(f"Login failed, trying signup: {err}")
        try:
            signup(args.base_url, args.email, args.password, args.first_name, args.last_name)
            token = login(args.base_url, args.email, args.password)
        except Exception as signup_err:
            print(f"Signup/login fallback failed: {signup_err}", file=sys.stderr)
            return 1

    print("Authenticated successfully.")
    create_categories(args.base_url, token, [c.name for c in CATEGORY_TEMPLATES])
    create_products(args.base_url, token, args.products)
    print("Seeding complete.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
