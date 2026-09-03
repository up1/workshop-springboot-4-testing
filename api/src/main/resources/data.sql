INSERT INTO customers (customer_id, name, email, phone) VALUES
    (1, 'Alice Example', 'alice@example.com', '555-0100'),
    (2, 'Bob Example', 'bob@example.com', '555-0200')
ON CONFLICT (customer_id) DO NOTHING;

INSERT INTO products (product_id, name, price, stock) VALUES
    (1, 'Widget', 20.00, 100),
    (2, 'Gadget', 60.00, 50)
ON CONFLICT (product_id) DO NOTHING;
