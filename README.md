# MyShoppingListApp
Android app with Google Maps integration using GCP API key.

*MyShoppingListApp with Google Maps Integration*

An Android app built using **Kotlin (Jetpack Compose)** that allows users to create and manage shopping lists while integrating **Google Maps SDK (via GCP API key)** to attach real-time locations to each shopping item.  

---

## 🚀 Features  

- 📋 Add, edit, and delete shopping list items.  
- 📍 Attach **location addresses** to each item using Google Maps API.  
- 🗺️ Interactive map view with draggable markers and location selection.  
- 🌐 Reverse geocoding using **Google Maps Geocoding API**.  
- 🔑 API integration with **Google Cloud Platform (GCP)**.  

---

## ⚙️ Tech Stack  

- **Language** → Kotlin  
- **UI Framework** → Jetpack Compose  
- **APIs & Libraries** →  
  - Google Maps SDK (GCP API key)  
  - Retrofit (for Geocoding API calls)  
  - Android Location Services (FusedLocationProviderClient)  
- **Architecture** → ViewModel + State Management  

---

## 📂 Project Structure  

MyShoppingListApp/
│
├── app/src/main/java/com/example/myshoppinglistapp/
│ ├── MainActivity.kt
│ ├── ShoppingList.kt
│ ├── LocationSelectionScreen.kt
│ ├── LocationUtils.kt
│ ├── LocationViewModel.kt
│ ├── GeocodingApiService.kt
│ ├── RetrofitClient.kt
│ └── LocationData.kt
│
├── app/src/main/res/ # XML resources
├── screenshots/ # App screenshots
└── api_key_sample.txt # Dummy API key instructions

