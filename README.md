# 💳 Loan App

The **Loan App** is a powerful and user-friendly mobile application for managing loan applications. It allows users to log in via **Google** or OTP, store personal and financial data securely in Firebase Realtime Database, and seamlessly manage loan-related details.

## ✨ Features

- 🔑 **Login Options**:
  - 📧 Email and OTP verification via Firebase Authentication.
  - 🟢 **Google Sign-In** for a fast and secure login experience.

- 📋 **User Data Management**:
  - Collect and save essential details:
    - 🎂 Date of Birth (DOB)
    - 🆔 PAN Card Number
    - ⚧ Gender
    - 💼 Profession
    - 💵 Income
    - 📍 Pin Code
    - 🏦 Loan Amount

- 🔗 **Firebase Integration**:
  - Realtime database to securely store and retrieve user data.
  - Easy-to-scale backend for growing user needs.

## 🛠️ Technologies Used

- **Language**:  Kotlin  
- **Backend**: Firebase Authentication and Firebase Realtime Database  
- **UI**: Material Design components   

## 🚀 How It Works

1. **Login**:
   - Users can log in using:
     - OTP verification via Firebase Authentication.
     - Google Sign-In for quick access.

2. **Data Collection**:
   - Users fill out a simple form to provide personal and financial details like DOB, PAN, Gender, etc.

3. **Data Storage**:
   - All data is securely stored in Firebase Realtime Database for easy access and management.

4. **Loan Request**:
   - Based on the data provided, users can request a loan by specifying the desired loan amount.

  ![Login](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Login.png)
  ![Details](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Details.png)
  ![Loan_Amount](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Loan_Amount.png)
  ![DOB](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/DOB.png)
  ![Gender](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Gender.png)
  ![profession](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/profession.png)
  ![Income](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Income.png)
  ![Pancard](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Pancard.png))
  ![Profile](https://github.com/Darshan26B/Loan-App/blob/4e6e0c333ffe8d9d6d3884a92bf49dc27c59121b/Profile.png)
 

## 🔐 Firebase Data Structure

Here’s an example of how user data is stored in Firebase Realtime Database:

```json
{
  "users": {
    "mobile number": {
      "dob": "1995-05-10",
      "pan_card": "ABCDE1234F",
      "gender": "Male",
      "profession": "Software Engineer",
      "income": "75000",
      "loan_amount": "500000",
      "pin_code": "395006"
    }
  }
}


