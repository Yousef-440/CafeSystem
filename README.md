# ☕ CafeSystem  

## 📌 User Registration & Email Verification  

### 🔹 Sign Up Flow  
1. Accepts user data via **`SignUpUserDto`** (name, email, phone number, password).  
2. Checks if the email already exists (**duplicate validation**).  
3. Encrypts the password using **`PasswordEncoder`**.  
4. Assigns a default role: **`USER`**.  
5. Creates a **Verification Token** (UUID + 24h expiry).  
6. Sends a verification email with an activation link.  
7. Returns a structured **ApiResponse** with a welcome message and user details.  

---

### 🔹 Email Verification Flow  
1. Accepts the **token** from the email link.  
2. Validates if the token exists in the database.  
3. Ensures the token is not expired.  
4. If valid → activates the user account (**status = ACTIVE**).  
5. Returns a success message:  

---

### 🔹 DTOs Used  
- **`SignUpUserDto`** → contains only the required fields for registration.  
- **`SignUpUserResponse`** → contains the response data after successful signup.  

---

### 🔹 Endpoints  
- **POST** `/api/v1/user/signup` → Register a new user.  
- **GET** `/api/v1/user/verify?token=UUID` → Verify email and activate the account.  

---
