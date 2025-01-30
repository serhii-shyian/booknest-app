-- Remove test cart item
DELETE FROM cart_items WHERE shopping_cart_id = 2;
-- Remove all shopping carts
DELETE FROM shopping_carts;
-- Remove all users
DELETE FROM users;
-- Remove all books
DELETE FROM books;
