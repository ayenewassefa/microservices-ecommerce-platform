import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Button, Alert, Spinner } from 'react-bootstrap';
import api from '../services/api';

function CreateProduct() {
    const navigate = useNavigate();

    const [productName, setProductName] = useState('');
    const [productDescription, setProductDescription] = useState('');
    const [productPrice, setProductPrice] = useState('');
    const [stock, setStock] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        setLoading(true);
        setError('');

        try {
            await api.post('/product', {
                productName,
                productDescription,
                productPrice: parseFloat(productPrice),
                stock: parseInt(stock, 10) || 0
            });
            navigate('/dashboard', { state: { message: ' Product created successfully!' } });
        } catch (err) {
            setError('Failed to create product. Please try again.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="mx-auto" style={{ maxWidth: '600px' }}>
            <h2 className="mb-4">Create New Product</h2>

            {error && <Alert variant="danger">{error}</Alert>}

            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Product Name *</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="Enter product name"
                        value={productName}
                        onChange={(e) => setProductName(e.target.value)}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Description</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={3}
                        placeholder="Enter description"
                        value={productDescription}
                        onChange={(e) => setProductDescription(e.target.value)}
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Price ($) *</Form.Label>
                    <Form.Control
                        type="number"
                        step="0.01"
                        placeholder="0.00"
                        value={productPrice}
                        onChange={(e) => setProductPrice(e.target.value)}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Stock (Quantity) *</Form.Label>
                    <Form.Control
                        type="number"
                        step="1"
                        placeholder="0"
                        value={stock}
                        onChange={(e) => setStock(e.target.value)}
                        required
                    />
                </Form.Group>

                <Button 
                    variant="primary" 
                    type="submit" 
                    disabled={loading}
                    className="w-100"
                >
                    {loading ? (
                        <>
                            <Spinner animation="border" size="sm" className="me-2" />
                            Creating...
                        </>
                    ) : (
                        'Create Product'
                    )}
                </Button>
            </Form>
        </div>
    );
}

export default CreateProduct;