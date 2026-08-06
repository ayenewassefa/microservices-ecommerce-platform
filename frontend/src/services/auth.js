import axios from 'axios';

const KEYCLOAK_URL = 'http://localhost:8181';
const REALM = 'spring-boot-microservices-realm';
const CLIENT_ID = 'spring-cloud-client';

export const login = async (username, password) => {
    try {
        const response = await axios.post(
            `${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect/token`,
            new URLSearchParams({
                client_id: CLIENT_ID,
                grant_type: 'password',
                username: username,
                password: password,
            }),
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
            }
        );

        const { access_token } = response.data;
        localStorage.setItem('token', access_token);

        return { success: true, data: response.data };
    } catch (error) {
        return {
            success: false,
            message: error.response?.data?.error_description || 'Login failed',
        };
    }
};

export const logout = () => {
    localStorage.removeItem('token');
};

export const isAuthenticated = () => {
    const token = localStorage.getItem('token');
    if (!token) return false;

    try {
        const payloadBase64 = token.split('.')[1];
        const payloadJson = atob(payloadBase64);
        const payload = JSON.parse(payloadJson);

        const currentTimeInSeconds = Math.floor(Date.now() / 1000);
        if (payload.exp && payload.exp < currentTimeInSeconds) {
            localStorage.removeItem('token');
            return false;
        }
        return true;
    } catch (error) {
        localStorage.removeItem('token');
        return false;
    }
};

export const getUserName = () => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
        const payloadBase64 = token.split('.')[1];
        const payloadJson = atob(payloadBase64);
        const payload = JSON.parse(payloadJson);
        return payload.preferred_username || payload.name || 'User';
    } catch (error) {
        return null;
    }
};