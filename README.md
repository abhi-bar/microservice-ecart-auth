# 🛡️ Auth Handler Microservice for E-Commerce

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

```bash
git clone https://github.com/your-username/microservice-ecommerce-auth-handler.git
cd microservice-ecommerce-auth-handler
