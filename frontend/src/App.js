import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import Login from './components/Login';
import ProductList from './components/ProductList';
import CreateProduct from './components/CreateProduct';
import CreateOrder from './components/CreateOrder';
import NavBar from './components/NavBar';
import { isAuthenticated } from './services/auth';
import EditProduct from './components/EditProduct';
import PublicProductList from './components/PublicProductList';
import GuestCheckout from './components/GuestCheckout';
import OrderConfirmation from './components/OrderConfirmation';

function PrivateRoute({ children }) {
    return isAuthenticated() ? children : <Navigate to="/login" />;
}

function App() {
    return (
        <BrowserRouter>
            <NavBar />
            <Container className="mt-4" style={{ minHeight: '80vh' }}>
                <Routes>
                    {}
                    <Route path="/" element={<PublicProductList />} />
                    <Route path="/checkout" element={<GuestCheckout />} />
                    <Route path="/confirmation" element={<OrderConfirmation />} />
                    <Route path="/login" element={<Login />} />

                    {}
                    <Route path="/dashboard" element={<PrivateRoute><ProductList /></PrivateRoute>} />
                    <Route path="/create-product" element={<PrivateRoute><CreateProduct /></PrivateRoute>} />
                    <Route path="/create-order" element={<PrivateRoute><CreateOrder /></PrivateRoute>} />
                    <Route path="/edit-product/:id" element={<PrivateRoute><EditProduct /></PrivateRoute>} />
                </Routes>
            </Container>
        </BrowserRouter>
    );
}

export default App;