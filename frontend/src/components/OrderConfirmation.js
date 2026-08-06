import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Container, Card, Button, Alert } from 'react-bootstrap';

function OrderConfirmation() {
    const location = useLocation();
    const navigate = useNavigate();
    const message = location.state?.message;

    return (
        <Container className="mt-5" style={{ maxWidth: '600px' }}>
            <Card className="shadow-lg">
                <Card.Body className="text-center p-5">
                    <h2> Order Confirmed!</h2>
                    {message && (
                        <Alert variant="success" className="mt-3">
                            {message}
                        </Alert>
                    )}
                    <p className="mt-3">Thank you for your order.</p>
                    <Button variant="primary" onClick={() => navigate('/')}>
                        Continue Shopping
                    </Button>
                </Card.Body>
            </Card>
        </Container>
    );
}

export default OrderConfirmation;