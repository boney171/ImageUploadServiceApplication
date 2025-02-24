# Synchrony Application

A Spring Boot application that provides authentication, user management, and image uploading features with integration to the Imgur API.

## Overview

Synchrony is built using Spring Boot and follows a layered architecture with clear separation of concerns. It provides REST endpoints for user registration, login (with JWT authentication), user profile retrieval, and image upload to Imgur.

## Prerequisites

- **Java 17** (or later)
- **Gradle** (depending on your build tool)
- Internet connection (for external API calls, e.g., to Imgur)
- An IDE such as IntelliJ IDEA or Eclipse (optional but recommended)

## Building the Application

The project uses Gradle. To build the application, open a terminal in the project root and run:
```bash
./gradlew clean build
```
## Running the Application
```bash
./gradlew bootRun
```
## Testing the Application
```bash
./gradlew test
```


## API Endpoints

### User Registration
- **URL:** `http://localhost:8080/auth/registration`
- **Method:** `POST`
- **Content-Type:** `application/json`

**Request Body Example:**
```json
{
  "username": "admin",
  "password": "password",
  "name": "Tri"
}
```
**Success Response:**
- **Status Code:** `201` Created

**Response Body Example:**
```json
{
  "id": 1,
  "username": "admin",
  "name": "Tri"
}
```
**Error Response**:
- **Status Code**: `409` Conflict
- **Status Code**: `400` Bad Request

### User Login
- **URL:** `http://localhost:8080/auth`
- **Method:** `POST`
- **Content-Type:** `application/json`

**Request Body Example:**
```json
{
  "username": "admin",
  "password": "password"
}
```
**Success Response:**
- **Status Code:** `201` Created
  **Response Body Example:**
```json
{
  "auth_token": "your-jwt-token-here"
}
```
**Error Response**:
- **Status Code**: `404` Not Found - User does not exist
- **Status Code**: `401` Unauthorized - Incorrect password

### Upload Image
- **URL:** `http://localhost:8080/images`
- **Method:** `POST`
- **Content-Type:** `multipart/form-data`
- **Authorization:** `Bearer <auth_token>`

**Request Body (form-data):**

| Field          | Type   | Description              |
|---------------|--------|--------------------------|
| `multipartFile` | file   | Image file to upload    |
| `title`        | text   | Title of the image      |
| `description`  | text   | Description of the image |

**Success Response:**
- **Status Code:** `200` OK

**Response Body Example:**
```json
{
  "id": 1,
  "title": "test",
  "path": "https://i.imgur.com/XtQO2Aq.jpeg",
  "dateUploaded": "2025-02-24T07:41:09.501+00:00"
}
```
**Error Response**:
- **Status Code**: `400` Bad Request - Invalid request parameters
- **Status Code**: `401` Unauthorized - Missing or invalid authentication token
- **Status Code**: `500` Internal Server Error - Failed to upload image to Imgur

### Delete Image
- **URL:** `http://localhost:8080/images/{image_id}`
- **Method:** `DELETE`
- **Authorization:** `Bearer <auth_token>`

**Path Parameter:**

| Parameter | Type  | Description |
|-----------|-------|-------------|
|`image_id` | `Long` |`ID of the image to delete`|

**Success Response:**
- **Status Code:** `200` OK

**Response Body Example:**
```json
{
  "status": "200",
  "message": "Image deleted successfully"
}
```

**Error Response**:
- **Status Code**: `401` Unauthorized – Missing or invalid authentication token
- **Status Code**: `404` Not Found - Image does not exist
- **Status Code**: `500` Internal Server Error - Unable to delete image from Imgur

## Get User Profile

- **URL:** `http://localhost:8080/users/{user_id}/user_profile`
- **Method:** `GET`
- **Authorization:** Requires Bearer Token (JWT)
- **Response Format:** `application/json`

### Path Parameters
| Parameter  | Type    | Description                 |
|------------|--------|-----------------------------|
| `user_id`  | `long` | ID of the user to retrieve  |

### Success Response
- **Status Code:** `200 OK`

**Response Body Example:**
```json
{
  "id": 1,
  "username": "admin",
  "name": "Tri",
  "images": [
    {
      "id": 1,
      "title": "test",
      "path": "https://i.imgur.com/NkARBBt.jpeg",
      "dateUploaded": "2025-02-24T07:41:07.366+00:00"
    },
    {
      "id": 2,
      "title": "test",
      "path": "https://i.imgur.com/XtQO2Aq.jpeg",
      "dateUploaded": "2025-02-24T07:41:09.501+00:00"
    }
  ]
}
```
**Error Response**:
- **Status Code**: `401` Unauthorized – Missing or invalid authentication token


