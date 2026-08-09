import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [message, setMessage] = useState('')

  useEffect(() => {
    axios.get('http://localhost:8080/api/hello')
      .then(response => {
        setMessage(response.data)
      })
      .catch(error => {
        console.error('Lỗi gọi API:', error)
        setMessage('Không kết nối được backend')
      })
  }, [])

  return (
    <div>
      <h1>Test kết nối Frontend - Backend</h1>
      <p>{message}</p>
    </div>
  )
}

export default App