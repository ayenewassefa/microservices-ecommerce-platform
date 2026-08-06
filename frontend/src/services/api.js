import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api',
});

api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        const publicPaths = ['/', '/checkout'];
        const currentPath = window.location.pathname;

        if (error.response && error.response.status === 401) {
            if (!publicPaths.includes(currentPath)) {
                localStorage.removeItem('token');
                window.location.href = '/login';
            } else {
                localStorage.removeItem('token');
            }
        }
        return Promise.reject(error);
    }
);

export default api;