import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Button, Alert, Spinner } from 'react-bootstrap';
import api from '../services/api';

function CreateOrder() {
    const navigate = useNavigate();

    const [products, setProducts] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState('');
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(false);
    const [fetchLoading, setFetchLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            setFetchLoading(true);
            const response = await api.get('/product');
            setProducts(response.data);
            if (response.data.length > 0) {
                setSelectedProductId(response.data[0].id);
            }
        } catch (err) {
            setError('Failed to load products. Please refresh.');
        } finally {
            setFetchLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!selectedProductId) {
            setError('Please select a product.');
            return;
        }
        if (quantity < 1) {
            setError('Quantity must be at least 1.');
            return;
        }

        setLoading(true);
        setError('');

        try {
            await api.post('/order', {
                orderLineItemsDtoList: [
                    {
                        productId: selectedProductId,
                        quantity: quantity
                    }
                ]
            });
            navigate('/dashboard', { state: { message: ' Order placed successfully! Stock updated!' } });
        } catch (err) {
            const errorMessage = err.response?.data?.message || 'Failed to place order. Please try again.';
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    if (fetchLoading) {
        return (
            <div className="text-center mt-5">
                <Spinner animation="border" variant="primary" />
                <p className="mt-2">Loading products...</p>
            </div>
        );
    }

    return (
        <div className="mx-auto" style={{ maxWidth: '600px' }}>
            <h2 className="mb-4">Place a New Order</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            {products.length === 0 ? (
                <Alert variant="warning">No products available to order.</Alert>
            ) : (
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Select Product *</Form.Label>
                        <Form.Select
                            value={selectedProductId}
                            onChange={(e) => setSelectedProductId(e.target.value)}
                            required
                        >
                            {products.map((product) => (
                                <option key={product.id} value={product.id}>
                                    {product.productName} - ${product.productPrice} (Stock: {product.stock || 0})
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
                        {loading ? (
                            <>
                                <Spinner animation="border" size="sm" className="me-2" />
                                Placing Order...
                            </>
                        ) : (
                            'Place Order'
                        )}
                    </Button>
                </Form>
            )}
        </div>
    );
}

export default CreateOrder;