import React from 'react'

const Login = () => {
  return (
    <div className="flex flex-col justify-center items-center">
        <h3 className="text-4xl">Login</h3>
        <form className='flex flex-col gap-2 w-2/3 p-3'>
            <input type="text" className="bg-white border-2 border-black rounded-2xl text-black px-1 py-2"/>
            <input type="password" className="bg-white border-2 border-black rounded-2xl text-black px-1 py-2"/>
        </form>
    </div>
  )
}

export default Login