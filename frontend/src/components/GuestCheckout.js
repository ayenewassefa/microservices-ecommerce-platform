import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Button, Alert, Container, Card } from 'react-bootstrap';
import api from '../services/api';

function GuestCheckout() {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await api.get('/product');
                setProducts(response.data);
                if (response.data.length > 0) {
                    setSelectedProductId(response.data[0].id);
                }
            } catch (err) {
                setError('Failed to load products.');
            }
        };
        fetchProducts();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            console.log(" Sending order...");
            await api.post('/order', {
                orderLineItemsDtoList: [
                    { productId: selectedProductId, quantity }
                ]
            });
            console.log(" Order successful! Navigating to home...");
            navigate('/confirmation', { 
    state: { message: ' Order placed successfully!' } 
});
        } catch (err) {
            console.error(" Order failed:", err);
            setError('Failed to place order. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container className="mt-5" style={{ maxWidth: '600px' }}>
            <h2 className="text-center mb-4">🛒 Checkout</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            {products.length === 0 ? (
                <Alert variant="warning">No products available.</Alert>
            ) : (
                <Card className="shadow">
                    <Card.Body>
                        <Form onSubmit={handleSubmit}>
                            <Form.Group className="mb-3">
                                <Form.Label>Select Product *</Form.Label>
                                <Form.Select
                                    value={selectedProductId}
                                    onChange={(e) => setSelectedProductId(e.target.value)}
                                    required
                                >
                                    {products.map((p) => (
                                        <option key={p.id} value={p.id}>
                                            {p.productName} - ${p.productPrice} (Stock: {p.stock})
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Quantity *</Form.Label>
                                <Form.Control
                                    type="number"
                                    min="1"
                                    value={quantity}
                                    onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
                                    required
                                />
                            </Form.Group>

                            <Button 
                                variant="success" 
                                type="submit" 
                                disabled={loading}
                                className="w-100"
                            >
                                {loading ? 'Processing...' : 'Place Order'}
                            </Button>
                        </Form>
                    </Card.Body>
                </Card>
            )}
        </Container>
    );
}

export default GuestCheckout;