import api from './api';

export const getProducts = async () => {
    try {
        const response = await api.get('/api/product');
        return { success: true, data: response.data };
    } catch (error) {
        return {
            success: false,
            message: error.response?.data?.message || 'Failed to fetch products',
        };
    }
};

export const createProduct = async (productData) => {
    try {
        const response = await api.post('/api/product', productData);
        return { success: true, data: response.data };
    } catch (error) {
        return {
            success: false,
            message: error.response?.data?.message || 'Failed to create product',
        };
    }
};