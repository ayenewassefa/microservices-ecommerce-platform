import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Form, Button, Alert, Spinner } from 'react-bootstrap';
import api from '../services/api';

function EditProduct() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [productName, setProductName] = useState('');
    const [productDescription, setProductDescription] = useState('');
    const [productPrice, setProductPrice] = useState('');
    const [stock, setStock] = useState('');
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const response = await api.get(`/product/${id}`);
                const p = response.data;
                setProductName(p.productName);
                setProductDescription(p.productDescription || '');
                setProductPrice(p.productPrice);
                setStock(p.stock);
                setLoading(false);
            } catch (err) {
                setError('Failed to load product details.');
                setLoading(false);
            }
        };
        fetchProduct();
    }, [id]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError('');

        try {
            await api.put(`/product/${id}`, {
                productName,
                productDescription,
                productPrice: parseFloat(productPrice),
                stock: parseInt(stock, 10) || 0
            });
            navigate('/dashboard', { state: { message: ' Product updated successfully!' } });
        } catch (err) {
            setError('Failed to update product. Please try again.');
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className="text-center mt-5">
                <Spinner animation="border" variant="primary" />
                <p className="mt-2">Loading product...</p>
            </div>
        );
    }

    return (
        <div className="mx-auto" style={{ maxWidth: '600px' }}>
            <h2 className="mb-4">✏️ Edit Product</h2>
            {error && <Alert variant="danger">{error}</Alert>}
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3">
                    <Form.Label>Product Name *</Form.Label>
                    <Form.Control
                        type="text"
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
                        value={productDescription}
                        onChange={(e) => setProductDescription(e.target.value)}
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Price ($) *</Form.Label>
                    <Form.Control
                        type="number"
                        step="0.01"
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
                        value={stock}
                        onChange={(e) => setStock(e.target.value)}
                        required
                    />
                </Form.Group>

                <Button 
                    variant="primary" 
                    type="submit" 
                    disabled={submitting}
                    className="w-100"
                >
                    {submitting ? 'Updating...' : 'Update Product'}
                </Button>
            </Form>
        </div>
    );
}

export default EditProduct;