
# 🌱 Friendly Lend App

**Friendly Lend App** is a full-featured web application built with Spring Boot and Thymeleaf that enables users to register, log in, and share items by lending or borrowing them. The app includes item management, favorites, user roles, and a live connection to a separate REST microservice (`ecoSurvey-svc`) that allows users to express their stance on environmental topics like sustainability, recycling, and pollution through interactive surveys.

---

## 📘 About the Project

Friendly Lend is a community-driven lending platform where users can offer and borrow everyday items. The application promotes sustainability and reduced consumption by encouraging item reuse and sharing. Users can interact with items, mark them as favorites, and access personal item lists.

A unique feature of Friendly Lend is its integration with an eco-awareness survey system. Users can explore short, impactful environmental topics such as **SUSTAINABILITY**, **ENVIRONMENT**, and **RECYCLING** — and express their support with just one click. One vote. Real change.

---

### 🌍 Vision & Target Audience

Friendly Lend is targeted at **Generation Z** – active, socially conscious, and digitally connected users who are looking for sustainable and alternative ways to engage with fashion.

Future plans include:
- 💬 In-person item exchanges at popular meeting spots like cafés, campuses, and gyms
- 🎯 Fashion bidding features where users with highly desired items are recognized as trendsetters

---

## 🔔 Notifications & Scheduling

Upon user registration, a default notification is sent explaining that item activity is monitored. If an item remains unborrowed for more than 60 days, a scheduled task automatically sends a warning notification to the item's owner, encouraging them to take action or risk archival of the item.

This system ensures a clean and active catalog of available items on the platform.

---

## 🚀 Features

- 🧾 User registration and login with roles: `USER` and `ADMIN`
- 📦 Add, view, and remove items
- ❤️ Mark items as favorites
- 🌍 Integrated eco-surveys via REST API
- 🔔 Notification system (auto-warnings for inactive items)
- 🕒 Scheduled tasks (check item activity every 60 days)
- 🛒 Item purchase and delivery location modules
- 🧼 Global exception handling

---

## 🔗 Integration with `ecoSurvey-svc` (REST API)

This application connects to a separate REST API (`ecoSurvey-svc`) that enables users to participate in concise ecological surveys directly from the Friendly Lend interface. Topics include:

- **SUSTAINABILITY** – Supporting smart fashion and conscious choices
- **ENVIRONMENT** – Raising awareness about pollution and plastic reduction
- **RECYCLING** – Embracing reuse and reducing textile waste

Votes are submitted via REST communication and stored in the ecoSurvey service, which maintains its own database.

---

## 🛡️ Security

- Role-based access control with custom roles: `USER`, `ADMIN`
- Secure routes using `SecurityFilterChain` configuration
- Custom login/logout and protected endpoints
- `AuthenticationMetadata`: custom `UserDetails` implementation with UUID-based identification and role control
- Fine-grained authorization using `AuthorizationService`:
    - Only users or admins can access sensitive resources
- Method-level access with `@EnableMethodSecurity`

---

## 📂 Package Structure

```
app
├── config             # Spring and security configuration
├── deliveryLocation   # Delivery address logic
├── exception          # Global error handling
├── favorite           # Favorite items
├── item               # Item logic (CRUD)
├── notification       # Notifications
├── purchase           # Purchase logic
├── scheduled          # Scheduled tasks
├── security           # Spring Security implementation
├── survey             # REST client for ecoSurvey API
├── user               # User management and roles
└── web
    ├── dto            # Data Transfer Objects
    └── mapper         # Controllers and mappers
```

---

## 🧰 Tech Stack

- Java 21
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA (Hibernate)
- Spring Security
- MySQL
- REST communication with `RestTemplate`
- Maven

---

## ⚙️ Installation & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/friendly-lend-app.git
   cd friendly-lend-app
   ```

2. Configure your database credentials in `application-secret.properties` (this file is excluded via `.gitignore`):
   ```properties
   spring.datasource.username=your_user
   spring.datasource.password=your_password
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access in browser:
   ```
   http://localhost:8080
   ```

---

## 📸 Screenshots

### 🔐 Authentication


**Register Page**  
![Register](screenshots/register.PNG)

**Login Page**  
![Login](screenshots/login.PNG)

**Index Page (Welcome)**  
![Index](screenshots/index.PNG)

---

### 🏠 Home Pages

**User Home Page**  
![User Home](screenshots/home_user_page.PNG)

**Admin Home Page**  
![Admin Home](screenshots/home_admin.PNG)

---

### 👤 Profile & Notifications

**Profile (Admin View)**  
![Profile Admin](screenshots/profile_admin.PNG)

**Initial Notification in Profile**  
![Notification Profile](screenshots/notification_profile.PNG)

**Scheduled Inactivity Notification**  
![Scheduled Notification](screenshots/notification_scheduled.PNG)

**Delivery Spot Preferences**  
![Delivery Spot](screenshots/my_delivery_spot.PNG)

---

### 🧥 Item Management

**Add Item Form**  
![Add Item](screenshots/add_Item.PNG)

**Offered Items List**  
![Offered Items](screenshots/offered_items_page.PNG)

**Item Zoom Preview**  
![Item Zoom](screenshots/item_zoom.PNG)

**Favorites Page**  
![Favorites](screenshots/favorities_page.PNG)

**My Closet Page**  
![My Closet](screenshots/my_closet_page.PNG)

---

### 🌿 Eco Survey

**Survey Confirmation Page**  
![Confirmation](screenshots/survey_confirmation.PNG)

**My Eco Vote**  
![My Eco Vote](screenshots/user_survey_page.PNG)

---

### ⚙️ Admin Panel

**User Management Page**  
![Users](screenshots/users_page.PNG)

---

## 🛠️ Future Improvements

- Docker support in a separate development branch
- Swagger / OpenAPI documentation
- CI/CD with GitHub Actions
- Pagination and filtering on item lists
- Enhanced UI/UX (theme, mobile support)

---

## 👩‍💻 Author

Developed as a final project in the Advanced Java Web course at SoftUni (2025)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
