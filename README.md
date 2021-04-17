# Job Hunter Authentication


### Mappings

| Service URL | API Gateway URL | Method | Description |
| ------ | ------ | ------ | ------ |
| /login | /api/auth/login | POST | Send Email And Password for validation |
| /register | /api/auth/register | POST | Send Email, Password, Name and User Type |
| /validateEmail | /api/auth/validateEmail | POST | Send Validation token |
| /profile/{userId} | /api/auth/profile/{userId} | GET | Get the userProfile of the user with the id of "userId" |
| /profile/all | /api/auth/profile/all | GET | Get a list with all the profiles |
| /profile/{userId}/addReview | /api/auth/profile/{userId}/addReview | POST | Send a Review to be added to the user with the id of "userId" |
| /profile/{userId}/update | /api/auth/profile/{userId}/update | POST | Send a new Profile to be updated for the user with the id of "userId" |