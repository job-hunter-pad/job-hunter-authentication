# Job Hunter Authentication

## Authentication

### Login

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /login | /api/auth/login | POST |

#### Request

```json
{
  "email": "",
  "password": ""
}
```

### Register

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /register | /api/auth/register | POST |

```json
{
  "name": "",
  "email": "",
  "password": "",
  "userType": ""
}
```

### Validate Email

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /validateEmail | /api/auth/validateEmail | POST |

#### Request

```json
{
  "account_key": ""
}
```

#### Response

```json
{
  "message": ""
}
```

The Endpoint will return one of these messages

- "Account validated"
- "Account already validated"
- null message only if the Request `account_key` is empty

## Profile

### Get Profile

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /profile/{userId} | /api/auth/profile/{userId} | GET |

#### Description

Get the Profile of the user with the id of `userId`

#### Request

PathVariable: userId

#### Response

UserProfile

```json
 {
  "userId": "",
  "name": "",
  "email": "",
  "userType": "",
  "location": "",
  "description": "",
  "phoneNumber": "",
  "reviews": [],
  "skills": []
}
```

```java
public enum UserType {
    FREELANCER,
    EMPLOYER
}

```

### Update Profile

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /profile/{userId}/update | /api/auth/profile/{userId}/update | POST |

#### Description

Send a new Profile to be updated for the user with the id of `userId`

#### Request

PathVariable: userId

Request Body: ProfileDTO

```json
{
  "name": "",
  "description": "",
  "location": "",
  "phoneNumber": "",
  "skills": []
}
```

#### Response

UserProfile

```json
 {
  "userId": "",
  "name": "",
  "email": "",
  "userType": "",
  "location": "",
  "description": "",
  "phoneNumber": "",
  "reviews": [],
  "skills": []
}
```

```java
public enum UserType {
    FREELANCER,
    EMPLOYER
}

```

### Get All Profiles

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /profile/all | /api/auth/profile/all | GET |

#### Description

Get a list with all the profiles

#### Response

Array of UserProfile

```json
 [
  {
    "userId": "",
    "name": "",
    "email": "",
    "userType": "",
    "location": "",
    "description": "",
    "phoneNumber": "",
    "reviews": [],
    "skills": []
  }
]
```

### Add Review

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /profile/{userId}/addReview | /api/auth/profile/{userId}/addReview | POST |

#### Description

Send a Review to be added to the user with the id of `userId`

#### Request

PathVariable: userId

RequestBody: Review

```json
{
  "reviewerName": "",
  "description": "",
  "reviewScore": 0
}
```

#### Response

UserProfile

```json
 {
  "userId": "",
  "name": "",
  "email": "",
  "userType": "",
  "location": "",
  "description": "",
  "phoneNumber": "",
  "reviews": [],
  "skills": []
}
```

```java
public enum UserType {
    FREELANCER,
    EMPLOYER
}

```

### Get Profile Photo

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /profile/{userId}/getPhoto | /api/auth/profile/{userId}/getPhoto | GET |

#### Description

Get the profile Photo of the user with the id of `userId`

#### Request

PathVariable: userId

#### Response

Array of bytes that represent the Image

### Update Profile Photo

| URL | API Gateway URL | Method |
| ------ | ------ | ------ |
| /profile/{userId}/updatePhoto | /api/auth/profile/{userId}/updatePhoto | POST |

#### Description

Update the profile Photo for the user with the id of `userId`

Maximum size of photo: **10MB**

#### Request

PathVariable: userId

RequestParam: profilePhoto - MultipartFile file (Form Data)

#### Response

If the Update succeeds then `HttpStatus.OK` will be sent
