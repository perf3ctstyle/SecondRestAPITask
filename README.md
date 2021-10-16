# SecondRestAPITask

The system should be extended to expose the following REST APIs:
1. Change single field of gift certificate (e.g. implement the possibility to change only duration of a certificate or only price).
2. Add new entity User.
2.1. implement only get operations for user entity.
3. Make an order on gift certificate for a user (user should have an ability to buy a certificate).
4. Get information about user’s orders.
5. Get information about user’s order: cost and timestamp of a purchase.
6. The order cost should not be changed if the price of the gift certificate is changed.
7. Get the most widely used tag of a user with the highest cost of all orders.
7.1. Create separate endpoint for this query.
8. Search for gift certificates by several tags (“and” condition).
9. Pagination should be implemented for all GET endpoints.
10. Support HATEOAS on REST endpoints.
