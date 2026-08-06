import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Spinner, Alert, Modal } from 'react-bootstrap';
import { PencilFill, TrashFill } from 'react-bootstrap-icons';
import api from '../services/api';

function ProductList() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();
    const successMessage = location.state?.message;

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [productToDelete, setProductToDelete] = useState(null);
    const [deleting, setDeleting] = useState(false);

    useEffect(() => {
        fetchProducts();
    }, []);

    const fetchProducts = async () => {
        try {
            setLoading(true);
            const response = await api.get('/product');
            setProducts(response.data);
            setError('');
        } catch (err) {
            setError('Failed to load products');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteClick = (id, name) => {
        setProductToDelete({ id, name });
        setShowDeleteModal(true);
    };

    const handleDeleteConfirm = async () => {
        if (!productToDelete) return;
        setDeleting(true);
        try {
            await api.delete(`/product/${productToDelete.id}`);
            setShowDeleteModal(false);
            setProductToDelete(null);
            await fetchProducts(); // Refresh list
        } catch (err) {
            setError('Failed to delete product.');
        } finally {
            setDeleting(false);
        }
    };

    if (loading) {
        return (
            <div className="text-center mt-5">
                <Spinner animation="border" variant="primary" />
                <p className="mt-2">Loading products...</p>
            </div>
        );
    }

    if (error) return <Alert variant="danger">{error}</Alert>;

    return (
        <>
            {successMessage && (
                <Alert variant="success" onClose={() => window.history.replaceState({}, '')} dismissible>
                    {successMessage}
                </Alert>
            )}

            {/* Delete Confirmation Modal */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Confirm Delete</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    Are you sure you want to delete <strong>{productToDelete?.name}</strong>? This action cannot be undone.
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)} disabled={deleting}>
                        Cancel
                    </Button>
                    <Button variant="danger" onClick={handleDeleteConfirm} disabled={deleting}>
                        {deleting ? 'Deleting...' : 'Delete'}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Header with big buttons */}
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>📦 Products</h2>
                <div>
                    <Button 
                        variant="success" 
                        size="lg"
                        className="me-2 fw-bold"
                        onClick={() => navigate('/create-order')}
                        style={{ padding: '10px 24px' }}  
                    >
                        New Order
                    </Button>
                    <Button 
                        variant="primary"
                        size="lg"
                        className="fw-bold"
                        onClick={() => navigate('/create-product')}
                        style={{ padding: '10px 24px' }} 
                    >
                        Add Product
                    </Button>
                </div>
            </div>

            {/* Product Grid */}
            <Row xs={1} md={2} lg={3} className="g-4">
                {products.map((product) => (
                    <Col key={product.id}>
                        <Card className="h-100 shadow-sm">
                            <Card.Body>
                                <Card.Title className="text-truncate">{product.productName}</Card.Title>
                                <Card.Text className="text-muted small">
                                    {product.productDescription || 'No description'}
                                </Card.Text>
                                <Card.Text>
                                    <strong>Price:</strong> ${product.productPrice}
                                </Card.Text>
                                <Card.Text>
                                    <strong>Stock:</strong> 
                                    <span className={product.stock > 0 ? 'text-success' : 'text-danger'}>
                                        {' '}{product.stock}
                                    </span>
                                </Card.Text>
                            </Card.Body>
                            <Card.Footer className="d-flex justify-content-between bg-white border-top-0">
                                <Button 
                                    variant="outline-primary" 
                                    size="sm"
                                    onClick={() => navigate(`/edit-product/${product.id}`)}
                                >
                                    <PencilFill /> Edit
                                </Button>
                                <Button 
                                    variant="outline-danger" 
                                    size="sm"
                                    onClick={() => handleDeleteClick(product.id, product.productName)}
                                >
                                    <TrashFill /> Delete
                                </Button>
                            </Card.Footer>
                        </Card>
                    </Col>
                ))}
            </Row>

            {products.length === 0 && (
                <div className="text-center mt-5">
                    <p className="text-muted">No products available. Click "Add Product" to get started!</p>
                </div>
            )}
        </>
    );
}

export default ProductList;