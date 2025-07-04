import axios from 'axios'
const request = axios.create(<any>{
    baseURL: '/mosaic',
    timeout: 60000,
})

request.interceptors.request.use(config => {
    // config.headers.token = ''
    return config
}, (error) => Promise.reject(error))

request.interceptors.response.use(response => {
    const res = response.data
    return res
}, (error) => {

    return Promise.reject(error)
})

export default request
