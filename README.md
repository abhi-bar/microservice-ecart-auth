# 🛡️ Auth Handler Microservice 

This project is a **production-ready authentication microservice** for modern web applications — built using **Spring Boot**, **JWT with RSA**, **OAuth2 login (Google & GitHub)**, and a **React frontend**.

Designed to be scalable, secure, and easy to integrate into microservice-based architectures.

---

## 🌟 Features

✅ Email/password signup/login with JWT  
✅ OAuth2 login with **Google** and **GitHub**  
✅ JWT token generation using **RSA key pair**  
✅ Role-based access control (`ROLE_USER`, `ROLE_ADMIN`, `ROLE_SELLER`)  
✅ Custom `OAuth2UserService` and `OAuth2SuccessHandler`  
✅ CORS, CSRF protection, and stateless session setup  
✅ Custom refresh token logic out of the box for more robust in day to day uses
✅ React frontend with 3 login options:  
  🔹 Google  
  🔹 GitHub  
  🔹 Email/Password  


---

## 🏗️ Tech Stack

**Backend**  
- Java 21  
- Spring Boot 3.5.3 
- Spring Security 6  
- Spring OAuth2 Client  
- H2 (for dev), PostgreSQL (prod-ready)  
- RSA signed JWT with expiration handling

**Frontend**  
- React (CRA)  
- React Router DOM  
- Axios  
- Styled with custom CSS

---

## 🚀 Quick Start

### 1. Clone this repo

bash
git clone https://github.com/abhi-bar/microservice-auth.git
cd microservice-ecommerce-auth-handler


###2. 🔐 Generate RSA Keys

bash
openssl genrsa -out private.pem 2048
openssl rsa -in private.pem -pubout -out public.pem

Place them in:
auth-handler/src/main/resources/rsa/

###3. ⚙️ Environment Setup
GOOGLE-CLIENT-ID=your-google-client-id
GOOGLE-CLIENT-SECRET=your-google-client-secret

GITHUB-CLIENT-ID=your-github-client-id
GITHUB-CLIENT-SECRET=your-github-client-secret

These are read from application.properties using:
spring.security.oauth2.client.registration.google.client-id=${GOOGLE-CLIENT-ID}

###4. 🧪 Backend Setup
cd auth-handler
./mvnw spring-boot:run


###5. 💻 Frontend Setup
cd oauth-login-ui
npm install
npm start


###6.🧠 Architecture
React UI
  └── Signup + OAuth2 Buttons
      └── Spring Boot (OAuth2 filters)
          ├── JWT (RSA-signed)
          ├── OAuth2SuccessHandler
          ├── OAuth2UserService
          └── User, Role, Token entities



###7. Here are the demo of the pages 


<img width="358" height="272" alt="Screenshot 2025-07-16 at 10 06 04 PM" src="https://github.com/user-attachments/assets/9e48a655-9ddf-4604-b11a-d4179f4843e5" />

<img width="421" height="326" alt="Screenshot 2025-07-16 at 10 06 27 PM" src="https://github.com/user-attachments/assets/fdfd64cc-048f-4e7b-911e-2309c3b5f6d7" />

<img width="824" height="443" alt="Screenshot 2025-07-16 at 10 07 14 PM" src="https://github.com/user-attachments/assets/477af02c-44a3-40e7-b7a8-d5100d981119" />

<img width="448" height="624" alt="Screenshot 2025-07-16 at 10 07 29 PM" src="https://github.com/user-attachments/assets/76779f82-4085-481b-9993-92c9592c58bb" />



