import React, { useState, useEffect } from 'react';
import { Navbar, Nav, Container, Button, NavDropdown } from 'react-bootstrap';
import { useNavigate, useLocation } from 'react-router-dom';
import { isAuthenticated, logout, getUserName } from '../services/auth';

function NavBar() {
    const navigate = useNavigate();
    const location = useLocation();
    const [userName, setUserName] = useState('');

    useEffect(() => {
        if (isAuthenticated()) {
            setUserName(getUserName() || 'User');
        } else {
            setUserName('');
        }
    }, [location]);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <Navbar bg="dark" variant="dark" expand="lg" className="shadow-sm">
            <Container>
                {}
                <Navbar.Brand 
                    onClick={() => navigate('/')} 
                    style={{ cursor: 'pointer', fontWeight: 'bold' }}
                >
                     MicroShop
                </Navbar.Brand>
                
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto">
                        {}
                        <Nav.Link onClick={() => navigate('/')}>Home</Nav.Link>
                        <Nav.Link onClick={() => navigate('/checkout')}>Checkout</Nav.Link>

                        {}
                        {isAuthenticated() && (
                            <>
                                <Nav.Link onClick={() => navigate('/dashboard')}>Dashboard</Nav.Link>
                                <Nav.Link onClick={() => navigate('/create-order')}>New Order</Nav.Link>
                                <Nav.Link onClick={() => navigate('/create-product')}>Add Product</Nav.Link>
                            </>
                        )}
                    </Nav>
                    
                    {isAuthenticated() ? (
                        <Nav>
                            <NavDropdown 
                                title={`welcome ${userName}`} 
                                id="basic-nav-dropdown"
                                align="end"
                            >
                                <NavDropdown.Item onClick={() => navigate('/dashboard')}>
                                    Dashboard
                                </NavDropdown.Item>
                                <NavDropdown.Divider />
                                <NavDropdown.Item onClick={handleLogout}>
                                    Logout
                                </NavDropdown.Item>
                            </NavDropdown>
                        </Nav>
                    ) : (
                        <Button variant="outline-light" onClick={() => navigate('/login')}>
                            Admin Login
                        </Button>
                    )}
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavBar;