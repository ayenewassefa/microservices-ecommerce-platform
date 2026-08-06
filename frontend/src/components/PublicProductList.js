import React, { useState, useEffect } from 'react';
import { Card, Row, Col, Container, Spinner, Alert } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

function PublicProductList() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setLoading(true);
                const response = await api.get('/product');
                setProducts(response.data);
                setError('');
            } catch (err) {
                console.error('Failed to fetch products:', err);
                setError('Failed to load products. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []); 

    if (loading) {
        return (
            <div className="text-center mt-5">
                <Spinner animation="border" variant="primary" />
                <p className="mt-2">Loading products...</p>
            </div>
        );
    }

    // Show error message
    if (error) {
        return (
            <Container className="mt-4">
                <Alert variant="danger">{error}</Alert>
            </Container>
        );
    }

    if (products.length === 0) {
        return (
            <Container className="mt-4 text-center">
                <h3> No products available</h3>
                <p className="text-muted">Check back later for new arrivals!</p>
            </Container>
        );
    }

    return (
        <Container>
            <h2 className="text-center my-4"> Our Products</h2>
            <Row xs={1} md={2} lg={3} xl={4} className="g-4">
                {products.map((product) => (
                    <Col key={product.id}>
                        <Card className="h-100 shadow-sm hover-shadow">
                            <Card.Body>
                                <Card.Title className="text-truncate">{product.productName}</Card.Title>
                                <Card.Text className="text-muted small">
                                    {product.productDescription || 'No description available'}
                                </Card.Text>
                                <Card.Text>
                                    <strong>Price:</strong> ${product.productPrice?.toFixed(2) || '0.00'}
                                </Card.Text>
                                <Card.Text>
                                    <strong>Stock:</strong>{' '}
                                    <span className={product.stock > 0 ? 'text-success' : 'text-danger'}>
                                        {product.stock || 0} units
                                    </span>
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Container>
    );
}

export default PublicProductList;